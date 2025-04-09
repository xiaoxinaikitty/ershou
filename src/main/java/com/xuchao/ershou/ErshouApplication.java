package com.xuchao.ershou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xuchao.ershou.mapper")
public class ErshouApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErshouApplication.class, args);
    }

}
