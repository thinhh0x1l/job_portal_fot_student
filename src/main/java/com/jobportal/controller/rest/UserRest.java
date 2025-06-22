package com.jobportal.controller.rest;

import com.jobportal.dto.request.CommentDto;
import com.jobportal.dto.request.CommentUpdate;
import com.jobportal.dto.request.CompaniesReq;
import com.jobportal.service.UserService;
import com.jobportal.service.recruiter.RecruiterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/user/api")
public class UserRest {

    UserService userService;

    @PostMapping("/checkemail")
    public boolean existsByEmail(@RequestBody String email) {
        boolean a = userService.existsByEmail(email);
        System.out.println(a);
        return a;
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> data) {
        String newPassword = data.get("newPassword");
        userService.changePassword(newPassword);
        return ResponseEntity.ok("Đổi thành công");
    }
    @PostMapping("/createComment")
    public ResponseEntity<?> existsByEmail(@RequestBody CommentDto commentDto) {
        userService.createComment(commentDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<?> getReviews(@PathVariable Integer id,
                                        @RequestParam(value = "page",defaultValue = "1")int page,
                                        @RequestParam(value = "star",defaultValue = "0")int star
                                        ) {
        return ResponseEntity.ok(userService.getComments(id,star,page));
    }
    @PatchMapping("/like/{id}")
    public ResponseEntity<?> like(@PathVariable Integer id) {
        userService.checkLike(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-comment")
    public ResponseEntity<?> updateComment(@RequestBody CommentUpdate commentUpdate) {
        userService.updateComment(commentUpdate);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark")
    public ResponseEntity<?> mark() {
        userService.updateSeen();
        return ResponseEntity.ok().build();
    }
    @GetMapping("/get-aisystem")
    public ResponseEntity<?> getAisystem() {
        return ResponseEntity.ok(userService.getAisystem());
    }

    @GetMapping("/get-jobInCompany")
    public ResponseEntity<?> getJobInCompany (@RequestParam("compyId") int compyId,
                                              @RequestParam(value = "page",defaultValue =  "1") int page){
        return ResponseEntity.ok(userService.getJobsInCompany(compyId,page));
    }

    @GetMapping("/get-allCompanies")
    public ResponseEntity<?> getJobInCompany (
            @RequestParam(value = "page",defaultValue = "1")int page,
            @RequestParam(value = "keywords",defaultValue = "")String keywords,
            @RequestParam(value = "filter",defaultValue = "0")String filter,
            @RequestParam(value = "districtCode",defaultValue = "0")String districtCode
            ){
        return ResponseEntity.ok(userService.companiesRes(new CompaniesReq(page,keywords,filter,districtCode)));
    }
}
