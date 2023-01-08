package cn.tojintao.rpc.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author cjt
 * @date 2023/1/8 16:19
 */
@Slf4j
@Component
public class YmlConfigUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext = null;

    private static Environment environment;

    public YmlConfigUtil() {
    }

    public static String getConfigByKey(String key) {
        if (environment ==null){
            YmlConfigUtil.environment = applicationContext.getBean(Environment.class);
        }
        return environment.getProperty(key);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(YmlConfigUtil.applicationContext == null){
            YmlConfigUtil.applicationContext  = applicationContext;
        }
    }
}

