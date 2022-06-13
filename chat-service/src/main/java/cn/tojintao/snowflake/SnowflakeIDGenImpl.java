package cn.tojintao.snowflake;

import cn.tojintao.snowflake.common.Result;
import cn.tojintao.snowflake.common.Status;
import cn.tojintao.snowflake.common.Utils;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author cjt
 * @date 2022/5/31 23:14
 */
@Slf4j
public class SnowflakeIDGenImpl implements IDGen {
    @Override
    public boolean init() {
        return true;
    }

    private final long twepoch;  //开始时间戳

    private long workerId;  //工作机器ID
    private long sequence = 0L;  //毫秒内序列（0~4095）
    private long lastTimestamp = -1L;   //上次生成ID的时间戳

    private long workerIdBits = 10L; //机器id所占的位数
    private long sequenceBits = 12L;    //序列在id中占的位数

    private long maxWorkerId = -1L ^ (-1L << workerIdBits); //支持的最大机器id
    private long sequenceMask = -1L ^ (-1L << sequenceBits);  //生成序列的掩码

    private long workerIdShift = sequenceBits; //机器ID向左移12位
    private long timestampLeftShift = sequenceBits + workerIdBits; //时间戳向左移22位(12+10)

    private static final Random RANDOM = new Random();

    public SnowflakeIDGenImpl(String zkAddress, int port) {
        //Thu Nov 04 2010 09:42:54 GMT+0800 (中国标准时间)
        this(zkAddress, port, 1288834974657L);
    }

    /**
     * @param zkAddress zk地址
     * @param port      snowflake监听端口
     * @param twepoch   起始的时间戳
     */
    public SnowflakeIDGenImpl(String zkAddress, int port, long twepoch) {
        this.twepoch = twepoch;
        if (System.currentTimeMillis() <= twepoch) {
            throw new RuntimeException("Snowflake not support twepoch gt currentTime");
        }
        final String ip = Utils.getIp();
        SnowflakeZookeeperHolder holder = new SnowflakeZookeeperHolder(ip, String.valueOf(port), zkAddress);
        log.info("twepoch:{} ,ip:{} ,zkAddress:{} port:{}", twepoch, ip, zkAddress, port);
        boolean initFlag = holder.init();
        if (initFlag) {
            workerId = holder.getWorkerID();
            log.info("START SUCCESS USE ZK WORKERID-{}", workerId);
        } else {
            throw new RuntimeException("Snowflake Id Gen is not init ok");
        }
        Preconditions.checkArgument(workerId >= 0 && workerId <= maxWorkerId, "workerID must gte 0 and lte 1023");
    }

    @Override
    public synchronized Result get(String key) {
        long timestamp = System.currentTimeMillis();
        //发生了时钟回拨，此刻时间小于上次发号时间
        if (timestamp < lastTimestamp) {
            long offset = timestamp - lastTimestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = System.currentTimeMillis();
                    if (timestamp < lastTimestamp) {
                        return new Result(-1, Status.EXCEPTION);
                    }
                } catch (InterruptedException e) {
                    log.error("wait interrupted");
                    return new Result(-2, Status.EXCEPTION);
                }
            } else {
                return new Result(-3, Status.EXCEPTION);
            }
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //seq为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = RANDOM.nextInt(100);
        }
        lastTimestamp = timestamp;
        long id = ((timestamp - twepoch) << timestampLeftShift)
                | (workerId << workerIdShift)
                | sequence;
        return new Result(id, Status.SUCCESS);
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public long getWorkerId() {
        return workerId;
    }
}
