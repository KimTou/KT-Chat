package cn.tojintao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author cjt
 * @date 2022/6/8 23:36
 */
@MapperScan("cn.tojintao.mapper")
@EnableOpenApi
@EnableTransactionManagement
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(ChatApplication.class, args);
    }
}
