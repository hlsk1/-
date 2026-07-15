package com.aizuche;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AizucheRentalApplication {
    public static void main(String[] args) {
        SpringApplication.run(AizucheRentalApplication.class, args);
        System.out.println("爱租车汽车租赁管理系统启动成功！");
        System.out.println("========================================");
        System.out.println("  首页:     http://localhost:8081/");
        System.out.println("  管理后台: http://localhost:8081/#/admin");
        System.out.println("  用户中心: http://localhost:8081/#/user");
        System.out.println("  默认账号: admin / 123456");
        System.out.println("========================================");
    }
}