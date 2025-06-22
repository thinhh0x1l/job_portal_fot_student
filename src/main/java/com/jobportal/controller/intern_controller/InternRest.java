package com.jobportal.controller.intern_controller;

import com.jobportal.dto.request.*;
import com.jobportal.dto.response.SotResponse;
import com.jobportal.entity.Notification;
import com.jobportal.entity.ProjectIntern;
import com.jobportal.entity.SendOutCv;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.UserService;
import com.jobportal.service.intern.InternService;
import com.jobportal.utils.FileUploadUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/intern/api")
public class InternRest {
    InternService internService;
    UserService userService;
    @PostMapping("/likeJob")
    public boolean addLikeJob(@RequestBody Integer integer,
                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if(customUserDetails == null)
            return false;
        internService.addFavorites(integer);
        return true;
    }

    @DeleteMapping("/likeJob")
    public boolean removeLikeJob(@RequestBody Integer integer,
                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if(customUserDetails == null)
            return false;
        internService.removeFavorite(integer);
        return true;
    }
    @GetMapping("/auth")
    public boolean checkAuth(Authentication authentication ) {
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken)
            return false;
        return true;
    }
    @PostMapping("/guicv")
    public ResponseEntity<Integer> sendCvv(@RequestBody SendOutCvDto sendOutCv,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer i = internService.sendOutCv2(userDetails.getId(), sendOutCv);
        return ResponseEntity.ok(i);
    }

    @GetMapping("/thong-bao-v3/{id}/{nid}")
    public ResponseEntity<?> thongBaoV3(@PathVariable("id")int id,
                                        @PathVariable("nid")int nid) {
        SendOutCv cv = userService.getCvBySotId(id);
        Notification n = userService.findNotificationById(nid);
        SotResponse sotResponse =
                SotResponse.builder()
                        .companyName(cv.getPostJob().getRecruiter().getCompany().getCompanyName())
                        .companyImage(n.getImage())
                        .content(n.getContent())
                        .jobName(cv.getPostJob().getName())
                        .jobUrl("/tim-viec/"+cv.getPostJob().getId())
                        .sotUrl("/chitiet-cv/"+cv.getId())
                        .id(cv.getId())
                        .statusCv(cv.getCvStatus().name())
                        .build();
        return ResponseEntity.ok(sotResponse);
    }

    @PutMapping("/tu-choi-cv/{id}")
    public ResponseEntity<?> tuChoiCv(@PathVariable("id")int id){
        internService.rejectCv(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/chap-nhan-cv/{id}")
    public ResponseEntity<?> chapNhanCv(@PathVariable("id")int id){
        internService.hired(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-company")
    public ResponseEntity<?> checkCompany(){
        return ResponseEntity.ok(internService.checkCompany());
    }

    @GetMapping("/descriptionDetail")
    public ResponseEntity<?> descriptionDetail(@RequestParam(value = "id",defaultValue = "0")int id){
        return ResponseEntity.ok(internService.descriptionDetail(id));
    }
    @PutMapping("/updateDescriptionDetail")
    public ResponseEntity<?> updateDescriptionDetail(@RequestBody UpdateDescriptionDetailRequest updateDescriptionDetailRequest){
        internService.updateDescriptionDetail( updateDescriptionDetailRequest);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/projectInternDetail")
    public ResponseEntity<?> projectInternDetail(@RequestParam(value = "id",defaultValue = "0")int id){
        return ResponseEntity.ok(internService.projectInternDetail(id));
    }
    @GetMapping("/projectInternDetail/{id}")
    public ResponseEntity<?> projectInternDetailById(@PathVariable("id") int idProject){
        return ResponseEntity.ok(internService.projectInternDetailById(idProject));
    }
    @DeleteMapping("/projectInternDetail/{id}")
    public ResponseEntity<?> deleteProjectInternDetailById(@PathVariable("id") int idProject){
        internService.deleteProjectInternDetailById(idProject);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateProjectInternDetail")
    public ResponseEntity<?> updateProjectInternDetail(@RequestBody ProjectInternRequest projectInternRequest){
        internService.updateProjectInternDetail( projectInternRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tagDetail")
    public ResponseEntity<?> tagDetail(@RequestParam(value = "id",defaultValue = "0")int id){
        System.out.println(id);
        return ResponseEntity.ok(internService.getTagDetail(id));
    }
    @PostMapping("/upload-cover")
    public ResponseEntity<?> uploadCover(@RequestParam("file") MultipartFile file){
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uploadDir = "photos/image-cover/" + internService.updateCover(fileName);
        if(!file.isEmpty())
            FileUploadUtil.cleanDir(uploadDir);
        FileUploadUtil.saveFile(uploadDir,fileName,file);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/tagDetail")
    public ResponseEntity<?> updateTagDetail(@RequestBody TagDetailsRequest tagDetailsRequest){
        internService.updateTagDetail(tagDetailsRequest);
        return ResponseEntity.ok().build();
    }



//    @GetMapping
//    public ResponseEntity<?> suggestJobsWithMatchCount(){
//        return ResponseEntity.ok(internService.suggestJobsWithMatchCount());
//    }
}