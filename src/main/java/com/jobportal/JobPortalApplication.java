package com.jobportal;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;
import jakarta.annotation.PostConstruct;
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
@EncryptablePropertySource(name = "EncryptedProperties", value = "classpath:application.properties")
public class JobPortalApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(JobPortalApplication.class, args);

    }
    @PostConstruct
    public void debugPort() {
        System.out.println(">>> PORT from Railway = " + System.getenv("PORT"));
    }

}
