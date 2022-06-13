package cn.tojintao.snowflake;

import cn.tojintao.snowflake.common.PropertyFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author cjt
 * @date 2022/5/30 22:41
 */
@Slf4j
public class SnowflakeZookeeperHolder {
    private String zk_AddressNode = null; //保存自身的key  ip:port-000000001
    private String listenAddress; //保存自身的key ip:port
    private int workerID;
    private static final String PREFIX_ZK_PATH = "/snowflake/" + PropertyFactory.getProperties().getProperty("spray.name");
    private static final String PATH_FOREVER = PREFIX_ZK_PATH + "/forever";//保存所有数据持久的节点
    private static final String PROP_PATH = System.getProperty("java.io.tmpdir") + File.separator + PropertyFactory.getProperties().getProperty("spray.name") + "/sprayconf/{port}/workerID.properties";
    private String ip;
    private String port;
    private String connectionString;
    private long lastUpdateTime;

    public SnowflakeZookeeperHolder(String ip, String port, String connectionString) {
        this.ip = ip;
        this.port = port;
        this.listenAddress = ip + ":" + port;
        this.connectionString = connectionString;
    }

    private CuratorFramework createWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs) {
        return CuratorFrameworkFactory.builder().connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(connectionTimeoutMs)
                .sessionTimeoutMs(sessionTimeoutMs)
                .build();
    }

    public boolean init() {
        try {
            CuratorFramework curator = createWithOptions(connectionString, new RetryUntilElapsed(1000, 4), 10000, 6000);
            curator.start();
            Stat stat = curator.checkExists().forPath(PATH_FOREVER);
            if (stat == null) {
                //不存在根节点,机器第一次启动,创建/snowflake/ip:port-000000000,并上传数据
                zk_AddressNode = createNode(curator);
                //在本地缓存workerId，默认是0
                updateLocalWorkerID(workerID);
                //定时上报本机时间给forever节点
                ScheduledUploadData(curator, zk_AddressNode);
                return true;
            } else {
                Map<String, Integer> nodeMap = Maps.newHashMap(); //ip:port->00001
                Map<String, String> realMap = Maps.newHashMap(); //ip:port->(ip:port-000001)
                //存在根节点,先检查是否有属于自己的根节点
                List<String> keys = curator.getChildren().forPath(PATH_FOREVER);
                for (String key : keys) {
                    String[] nodeKey = key.split("-");
                    realMap.put(nodeKey[0], key);
                    nodeMap.put(nodeKey[0], Integer.parseInt(nodeKey[1]));
                }
                //获取zk上曾经记录的workerId，这里可以看出workerId的分配是依靠zk的自增序列号
                Integer worker_id = nodeMap.get(listenAddress);
                if (worker_id != null) {
                    //有自己的节点，zk_AddressNode = /snowflake/${leaf.name}/forever+ip:port-0000001
                    zk_AddressNode = PATH_FOREVER + "/" + realMap.get(listenAddress);
                    workerID = worker_id;
                    //检查该节点当前的系统时间是否在最后一次上报时间之后
                    if (!checkInitTimeStamp(curator, zk_AddressNode)) {
                        throw new RuntimeException("init timestamp check error,forever node timestamp gt this node time");
                    }
                    //准备创建临时节点
                    ScheduledUploadData(curator, zk_AddressNode);
                    updateLocalWorkerID(workerID);
                    log.info("[Old NODE]find forever node have this endpoint ip-{} port-{} workerID-{} childNode and start SUCCESS", ip, port, workerID);
                } else {
                    //表示新启动的节点,创建持久节点 ,不用check时间
                    String newNode = createNode(curator);
                    zk_AddressNode = newNode;
                    String[] nodeKey = newNode.split("-");
                    workerID = Integer.parseInt(nodeKey[1]);
                    ScheduledUploadData(curator, zk_AddressNode);
                    updateLocalWorkerID(workerID);
                    log.info("[New NODE]can not find node on forever node that endpoint ip-{} port-{} workerID-{},create own node on forever node and start SUCCESS ", ip, port, workerID);
                }
            }
        } catch (Exception e) {
            log.error("Start node ERROR {}", e);
            try {
                Properties properties = new Properties();
                properties.load(new FileInputStream(new File(PROP_PATH.replace("{port}", port + ""))));
                workerID = Integer.valueOf(properties.getProperty("workerID"));
                log.warn("START FAILED ,use local node file properties workerID-{}", workerID);
            } catch (Exception ex) {
                log.error("Read file error ", ex);
                return false;
            }
        }
        return true;
    }

    /**
     * 在节点文件系统上缓存一个workerID值,当zk失效,机器重启时保证能够正常启动
     *
     * @param workerID
     */
    private void updateLocalWorkerID(int workerID) {
        File confFile = new File(PROP_PATH.replace("{port}", port));
        boolean exists = confFile.exists();
        log.info("file exists status is {}", exists);
        if (exists) {
            try {
                //更新覆盖workerID的缓存文件
                FileUtils.writeStringToFile(confFile, "workerID=" + workerID, false);
                log.info("update file cache workerID is {}", workerID);
            } catch (IOException e) {
                log.error("update file cache error ", e);
            }
        } else {
            //不存在文件,父目录页肯定不存在
            try {
                boolean mkdirs = confFile.getParentFile().mkdirs();
                log.info("init local file cache create parent dis status is {}, workerID is {}", mkdirs, workerID);
                if (mkdirs) {
                    FileUtils.writeStringToFile(confFile, "workerID=" + workerID, false);
                    log.info("local file cache workerID is {}", workerID);
                } else {
                    log.warn("create parent dir error");
                }
            } catch (IOException e) {
                log.warn("create workerID conf file error", e);
            }
        }
    }

    private void ScheduledUploadData(final CuratorFramework curator, final String zk_AddressNode) {
        Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "schedule-upload-time");
                thread.setDaemon(true);
                return thread;
            }
        }).scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                updateNewData(curator, zk_AddressNode);
            }
        }, 1L, 3L, TimeUnit.SECONDS); //每3s上报数据
    }

    private boolean checkInitTimeStamp(CuratorFramework curator, String zk_AddressNode) throws Exception {
        byte[] bytes = curator.getData().forPath(zk_AddressNode);
        Endpoint endpoint = deBuildData(new String(bytes));
        //检查该节点当前的系统时间是否在最后一次上报时间之后
        return endpoint.getTimestamp() <= System.currentTimeMillis();
    }

    /**
     * 创建持久顺序节点,并把节点数据放入value
     *
     * @param curator
     * @return
     * @throws Exception
     */
    private String createNode(CuratorFramework curator) throws Exception {
        try {
            return curator.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(PATH_FOREVER + "/" + listenAddress + "-", buildData().getBytes());
        } catch (Exception e) {
            log.error("create node error msg {} ", e.getMessage());
            throw e;
        }
    }

    private void updateNewData(CuratorFramework curator, String path) {
        try {
            if (System.currentTimeMillis() < lastUpdateTime) {
                return;
            }
            curator.setData().forPath(path, buildData().getBytes());
            lastUpdateTime = System.currentTimeMillis();
        } catch (Exception e) {
            log.info("update init data error path is {} error is {}", path, e);
        }
    }

    /**
     * 构建需要上传的数据
     *
     * @return 上报数据的json字符串
     */
    private String buildData() throws JsonProcessingException {
        Endpoint endpoint = new Endpoint(ip, port, System.currentTimeMillis());
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(endpoint);
        return json;
    }

    private Endpoint deBuildData(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Endpoint endpoint = mapper.readValue(json, Endpoint.class);
        return endpoint;
    }

    /**
     * 上报数据结构
     */
    static class Endpoint {
        private String ip;
        private String port;
        private long timestamp;

        public Endpoint() {
        }

        public Endpoint(String ip, String port, long timestamp) {
            this.ip = ip;
            this.port = port;
            this.timestamp = timestamp;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

    public String getZk_AddressNode() {
        return zk_AddressNode;
    }

    public void setZk_AddressNode(String zk_AddressNode) {
        this.zk_AddressNode = zk_AddressNode;
    }

    public String getListenAddress() {
        return listenAddress;
    }

    public void setListenAddress(String listenAddress) {
        this.listenAddress = listenAddress;
    }

    public int getWorkerID() {
        return workerID;
    }

    public void setWorkerID(int workerID) {
        this.workerID = workerID;
    }
}
