package com.jobportal.controller;

import com.jobportal.dto.request.InternDto;
import com.jobportal.entity.Intern;
import com.jobportal.entity.PostJob;
import com.jobportal.entity.Recruiter;
import com.jobportal.service.OnlineService;
import com.jobportal.service.intern.InternService;
import com.jobportal.service.recruiter.RecruiterService;
import com.jobportal.utils.CommonUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jobportal.utils.UrlSafeEncryptorUtil.decrypt;

@Controller
@RequiredArgsConstructor
public class KMAController {
    private final InternService internService;
    private final OnlineService onlineService;
    @GetMapping("/test")
    public String test(Model model) {
        return "test";
    }


    @GetMapping("/online/1")
    @ResponseBody
    public Map<String, InternDto> online1() {
        return onlineService.getInternDtoAccounts();
    }
    @GetMapping("/online/2")
    @ResponseBody
    public Map<String, Recruiter> online2() {
        return onlineService.getRecruiterAccounts();
    }

    @GetMapping("/test1")
    @ResponseBody
    public List<Integer> test1(){
        List<Integer> list = new ArrayList<>();
//        internService.relatedJobs().forEach(
//                i -> list.add(i.getId())
//        );
        return list;
    }
    @GetMapping("/test2")
    @ResponseBody
    public List<Integer> test2(){
        List<Integer> list = new ArrayList<>();
//        internService.relatedJobs().forEach(
//                i -> list.add(i.getId())
//        );
        return list;
    }
}
