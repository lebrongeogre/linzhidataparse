package com.cuit.linzhi;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cuit.linzhi.dao")
public class Starter {
    public static void main(String[] args) {
        SpringApplication.run(Starter.class);
    }

}
