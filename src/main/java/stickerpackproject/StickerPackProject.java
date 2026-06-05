/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package stickerpackproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {
    "stickerpackproject", "controller", "service", "facade",
    "config", "strategy", "proxy", "impl"
})

@EntityScan("entity")
@EnableJpaRepositories("repository")
@EnableAsync
@EnableRetry
public class StickerPackProject {

    public static void main(String[] args) {
        SpringApplication.run(StickerPackProject.class, args); 
    }
}
