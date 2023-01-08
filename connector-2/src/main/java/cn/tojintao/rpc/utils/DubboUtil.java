package cn.tojintao.rpc.utils;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.utils.SimpleReferenceCache;

/**
 * @author cjt
 * @date 2023/1/8 16:03
 */
public class DubboUtil {
    /**
     * 通过服务提供者ip获取dubbo 服务，不通过zookeeper
     * className provider 的class
     * ip provider
     * port provider
     */
    public static <T> T getDubboService(Class<?> className, String ip, Integer port) {
        String appName = YmlConfigUtil.getConfigByKey("dubbo.application.name");
        return getDubboService(className, String.format("dubbo://%s:%s", ip, port), appName);

    }


    /**
     * 通过服务提供者ip获取dubbo 服务，不通过zookeeper
     * * className provider 的class
     *      * ip provider
     *      * port provider
     *      appName  是dubbo 消费方的应用名
     */
    public static <T> T getDubboService(Class<?> className, String dubboUrl, String appName) {
        //消费者应用名
        ApplicationConfig application = new ApplicationConfig();
        application.setName(appName);

        //获取服务
        ReferenceConfig<T> referenceConfig = new ReferenceConfig<T>();
        referenceConfig.setApplication(application);
        referenceConfig.setInterface(className);
        referenceConfig.setUrl(dubboUrl);
        //超时时间30s
        referenceConfig.setTimeout(30 * 1000);
        //仅仅调用一次
        referenceConfig.setRetries(0);

        SimpleReferenceCache cache = SimpleReferenceCache.getCache();
        return cache.get(referenceConfig);
    }
}
