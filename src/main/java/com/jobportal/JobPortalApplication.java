package com.jobportal;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@EnableScheduling
@EnableEncryptableProperties
@FeignClient
public class JobPortalApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(JobPortalApplication.class, args);

    }

}
