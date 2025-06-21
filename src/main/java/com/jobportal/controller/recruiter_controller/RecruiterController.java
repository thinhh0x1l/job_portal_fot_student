package com.jobportal.controller.recruiter_controller;


import com.jobportal.dto.dummy.Point;
import com.jobportal.dto.dummy.ShowViewPj;
import com.jobportal.dto.dummy.UpdateCompany;
import com.jobportal.dto.request.PostJobUpdate;
import com.jobportal.dto.request.TaskRequest;
import com.jobportal.entity.*;
import com.jobportal.model.*;
import com.jobportal.repostory.RecruiterRepository;
import com.jobportal.service.UserService;
import com.jobportal.service.file.StorageService;
import com.jobportal.service.recruiter.CompanyService;
import com.jobportal.service.recruiter.PostJobService;
import com.jobportal.service.recruiter.RecruiterService;
import com.jobportal.utils.CommonUtils;
import com.jobportal.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.jobportal.service.recruiter.PostJobService.HOMEPAGE_INTERN;
import static com.jobportal.service.recruiter.RecruiterService.PAGE_SEEN_OUT_CV;

@Controller
@RequestMapping("/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final PrettyTime p = new PrettyTime(new Locale("vi"));

    private final RecruiterService recruiterService;
    private final CompanyService companyService;
    private final StorageService storageService;
    private final PostJobService postJobService;
    private final UserService userService;

    @GetMapping("/homepage")
    public String homepage(Authentication authentication, Model model) {
        proProperties(model, authentication);
        model.addAttribute("three",recruiterService.getThreeInternLasted());
        model.addAttribute("newPostJobInLastedWeek",recruiterService.newPostJobInLastedWeek());
        model.addAttribute("postJobApprove",recruiterService.postJobApprove());
        model.addAttribute("p",p);
        return "recruiter/homepage";
    }
    private static String getName(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
            return oidcUser.getEmail();
        }
        return authentication.getName();
    }
    private void proProperties(Model model,Authentication authentication) {
        model.addAttribute("companyLevel",recruiterService.companyLevel());
        model.addAttribute("notification",userService.findByReceiverId());
        model.addAttribute("countNotSeen",userService.countNotSeen());
        model.addAttribute("name", getName(authentication));
        model.addAttribute("viLocale", new Locale("vi", "VN"));
        model.addAllAttributes(recruiterService.recruiterProperties());
    }
    @GetMapping("/tao-congty")
    public String createNewCompany(Model model,Authentication authentication) {
        proProperties(model, authentication);
        model.addAttribute("company", new Company());
        model.addAttribute("districts", District.values());

        return "recruiter/create_company";
    }

    @PostMapping("/tao-congty")
    public String createNewCompany(@ModelAttribute("company") Company company,
                                   @RequestParam("companyImage")MultipartFile multipartFile,
                                   RedirectAttributes redirectAttributes) {;
        String filename = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        company.setImageUrl(filename);
        String uploadDir = "photos/companies/"+recruiterService.getRecruiter().getId();
        FileUploadUtil.saveFile(uploadDir,filename,multipartFile);
        recruiterService.createCompany(company);
        redirectAttributes.addFlashAttribute("message","Tạo công ty thành công");
        return "redirect:/recruiter/homepage";
    }

    @GetMapping("/tao-congviec")
    public String createNewJobPost(Model model,Authentication authentication) {
        proProperties(model, authentication);
        model.addAttribute("postJob", new PostJob());
        model.addAttribute("major", Major.values());
        model.addAttribute("p",p);
        model.addAttribute("allTags",userService.getAllTags());
        model.addAttribute("allTagTypes",TagType.values());
        return "recruiter/create_postJob";
    }
//    @PostMapping("/tao-congviec")
//    public String createNewJobPost(@ModelAttribute("postJob") PostJob postJob){
//        recruiterService.createPostJob(postJob);
//        return "redirect:/recruiter/tao-congviec";
//    }
    @PostMapping("/hidden-postjob/{id}")
    public String hiddenPostJob(@PathVariable("id")int id){
        recruiterService.hiddenPostJob(id);
        return "redirect:/recruiter/quanly-baidang";
    }
    @GetMapping("/quanly-baidang")
    public String managePostJobs(Model model,
                                 @RequestParam(value = "like",defaultValue = "") String like ,
                                 @RequestParam(name = "status", defaultValue = "0") int status,
                                 @RequestParam(name = "page", defaultValue = "1")int page,
                                 Authentication authentication) {
        proProperties(model, authentication);
        Page<PostJob> j = recruiterService.getLikeAndStatus(like,status,page);
        long startCount =  (page - 1L) * 6 + 1;
        long endCount = startCount + 6 -1;
        if(endCount > j.getTotalElements())
            endCount = j.getTotalElements();
        model.addAttribute("p",p);
        model.addAttribute("currentPage",page);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("totalPages",j.getTotalPages());
        model.addAttribute("jobPosts", j.getContent() );
        model.addAttribute("majors", Major.values());
        model.addAttribute("status",status);
        model.addAttribute("like",like);
        return "recruiter/managa_postjob";
    }

    @GetMapping("/quanly-baidang/{id}")
    public String postJobsDetail(Model model,
                                 @PathVariable("id") Integer id,
                                 Authentication authentication) {
        proProperties(model, authentication);
        PostJob postJob = recruiterService.getPostJobById(id);
        model.addAttribute("jopPost",postJob);
        model.addAttribute("p",p);
        model.addAttribute("expire",p.format(postJob.getPostingDeadline()));
        model.addAttribute("majors",Major.values());
        model.addAttribute("numberOfApplicants",recruiterService.countApplicantsByPostJobId(postJob.getId()));
        LocalDateTime o = postJob.getTimePost();
        LocalDateTime e = postJob.getPostingDeadline().atStartOfDay();
        Duration max = Duration.between(o, e);
        Duration current = Duration.between(o, LocalDateTime.now());
        model.addAttribute("per",current.toMinutes()*100/max.toMinutes());
        ShowViewPj pj = recruiterService.showViewPj(id);
        if(pj!=null){
            model.addAttribute("totalViews", pj.getTotalViewCount());
            model.addAttribute("userViews", pj.getLoggedInUserViewCount());
            model.addAttribute("anonymousViews", pj.getAnonymousViewCount());
            model.addAttribute("viewers", pj.getViewerList()); // List<User>
        }
        return "recruiter/postjob_detail";
    }


    @GetMapping("/tim-cv")
    public String searchCV(Model model,Authentication authentication,
                           @RequestParam(value = "like",defaultValue = "") String like ,
                           @RequestParam(name = "major", defaultValue = "0") String major,
                           @RequestParam(name = "page", defaultValue = "1")int page) {
        proProperties(model, authentication);
        Page<PostCv> postCvs = recruiterService.getListPostCvByNot(like,major,page);
        long startCount =  (page - 1L) * 6 + 1;
        long endCount = startCount + 6 -1;
        if(endCount > postCvs.getTotalElements())
            endCount = postCvs.getTotalElements();
        model.addAttribute("postCvs",postCvs.getContent());
        model.addAttribute("majors", Major.values());
        model.addAttribute("p",p);
        model.addAttribute("currentPage",page);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("totalPages",postCvs.getTotalPages());
        model.addAttribute("major",major);
        model.addAttribute("like",like);
        return "recruiter/searchcv";
    }

    @GetMapping("/duyet-cv")
    public String approveResume(Model model, Authentication authentication,
                                @RequestParam(value = "tab",defaultValue = "tab1") String tab,
                                @RequestParam(value = "page",defaultValue = "1") int page,
                                @RequestParam(value = "likeId",defaultValue = "0") String likeId,
                                @RequestParam(value = "nameJob",defaultValue = "") String nameJob,
                                @RequestParam(value = "seen",defaultValue = "01")String seen,
                                @RequestParam(name = "urgent",defaultValue = "0")int urgent,
                                @RequestParam(name = "pending",defaultValue = "0")String pending){
        proProperties(model, authentication);

        model.addAttribute("p",p);
        model.addAttribute("tab",tab);
        Page<SendOutCv> sendPage = recruiterService.getSendOutCvByRecruiterId(likeId,nameJob,seen,page,urgent,tab,pending);
        long startCount =  (page - 1L) * PAGE_SEEN_OUT_CV + 1;
        long endCount = startCount + PAGE_SEEN_OUT_CV -1;
        if(endCount > sendPage.getTotalElements())
            endCount = sendPage.getTotalElements();

        model.addAttribute("listJob",recruiterService.getAllByRecruiterId());
        model.addAttribute("likeId",likeId);
        model.addAttribute("nameJob",nameJob);
        model.addAttribute("seen",seen);
        model.addAttribute("urgent", urgent);
        model.addAttribute("pending", pending);

        model.addAttribute("sendPage", sendPage.getContent());
        model.addAttribute("currentPage",page);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("totalPages",sendPage.getTotalPages());
        model.addAttribute("totalElement",sendPage.getTotalElements());
        return "recruiter/approve-resume";
    }


    @GetMapping("/thong-bao")
    public String notification(Model model,Authentication authentication,
                               @RequestParam(value = "seen",defaultValue = "0")int seen){
        proProperties(model, authentication);
        model.addAttribute("p",p);
        model.addAttribute("seen",seen);
        return "recruiter/notification";
    }

    @GetMapping("/chitiet-cv/{id}")
    public String cvDetails(Model model,Authentication authentication,
                            @PathVariable("id")int id){
        proProperties(model, authentication);
        recruiterService.seenCv(id);
        SendOutCv cv = userService.getCvBySotId(id);
        model.addAttribute("p",p);
        model.addAttribute("cv", cv);
        model.addAttribute("numberOfCv", recruiterService.getNumberOfCvsBySotInternIdAndRecruiterId(cv.getIntern().getId()));
        return "recruiter/cv-detail";
    }

    @PostMapping("/interview/{id}/{cvStats}")
    public String interview(@PathVariable("id")int id,
                            @PathVariable("cvStats") String status){
        recruiterService.beingInterviewedCv(id,status);
        return "redirect:/recruiter/chitiet-cv/"+id;
    }

    @GetMapping("/thuc-tap-sinh")
    public String interns(Model model, Authentication authentication,
                          @RequestParam(value = "tab",defaultValue = "tab1") String tab,
                          @RequestParam(value = "page",defaultValue = "1") int page,
                          @RequestParam(value = "internStatus",defaultValue = "0") String internStatus,
                          @RequestParam(value = "keyword",defaultValue = "") String keyword,
                          @RequestParam(value = "status",defaultValue = "") String status,
                          @RequestParam(value = "week",defaultValue = "") String week){
        proProperties(model, authentication);
        model.addAttribute("p",p);
        model.addAttribute("tab",tab);
        model.addAttribute("internStatus",internStatus);
        model.addAttribute("weeks", SecWeeks.values());
        model.addAttribute("week",week);
        model.addAttribute("status",status);
        model.addAttribute("taskStatus", TaskStatus.values());
        model.addAttribute("keyword",keyword);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String minDateTime = LocalDateTime.now().plusDays(7).format(formatter);
        model.addAttribute("minDateTime", minDateTime);

        if (tab.equals("tab1")) {
            Page<Intern> sendPage = recruiterService.getInternByCompanyId(page, internStatus, keyword);
            long startCount = (page - 1L) * PAGE_SEEN_OUT_CV + 1;
            long endCount = startCount + PAGE_SEEN_OUT_CV - 1;
            if (endCount > sendPage.getTotalElements()) {
                endCount = sendPage.getTotalElements();
            }

            model.addAttribute("sendPage", sendPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("startCount", startCount);
            model.addAttribute("endCount", endCount);
            model.addAttribute("totalPages", sendPage.getTotalPages());
            model.addAttribute("totalElement", sendPage.getTotalElements());
        } else {
            Page<Task> sendPage1 = recruiterService.getTask(page, status, keyword,week);
            long startCount = (page - 1L) * PAGE_SEEN_OUT_CV + 1;
            long endCount = startCount + PAGE_SEEN_OUT_CV - 1;
            if (endCount > sendPage1.getTotalElements()) {
                endCount = sendPage1.getTotalElements();
            }

            model.addAttribute("taskStatusDum", recruiterService.taskStatusDumM());
            model.addAttribute("sendPage1", sendPage1.getContent());
            model.addAttribute("currentPage1", page);
            model.addAttribute("startCount1", startCount);
            model.addAttribute("endCount1", endCount);
            model.addAttribute("totalPages1", sendPage1.getTotalPages());
            model.addAttribute("totalElement1", sendPage1.getTotalElements());
        }

        return "recruiter/interns";
    }

    @GetMapping("/nhiem-vu/{id}")
    public String taskDetail(Model model,Authentication authentication,
                              @PathVariable("id")Integer id){
        proProperties(model, authentication);
        model.addAttribute("p",p);
        model.addAttribute("task",userService.getTaskById(id));
        model.addAttribute("taskRequest",new TaskRequest());
        return "recruiter/task-detail";
    }

    @PostMapping("/review-task/{id}")
    public String reviewTask (@ModelAttribute("taskRequest") TaskRequest taskRequest,
                              @PathVariable("id") Integer id){

        recruiterService.reviewTask(taskRequest,id);
        return "redirect:/recruiter/nhiem-vu/"+ id;
    }

    @GetMapping("/thuc-tap-sinh/{id}")
    public String internDetails(Model model,Authentication authentication,
                                @PathVariable("id")Integer id){
        proProperties(model, authentication);
        model.addAttribute("p",p);
        Point p = recruiterService.getTasksByInternId(id);
        model.addAttribute("map",p.getMap());
        model.addAttribute("point",p.getPoint());
        model.addAttribute("pointSum",p.getTotalPoint());
        model.addAttribute("intern",p.getIntern());
        model.addAttribute("totalTasks",p.getTotalTask());

        model.addAttribute("weeks", SecWeeks.values());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String minDateTime = LocalDateTime.now().plusDays(7).format(formatter);
        model.addAttribute("minDateTime", minDateTime);
        model.addAttribute("taskCompleted",p.getTaskCompleted());
        return "recruiter/intern-details";
    }
    @GetMapping("/setting")
    public String setting(Model model,Authentication authentication,
                          @RequestParam(value = "tab",defaultValue = "tab1")String tab){
        proProperties(model, authentication);
        model.addAttribute("p",p);
        model.addAttribute("tab",tab);
        model.addAttribute("company",recruiterService.getCompany());
        model.addAttribute("companySize",CompanySize.values());
        return "recruiter/setting";
    }

    @PostMapping("/update-company")
    public String updateCompany(@ModelAttribute UpdateCompany updateCompany,
                                @RequestParam("avatar") MultipartFile multipartFile,
                                @RequestParam(value = "tab",defaultValue = "tab1")String tab){
        if(multipartFile.isEmpty()){
            System.out.println("file is empty");
        }
        System.out.println(updateCompany.getCompanySize());
        return "redirect:/recruiter/setting?tab="+tab;
    }

    @GetMapping("/thong-ke")
    public String thongKe(Model model,Authentication authentication){
        proProperties(model, authentication);
        model.addAttribute("p",p);
        return "recruiter/thongke";
    }
    @GetMapping("/thong-ke2")
    public String thongKe2(Model model,Authentication authentication){
        proProperties(model, authentication);
        model.addAttribute("p",p);
        return "recruiter/thongke2";
    }

}
