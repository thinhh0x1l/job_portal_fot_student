package com.jobportal.controller.lecturer_controller;


import com.jobportal.entity.Intern;
import com.jobportal.entity.Recruiter;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.UserService;
import com.jobportal.service.file.StorageService;
import com.jobportal.service.intern.InternService;
import com.jobportal.service.lecturer.LecturerService;
import com.jobportal.service.recruiter.PostJobService;
import com.jobportal.service.recruiter.RecruiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

import static com.jobportal.utils.CommonUtils.setUriv;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequestMapping("/lecturer")
public class LecturerController {
    StorageService storageService;
    InternService internService;
    RecruiterService recruiterService;
    PostJobService postJobService;
    LecturerService lecturerService;
    UserService userService;
    PrettyTime p = new PrettyTime(new Locale("vi"));
    private void proper(Model model ,Authentication auth) {
        model.addAttribute("p",p);
        model.addAttribute("notification",userService.findByReceiverId());
        model.addAttribute("countNotSeen",userService.countNotSeen());
        model.addAttribute("viLocale", new Locale("vi", "VN"));
        model.addAttribute("auth",(CustomUserDetails)auth.getPrincipal() );
    }

    @GetMapping("/login")
    public String register(Model model, HttpServletRequest request, Authentication authentication) {

        if(authentication ==null || authentication instanceof AnonymousAuthenticationToken) {
            setUriv("recruiter");
            return "lecturer/login";
        }
        System.out.println(1+" "+authentication.isAuthenticated());
        return "redirect:/lecturer/homepage";
    }
    @GetMapping("/manage-student")
    public String manageStudent(Model model,
                                HttpServletRequest request, Authentication authentication) {
        proper(model,authentication);
        model.addAttribute("intern",new Intern());
        model.addAttribute("s",2);
        model.addAttribute("interns",lecturerService.getAllInterns());
        return "lecturer/students";
    }
    @GetMapping("/student/{id}")
    public String student(Model model, HttpServletRequest request, Authentication authentication,
                          @PathVariable int id,
                          @RequestParam(value = "tab",defaultValue = "tab1") String tab) {
        proper(model,authentication);
        model.addAttribute("intern",new Intern());
        model.addAttribute("interns",lecturerService.getAllInterns());
        model.addAttribute("tab",tab);
        model.addAttribute("internId",id);
        model.addAttribute("iD",lecturerService.detailStudent(id));
        model.addAttribute("s",2);

        model.addAttribute("followInterns",lecturerService.followIntern(id));

        return "lecturer/follow_internship";
    }
}
