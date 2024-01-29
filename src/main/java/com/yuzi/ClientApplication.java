package com.yuzi;

import com.yuzi.sharding.InitializeAfterFlyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

/**
 * Created by YangGuanRong
 * date: 2023/4/10
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Bean
    public ApplicationRunner initializeAfterFlyway() {
        return new InitializeAfterFlyway();
    }

}
