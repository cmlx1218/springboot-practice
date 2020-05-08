package com.cmlx.transaction;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.cmlx.transaction"})
@MapperScan("com.cmlx.transaction.*.persist.mapper")
public class TransactionPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionPracticeApplication.class, args);
    }

}
