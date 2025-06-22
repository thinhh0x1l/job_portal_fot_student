package com.jobportal.admin.controller;

import com.jobportal.admin.service.AdminService;
import com.jobportal.entity.*;
import com.jobportal.model.Faculty;
import com.jobportal.model.TagType;
import com.jobportal.service.UserService;
import com.jobportal.service.intern.InternService;
import com.jobportal.service.lecturer.LecturerService;
import com.jobportal.service.recruiter.CompanyService;
import com.jobportal.utils.CommonUtils;
import com.jobportal.utils.FileUploadUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static com.jobportal.admin.service.AdminService.PAGE_POSTJOB;
import static com.jobportal.service.recruiter.RecruiterService.PAGE_SEEN_OUT_CV;

@Controller
@RequestMapping("/admin")
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminController {
    LecturerService lecturerService;
    InternService internService;
    CompanyService companyService;
    AdminService adminService;
    PrettyTime p = new PrettyTime(new Locale("vi"));
    @GetMapping("/login")
    public String login() {
        return "admin/login";
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        proper(model);
        return "admin/dashboard";
    }

    private void proper(Model model) {
        model.addAttribute("p",p);
        model.addAttribute("notification",adminService.getNotificationByAdmin());
        model.addAttribute("countNotSeen",adminService.countNotSeenByAdmin());
        model.addAttribute("viLocale", new Locale("vi", "VN"));
    }

    @GetMapping("/manage-company")
    public String manageCompany(Model model) {
        proper(model);
        model.addAttribute("companies",companyService.getAllCompanies());
        return "admin/companies";
    }

    @GetMapping("/manage-ai")
    public String manageAi(Model model) {
        proper(model);

        return "admin/ai_system";
    }

    @GetMapping("/manage-student")
    public String manageStudent(Model model) {
        proper(model);
        model.addAttribute("s", 2);
        model.addAttribute("interns",internService.getInterns());
        model.addAttribute("intern", new Intern());
        model.addAttribute("lecturers",lecturerService.getAllLecturers());
        return "admin/students";
    }

    @PostMapping("/create-student")
    public String createLecturer(@ModelAttribute("intern" ) Intern intern, HttpServletRequest request,
                                 @RequestParam("avatar") MultipartFile multipartFile,
                                 @RequestParam("lecturerId") int lecturerId) {
        System.out.println(lecturerId);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        System.out.println(fileName);
        intern.setImageUrl(fileName);
        String uploadDir = "photos/user-photos/" + internService
                .register(intern,lecturerId).getId();
        FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        return "redirect:/admin/manage-student";
    }
    @GetMapping("/manage-lecturer")
    public String manageLecturer(Model model) {
        proper(model);
        model.addAttribute("lecturers",lecturerService.getAllLecturers());
        model.addAttribute("lecturer", new Lecturer());
        model.addAttribute("faculty", Faculty.values());
        model.addAttribute("s", 3);
        return "admin/lecturers";
    }
    @PostMapping("/create-lecturer")
    public String createLecturer(@ModelAttribute("lecturer" ) Lecturer lecturer, HttpServletRequest request,

                                 @RequestParam("avatar") MultipartFile multipartFile) {
        System.out.println(lecturer.getFaculty().getDisplayName());
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        lecturer.setImageUrl(fileName);
        String uploadDir = "photos/user-photos/" + lecturerService
                .register(lecturer).getId();
        FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        return "redirect:/admin/lecturer";
    }
    @GetMapping("/manage-recruiter")
    public String manageRecruiter(Model model) {
        proper(model);
        return "admin/recruiters";
    }
    @GetMapping("/manage-tag")
    public String manageTag(Model model, @RequestParam(value = "typeTag",defaultValue = "0") String typeTag) {
        proper(model);
        model.addAttribute("newTag",new Tag());
        model.addAttribute("typeTags", TagType.values());
        model.addAttribute("tags",adminService.getAllTags(typeTag));
        model.addAttribute("typeTag",typeTag);
        return "admin/tags";
    }



    @PostMapping("/newTag")
    public String newTag(@ModelAttribute("newTag") Tag tag) {
        adminService.newTag(tag);
        return "redirect:/admin/manage-tag";
    }
    @PostMapping("/updateTag")
    public String updateTag(
            @RequestParam("idTag") int idTag,
            @RequestParam("tagType") String tagType,
            @RequestParam("tagName") String tagName,
                            Model model) {
        adminService.updateTag(idTag,tagType,tagName);
        return "redirect:/admin/manage-tag";
    }

    @PostMapping("/deleteTag/{id}")
    public String deleteTag(@PathVariable("id") int idTag) {
        adminService.deleteTag(idTag);
        return "redirect:/admin/manage-tag";
    }

    @GetMapping("/manage-postjob")
    public String managePostJob(Model model,
                                @RequestParam(value = "ctyName",defaultValue = "0") int ctyName,
                                @RequestParam(value = "status",defaultValue = "0") int status,
                                @RequestParam(value = "page",defaultValue = "1") int page
                                ) {
        proper(model);
        model.addAttribute("ctyName",ctyName);
        model.addAttribute("status",status);
        model.addAttribute("recruiter",adminService.getAllRecruiter());
        Page<PostJob> postJobs = adminService.getPostJob(page,ctyName,status);
        long startCount =  (page - 1L) * PAGE_POSTJOB + 1;
        long endCount = startCount + PAGE_POSTJOB -1;
        if(endCount > postJobs.getTotalElements())
            endCount = postJobs.getTotalElements();

        model.addAttribute("postJobs", postJobs.getContent());
        model.addAttribute("currentPage",page);
        model.addAttribute("startCount",startCount);
        model.addAttribute("endCount",endCount);
        model.addAttribute("totalPages",postJobs.getTotalPages());
        model.addAttribute("totalElement",postJobs.getTotalElements());
        return "admin/postJob";
    }
}
