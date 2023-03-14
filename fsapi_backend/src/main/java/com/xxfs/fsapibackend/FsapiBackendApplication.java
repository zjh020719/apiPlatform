package com.xxfs.fsapibackend;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xxfs.fsapibackend.mapper")
@EnableDubbo
public class FsapiBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FsapiBackendApplication.class, args);
    }

}
