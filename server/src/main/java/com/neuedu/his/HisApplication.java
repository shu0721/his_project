package com.neuedu.his;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 东软云 HIS · 后端服务入口
 *
 * @since 2026-09-19
 */
@SpringBootApplication
@MapperScan("com.neuedu.his.mapper")
public class HisApplication {

    public static void main(String[] args) {
        SpringApplication.run(HisApplication.class, args);
        System.out.println("""

                ============================================================
                  东软云 HIS 后端服务已启动
                  接口地址：http://localhost:8080
                  H2 控制台：http://localhost:8080/h2-console
                ============================================================
                """);
    }
}
