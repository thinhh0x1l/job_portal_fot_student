package com.jobportal.controller;

import com.jobportal.entity.PostJob;
import com.jobportal.entity.Recruiter;
import com.jobportal.repostory.CompanyRepository;
import com.jobportal.repostory.RecruiterRepository;
import com.jobportal.service.recruiter.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final RecruiterService recruiterService;
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${admin.username}")
    private String adminUsername;
    @GetMapping("/recruiter/aa")
    public List<PostJob> index(Authentication auth) {
        if(auth instanceof AnonymousAuthenticationToken) {
            System.out.println("AnonymousAuthenticationToken");
        }
        if (auth == null) {
            System.out.println("Authentication null");
        }
        if (auth != null) {
            System.out.println("Authenticated");
        }
        Recruiter recruiter = recruiterRepository.findById(1).get();
        return recruiter.getPostJobs();
    }

//    @GetMapping("/405")
//    public void trigger405() throws HttpRequestMethodNotSupportedException {
//        throw new HttpRequestMethodNotSupportedException("GET", List.of("POST"));
//    }

}
