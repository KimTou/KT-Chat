package cn.tojintao.common;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author cjt
 * 日志切面
 */
@Component
@Aspect
public class LogAspect {

    private final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * cn.tojintao.controller.*.*(..))")
    public void pointcut(){}

    /**
     * 日志Aop切面
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        //Aop代理类的名字
        String typeName = signature.getDeclaringTypeName();
        //获取拦截的方法名
        String methodName = signature.getName();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Object result = pjp.proceed();
        logger.info("代理类名:{}, 请求的方法名:{}",JSON.toJSONString(typeName), JSON.toJSONString(methodName));
        logger.info("请求的参数信息为:{}", JSON.toJSON(parameterMap));
        logger.info("结果信息为:" + JSON.toJSON(result));
        return result;
    }

    /**
     * @param obj: returning获取目标方法的返回值
     */
    @AfterReturning(value = "pointcut()", returning = "obj")
    public void doAfterReturning(Object obj){
        logger.info("RESPONSE: " + obj);
    }

}
