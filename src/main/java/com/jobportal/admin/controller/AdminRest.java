package com.jobportal.admin.controller;


import com.jobportal.admin.service.AdminService;
import com.jobportal.dto.request.CheckInfo;
import com.jobportal.entity.Company;
import com.jobportal.service.intern.InternService;
import com.jobportal.service.lecturer.LecturerService;
import com.jobportal.service.recruiter.CompanyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/api")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminRest {
    LecturerService lecturerService;
    InternService internService;
    CompanyService companyService;
    AdminService adminService;
    @GetMapping("/student-detail/{id}")
    public ResponseEntity<?> get(@PathVariable int id) {
        return ResponseEntity.ok(internService.getDetailStudent(id));
    }
    @PutMapping("/mark")
    public ResponseEntity<?> markPostJob(){
        adminService.updateSeen();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark/{id}")
    public ResponseEntity<?> markPostJobByOne(@PathVariable Integer id){
        adminService.updateSeenByOne(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/update-student/{id}")
    public ResponseEntity<?> updateInternById(@PathVariable int id){
        return ResponseEntity.ok(internService.getInternById(id));
    }

    @GetMapping("/test")
    public ResponseEntity<?> getAllLecturers() {
        return ResponseEntity.ok(ResponseEntity.ok(internService.getDetailStudent(24)));
    }

    @GetMapping("/company-infomation/{id}")
    public ResponseEntity<?> getCompanyInformation(@PathVariable int id) {
        return ResponseEntity.ok(companyService.getCompanyInformation(id));
    }

    @PutMapping("/update-info")
    public ResponseEntity<?> updateCompanyInformation(@RequestBody CheckInfo checkInfo) {
        companyService.updateInfo(checkInfo);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/update-cer")
    public ResponseEntity<?> updateCompanyCer(@RequestBody CheckInfo checkInfo) {
        companyService.updateCer(checkInfo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/detailJob/{id}")
    public ResponseEntity<?> getDetailJob(@PathVariable int id) {
        return ResponseEntity.ok(adminService.detailPostJobRes(id));
    }

    @PostMapping("/duyetjobPost/{id}")
    public ResponseEntity<?> duyetjobPost(@PathVariable int id,
                                          @RequestParam("status") String status,
                                          @RequestParam("note") String note) {
        adminService.approvePostJob(id, status, note);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-apikey")
    public ResponseEntity<?> getApiKey() {
        return ResponseEntity.ok(adminService.getAisystem().getApiKey());
    }
    @PutMapping("/update-apikey")
    public ResponseEntity<?> updateApikey(@RequestBody String apikey) {
        adminService.updateApiKey(apikey);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-body")
    public ResponseEntity<?> getBody() {
        return ResponseEntity.ok(adminService.getAisystem().getBodyContent());
    }
    @PutMapping("/update-body")
    public ResponseEntity<?> updateBody(@RequestBody String body) {
        adminService.updateBody(body);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/ai-bad")
    public ResponseEntity<?> getBad() {
        adminService.aiBad();
        return ResponseEntity.ok().build();
    }

}
