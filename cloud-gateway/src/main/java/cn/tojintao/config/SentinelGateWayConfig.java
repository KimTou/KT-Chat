package cn.tojintao.config;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.model.dto.ResultInfo;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author cjt
 * @date 2022/6/13 0:09
 */
@Configuration
public class SentinelGateWayConfig {

    public SentinelGateWayConfig() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            //网关对请求进行了限流，就会执行此回调
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                ResultInfo<String> error = ResultInfo.error(CodeEnum.TOO_MANY_REQUEST, "请求过于频繁，请稍后重试");
                String jsonString = JSON.toJSONString(error);
                return ServerResponse.status(CodeEnum.TOO_MANY_REQUEST.getCode())
                        .body(Mono.just(jsonString), String.class);
            }
        });
    }
}
