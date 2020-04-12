package com.yh.storehouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.yh")
@MapperScan(basePackages = {"com.yh.storehouse.mapper"})
public class StorehouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorehouseApplication.class, args);
    }
}
