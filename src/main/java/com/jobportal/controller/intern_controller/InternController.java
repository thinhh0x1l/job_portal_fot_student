package com.jobportal.controller.intern_controller;

import com.jobportal.dto.request.SendOutCvDto;
import com.jobportal.entity.*;
import com.jobportal.model.*;
import com.jobportal.security.CustomDefaultOidcUser;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.UserService;
import com.jobportal.service.file.StorageService;
import com.jobportal.service.intern.InternService;
import com.jobportal.service.recruiter.CompanyService;
import com.jobportal.service.recruiter.PostJobService;
import com.jobportal.service.recruiter.RecruiterService;
import com.jobportal.utils.CommonUtils;
import com.jobportal.utils.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.jobportal.service.recruiter.PostJobService.HOMEPAGE_INTERN;
import static com.jobportal.service.recruiter.RecruiterService.PAGE_SEEN_OUT_CV;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class InternController {
    StorageService storageService;
    InternService internService;
    RecruiterService recruiterService;
    PostJobService postJobService;
    UserService userService;
    private final PrettyTime p = new PrettyTime(new Locale("vi"));
    private final CompanyService companyService;

    @GetMapping("/dang-cv")
    public String postCv(Model model) {
        model.addAttribute("majors", Major.values());
        model.addAttribute("postCv", new PostCv());
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("p",p);
        model.addAttribute("notification",userService.findByReceiverId());
        model.addAttribute("countNotSeen",userService.countNotSeen());
        return "intern/postcv";

    }

    @GetMapping("/profile")
    public String profile(Model model,Authentication authentication,
                          @RequestParam(value = "userId",defaultValue = "0")int userId) {




        if(authentication == null || (authentication instanceof AnonymousAuthenticationToken))
            return "forward:/403";
        boolean notRoleIntern = authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_INTERN"));
        if(notRoleIntern && userId==0){
            return "forward:/403";
        }
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("notification", userService.findByReceiverId());
        model.addAttribute("countNotSeen", userService.countNotSeen());


        model.addAttribute("userUd",userId);
        model.addAttribute("intern",internService.getInternByIdOrNo(userId));
        model.addAttribute("p", p);
        model.addAttribute("viLocale", new Locale("vi", "VN"));
        return "intern/profile";
    }

    @PostMapping("/dang-cv")
    public String savePostCv(@ModelAttribute("postCv") PostCv postCv,
                             @RequestParam("cvFile")MultipartFile file,Model model) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uploadDir = "photos/post-cvs/" + internService.postCv(postCv,fileName).getId();
        storageService.store(uploadDir, file);
        return "redirect:/homepage";
    }

    @PostMapping("/upload-avatar")
    public String uploadAvatar( @RequestParam("avatar")MultipartFile file){
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uploadDir = "photos/user-photos/" + internService.updateAvatar(fileName);
        if(!file.isEmpty())
            FileUploadUtil.cleanDir(uploadDir);
        FileUploadUtil.saveFile(uploadDir,fileName,file);
        return "redirect:/profile";
    }
    private Integer userId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        CustomDefaultOidcUser custom = null;
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }else if(principal instanceof CustomDefaultOidcUser customDefaultOidcUser) {
            custom = (CustomDefaultOidcUser) customDefaultOidcUser;
        }
        assert custom != null;
        return custom.getUser().getId();
    }

    @GetMapping("/tim-viec/{id}")
    public String postTimViec(@PathVariable Integer id, Model model,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Authentication authentication,
                              HttpServletRequest request) {
        PostJob postJob = recruiterService.getPostJobById(id);

        model.addAttribute("internSeen",recruiterService.findUniqueInternsSentCv(id));
        model.addAttribute("jopPost",postJob);
        model.addAttribute("expire",p.format(postJob.getPostingDeadline()));
        model.addAttribute("p",p);
        model.addAttribute("sendOutCv",new SendOutCv());
        LocalDateTime o = postJob.getTimePost();
        LocalDateTime e = postJob.getPostingDeadline().atStartOfDay();
        Duration max = Duration.between(o, e);
        Duration current = Duration.between(o, LocalDateTime.now());

        model.addAttribute("per",current.toMinutes()*100/max.toMinutes());
        if(authentication!=null && !(authentication instanceof AnonymousAuthenticationToken)) {
            userService.uniqueViewData(userId(authentication),id);
            if(userDetails.getRole().equals(Role.INTERN.name())){
                List<SendOutCv> lsv = internService.findTopByInternId(userDetails.getId()).getContent();
                model.addAllAttributes(internService.internProperties());
                SendOutCv sv = lsv.isEmpty() ? null : lsv.getFirst();
                model.addAttribute("myCV",sv);
                String[] split = request.getRequestURI().split("/");
                model.addAttribute("ex",
                        internService.countByInternIdAndJobPostId(
                        userDetails.getId(), Integer.parseInt(split[split.length - 1])));
                model.addAllAttributes(internService.internProperties());
                model.addAttribute("notification",userService.findByReceiverId());
                model.addAttribute("countNotSeen",userService.countNotSeen());
            }

        }

        return "intern/job-detail";
    }

    @GetMapping("/luutru")
    public String homepage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("p",p);
        Intern intern = internService.getInternWithFavorites(userDetails.getId());
        model.addAttribute("favorites", new HashSet<>(intern.getFavorites()));
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("notification",userService.findByReceiverId());
        model.addAttribute("countNotSeen",userService.countNotSeen());
        return "intern/favorite";
    }
    @PostMapping("/guicv")
    public String sendCv(@ModelAttribute("sendOutCv") SendOutCv sendOutCv,
                         @RequestParam("cvFile")MultipartFile file,
                         @RequestParam("jobId")Integer jobId,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uploadDir = "photos/sendCvs/" + internService.sendOutCv(userDetails.getId(),jobId, sendOutCv,fileName);
        storageService.store(uploadDir, file);

        return "redirect:/homepage";
    }

    @GetMapping("/cv-dagui")
    public String cvsSeen(Model model, @AuthenticationPrincipal CustomUserDetails userDetails,
                          @RequestParam(value = "status",defaultValue = "0")int status) {
        model.addAttribute("status",status);
        model.addAttribute("p",p);
        model.addAttribute("cvs",internService.findTopByInternId2(userDetails.getId(),status));
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("totalCV",internService.getAllCVByInternId());
        model.addAttribute("notification",userService.findByReceiverId());
        model.addAttribute("countNotSeen",userService.countNotSeen());
        return "intern/cvs_seen";
    }

    @GetMapping("/homepage")
    public String homepage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam(value = "district", defaultValue = "0") String district,
                           @RequestParam(value = "major", defaultValue = "0") String major,
                           @RequestParam(value = "salary", defaultValue = "0") String salary,
                           @RequestParam(name = "status", defaultValue = "00") String status,
                           @RequestParam(name = "like", defaultValue = "") String like,
                           @RequestParam(name = "page", defaultValue = "1") int page,
                           @RequestParam(name = "urgent",defaultValue = "0")int urgent) {

        Set<PostJob> postJobs = null;

        if(userDetails != null && userDetails.getRole().equals(Role.INTERN.name())) {
            Intern intern = internService.getInternWithFavorites(userDetails.getId());
            Set<PostJob> favorites = new HashSet<>(intern.getFavorites());
            model.addAttribute("favorites", favorites);
            postJobs = postJobService.relatedJobs(userDetails.getId(),like);
            model.addAttribute("relatedJobs",postJobs);
            model.addAllAttributes(internService.internProperties());
            model.addAttribute("notification",userService.findByReceiverId());
            model.addAttribute("countNotSeen",userService.countNotSeen());
        }

        int totalRelatedJobs = postJobs == null ? 0 : postJobs.size();

        Page<PostJob> pJobs = postJobService.getAll(district,salary,major,like,urgent,postJobs,page);
        long startCount =  (page - 1L) * HOMEPAGE_INTERN + 1;
        long endCount = startCount + HOMEPAGE_INTERN -1;
        if(endCount > pJobs.getTotalElements())
            endCount = pJobs.getTotalElements()  ;

        /// //////// Tổng trang
//        int totalPage = (int)((totalRelatedJobs+pJobs.getTotalElements()+HOMEPAGE_INTERN-1)/HOMEPAGE_INTERN);
//        if(page==1)
//            totalPage = (int)((pJobs.getTotalElements()  +
//                    totalRelatedJobs + HOMEPAGE_INTERN-1) / HOMEPAGE_INTERN);
        /// /////////////////////
        model.addAttribute("p",p);
        model.addAttribute("majors", Major.values());
        model.addAttribute("districts", District.values());
        model.addAttribute("salaries", Salary.values());
        model.addAttribute("status", status);
        model.addAttribute("like", like);
        model.addAttribute("urgent", urgent);
        model.addAttribute("major", major);
        model.addAttribute("district", district);
        model.addAttribute("salary", salary );

        model.addAttribute("postJobs", pJobs);
        model.addAttribute("currentPage",page);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("totalPages",pJobs.getTotalPages());
        model.addAttribute("totalElement",pJobs.getTotalElements());
        return "intern/h";
    }

    @GetMapping("/thong-bao")
    public String notification(Model model,Authentication authentication,
                               @RequestParam(value = "seen",defaultValue = "0")int seen){
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("notification",userService.findByReceiverId());
        model.addAttribute("countNotSeen",userService.countNotSeen());
        model.addAttribute("p",p);
        model.addAttribute("seen",seen);
        return "intern/notification";
    }

    @GetMapping("/chitiet-cv/{id}")
    public String cvDetails(Model model,Authentication authentication,
                            @PathVariable("id")int id){
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("notification",userService.findByReceiverId());
        model.addAttribute("countNotSeen",userService.countNotSeen());
        model.addAttribute("p",p);

        SendOutCv cv = userService.getCvBySotId(id);
        model.addAttribute("cv", cv);
        return "intern/cv-detail";
    }

    @GetMapping("/thong-bao-v3/{id}")
    public String thongBaoV3(Model model,Authentication authentication,
                             @PathVariable("id")int id) {
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("notification", userService.findByReceiverId());
        model.addAttribute("countNotSeen", userService.countNotSeen());
        model.addAttribute("p", p);

        SendOutCv cv = userService.getCvBySotId(id);
        model.addAttribute("cv", cv);
        return "intern/thongbaov3";
    }

    @GetMapping("/nhiem-vu")
    public String tasks(Model model,Authentication authentication,
                        @RequestParam(value = "tab",defaultValue = "tab2") String tab,
                        @RequestParam(value = "page",defaultValue = "1") int page,
                        @RequestParam(value = "status",defaultValue = "") String status,
                        @RequestParam(value = "week",defaultValue = "") String week){
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("notification", userService.findByReceiverId());
        model.addAttribute("countNotSeen", userService.countNotSeen());
        model.addAttribute("p", p);
        model.addAttribute("viLocale", new Locale("vi", "VN"));

        model.addAttribute("tab",tab);
        model.addAttribute("weeks", SecWeeks.values());
        model.addAttribute("week",week);
        model.addAttribute("status",status);
        model.addAttribute("taskStatus", TaskStatus.values());

        Page<Task> sendPage1 = internService.getTaskWithWeedByIntern(page, status,week);
        long startCount = (page - 1L) * HOMEPAGE_INTERN + 1;
        long endCount = startCount + HOMEPAGE_INTERN - 1;
        if (endCount > sendPage1.getTotalElements()) {
            endCount = sendPage1.getTotalElements();
        }

        model.addAttribute("sendPage1", sendPage1.getContent());
        model.addAttribute("currentPage1", page);
        model.addAttribute("startCount1", startCount);
        model.addAttribute("endCount1", endCount);
        model.addAttribute("totalPages1", sendPage1.getTotalPages());
        model.addAttribute("totalElement1", sendPage1.getTotalElements());
        return "intern/tasks";
    }

    @GetMapping("/nhiem-vu/{id}")
    public String taskDetails(Model model,Authentication authentication,
                              @PathVariable("id")Integer id){
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("notification", userService.findByReceiverId());
        model.addAttribute("countNotSeen", userService.countNotSeen());
        model.addAttribute("p", p);
        model.addAttribute("viLocale", new Locale("vi", "VN"));

        model.addAttribute("task",userService.getTaskById(id));
        return "intern/task-detail";
    }

    @PostMapping("/submitReport/{id}")
    public String submitReport(Model model,Authentication authentication,
                               @PathVariable("id")Integer id,
                               @RequestParam("studentComment") String studentComment,
                               @RequestParam("taskFile")MultipartFile file){
        internService.submitReport(id,file,studentComment);
        return "redirect:/nhiem-vu/"+id;
    }
    @PostMapping("/cancelReport/{id}")
    public String cancelReport(Model model,Authentication authentication,
                               @PathVariable("id")Integer id){
        internService.cancelReport(id);
        return "redirect:/nhiem-vu/"+id;
    }

    @GetMapping("/phan-hoi-tuyen-dung")
    public String phanHoiTuyenDung(Model model,Authentication authentication){
        model.addAllAttributes(internService.internProperties());
        model.addAttribute("notification", userService.findByReceiverId());
        model.addAttribute("countNotSeen", userService.countNotSeen());
        model.addAttribute("p", p);
        model.addAttribute("viLocale", new Locale("vi", "VN"));

        model.addAttribute("cvs",internService.recruitedByInternId());
        return "intern/phan-hoi-tuyen-dung";
    }

    @GetMapping("/companies")
    public String companies(Model model,Authentication authentication,
                            @RequestParam(value = "page",defaultValue = "1") int page){
        if(authentication!= null && !(authentication instanceof AnonymousAuthenticationToken)){
            model.addAllAttributes(internService.internProperties());
            model.addAttribute("notification", userService.findByReceiverId());
            model.addAttribute("countNotSeen", userService.countNotSeen());
        }

        model.addAttribute("p", p);
        model.addAttribute("viLocale", new Locale("vi", "VN"));
        model.addAttribute("companies",companyService.getAllCompanies(page,HOMEPAGE_INTERN));
        return "intern/companies";
    }

    @GetMapping("/compy/{id}")
    public String compy(Model model,Authentication authentication,
                        @PathVariable("id")int id){
        if(authentication!= null && !(authentication instanceof AnonymousAuthenticationToken)){
            model.addAllAttributes(internService.internProperties());
            model.addAttribute("notification", userService.findByReceiverId());
            model.addAttribute("countNotSeen", userService.countNotSeen());
            model.addAttribute("user",userService.getUser());
        }
        model.addAttribute("p", p);
        model.addAttribute("viLocale", new Locale("vi", "VN"));


        model.addAttribute("company",internService.getCompany(id));
        return "intern/companeytes";
    }
}

