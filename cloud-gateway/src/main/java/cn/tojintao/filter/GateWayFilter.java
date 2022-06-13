package cn.tojintao.filter;

import cn.tojintao.util.TokenUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author cjt
 * @date 2022/6/7 0:07
 */
@Component
@Slf4j
public class GateWayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求的URI
        String path = exchange.getRequest().getURI().getPath();
        String token = exchange.getRequest().getHeaders().getFirst("token");
        //如果是登录等开放的微服务,则直接放行
        if (path.startsWith("/user-info/user/login") || path.startsWith("/user-info/user/refresh-token")) {
            return chain.filter(exchange); //放行
        }
        /*String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            log.info("token为null");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }
        try {
            TokenUtil.verifyToken(token);
        } catch (TokenExpiredException e) {
            log.info("token为null");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        } catch (Exception e) {
            log.info("token认证失败");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }*/
        return chain.filter(exchange); //传递到过滤链的下一个Filter
    }

    @Override
    public int getOrder() {
        return 0;   //过滤器的顺序, 0表示第一个, 让全局过滤器优先级最高
    }
}