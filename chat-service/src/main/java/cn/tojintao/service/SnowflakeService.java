package cn.tojintao.service;

import cn.tojintao.snowflake.IDGen;
import cn.tojintao.snowflake.SnowflakeIDGenImpl;
import cn.tojintao.snowflake.common.Constants;
import cn.tojintao.snowflake.common.PropertyFactory;
import cn.tojintao.snowflake.common.Result;
import cn.tojintao.snowflake.common.ZeroIDGen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author cjt
 * @date 2021/6/20 20:20
 */
@Service("SnowflakeService")
public class SnowflakeService {
    private Logger logger = LoggerFactory.getLogger(SnowflakeService.class);

    private IDGen idGen;

    public SnowflakeService() throws Exception {
        Properties properties = PropertyFactory.getProperties();
        boolean flag = Boolean.parseBoolean(properties.getProperty(Constants.SPRAY_SNOWFLAKE_ENABLE, "true"));
        if (flag) {
            String zkAddress = properties.getProperty(Constants.SPRAY_SNOWFLAKE_ZK_ADDRESS);
            int port = Integer.parseInt(properties.getProperty(Constants.SPRAY_SNOWFLAKE_PORT));
            idGen = new SnowflakeIDGenImpl(zkAddress, port);
            if(idGen.init()) {
                logger.info("Snowflake Service Init Successfully");
            } else {
                throw new Exception("Snowflake Service Init Fail");
            }
        } else {
            idGen = new ZeroIDGen();
            logger.info("Zero ID Gen Service Init Successfully");
        }
    }

    public Result getId() {
        return idGen.get("key");
    }
}
