package com.jobportal.controller.recruiter_controller;

import com.jobportal.dto.dummy.UpdateCompany;
import com.jobportal.dto.request.AddTaskRequest;
import com.jobportal.dto.request.JobPostRequest;
import com.jobportal.dto.request.PostJobUpdate;
import com.jobportal.dto.response.InternsResponse;
import com.jobportal.entity.PostJob;
import com.jobportal.service.UserService;
import com.jobportal.service.recruiter.PostJobService;
import com.jobportal.service.recruiter.RecruiterService;
import com.jobportal.utils.CommonUtils;
import com.jobportal.utils.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/recruiter/api")
public class RecruiterRest {
    RecruiterService recruiterService;
    UserService userService;
    PostJobService postJobService;


    @PostMapping ("/checkemail")
    public boolean existsByEmail(@RequestBody String email) {
        boolean a = userService.existsByEmail(email);
        System.out.println(a);
        return a;
    }
    @GetMapping("/checkcompany")
    public boolean existsByCompany() {
        boolean a = recruiterService.existsByCompany();
        System.out.println(a);
        return a;
    }

    @PostMapping ("/increaseView")
    public void existsByEmail(@RequestBody Integer id) {
         recruiterService.increaseView(id);
    }

    @PutMapping("/quanly-baidang")
    public ResponseEntity<?> updatePostJob(@RequestBody PostJobUpdate jobUpdate){
        postJobService.updatePostJob(jobUpdate);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark")
    public ResponseEntity<?> markPostJob(){
        userService.updateSeen();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark/{id}")
    public ResponseEntity<?> markPostJobByOne(@PathVariable Integer id){
        userService.updateSeenByOne(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/rejectCv/{id}")
    public ResponseEntity<?> rejectCv(@PathVariable Integer id){

        recruiterService.rejectCv(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/interns")
    public ResponseEntity<?> getInterns(){
        List<InternsResponse> list = new ArrayList<>();
        recruiterService.getInterns().forEach(i -> {
            list.add(InternsResponse.builder()
                            .id(i.getId())
                            .email(i.getEmail())
                            .name(i.getFirstName()+" "+i.getLastName())
                            .image("/images/user-photos/"+i.getId()+"/"+i.getImageUrl())
                    .build());
        });
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/addTask")
    public ResponseEntity<?> addTask(@RequestPart("file") MultipartFile file,
                                     @RequestPart("data") AddTaskRequest data){
        recruiterService.addTask(file,data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-company")
    public ResponseEntity<?> updateCompany(@RequestPart("data") UpdateCompany updateCompany,
                                @RequestPart(value = "avatar", required = false) MultipartFile multipartFile
                                ){
        String fileName = "";
        if(multipartFile!=null)
            fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDir = "photos/companies/" + recruiterService
                .updateCompany(updateCompany,fileName).getId();
        if(multipartFile!=null) {
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        }


        System.out.println(updateCompany.getCompanySize());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/upload-certificate")
    public  ResponseEntity<?> uploadCertificate(@RequestParam("avatar") MultipartFile multipartFile){
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDir = "photos/certificate/" + recruiterService
                .updateCertificate(fileName).getId();
        if(!multipartFile.isEmpty())
            FileUploadUtil.cleanDir(uploadDir);
        FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/postJobChartResponse/{id}")
    public ResponseEntity<?> getPostJobChartResponse(@PathVariable("id") Integer id){
        var data = recruiterService.postJobChartResponse(id);
        System.out.println(data.getTotalCv());
        return ResponseEntity.ok(data);
    }
    @GetMapping("/chartPostJobChart/{by}")
    public ResponseEntity<?> getChartPostJobChart(@PathVariable int by){
        return ResponseEntity.ok(recruiterService.chartPostJob(by));
    }
    @GetMapping("/getAllByRecruiterIdOrderByMajor")
    public ResponseEntity<?> getAllByRecruiterIdOrderByMajor(){
        return ResponseEntity.ok(recruiterService.getAllByRecruiterIdOrderByMajor());
    }
    @GetMapping("/chartPostJob1/{by}")
    public ResponseEntity<?> chartPostJob1(@PathVariable int by){
        return ResponseEntity.ok(recruiterService.chartPostJob1(by));
    }
    @GetMapping("/getAllByRecruiterIdChart")
    public ResponseEntity<?> getAllByRecruiterIdChart(){
        return ResponseEntity.ok(recruiterService.getAllByRecruiterIdChart());
    }
    @GetMapping("/viewCharts")
    public ResponseEntity<?> viewCharts(){
        return ResponseEntity.ok(recruiterService.viewCharts());
    }

    @PostMapping("/create-job-post")
    public ResponseEntity<?> createNewJobPost(@RequestBody JobPostRequest postJob){
        recruiterService.createPostJob(postJob);
        return ResponseEntity.ok().build();
    }
    @GetMapping ("/get-all-tags-to-update")
    public ResponseEntity<?> createNewJobPost(){
        return ResponseEntity.ok(recruiterService.getAllTagsToUpdate());
    }
}
