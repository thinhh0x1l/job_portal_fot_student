package com.jobportal.controller;

import com.jobportal.entity.Intern;
import com.jobportal.entity.PostJob;
import com.jobportal.entity.Recruiter;
import com.jobportal.model.District;
import com.jobportal.model.Major;
import com.jobportal.model.Role;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.UserService;
import com.jobportal.service.intern.InternService;
import com.jobportal.service.recruiter.PostJobService;
import com.jobportal.service.recruiter.RecruiterService;
import com.jobportal.utils.CommonUtils;
import com.jobportal.utils.FileUploadUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;

import static com.jobportal.utils.CommonUtils.setUriv;
import static com.jobportal.utils.UrlSafeEncryptorUtil.decrypt;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class SecurityController {
    InternService internService;
    UserService userService;
    PostJobService postJobService;
    private final PrettyTime p = new PrettyTime(new Locale("vi"));
    @GetMapping("/login")
    public String login(Authentication authentication, Model model) {
        if(authentication ==null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("intern", new Intern());
            //setUriv("recruiter");
            return "intern/login";
        }

        return "redirect:/homepage";
    }
//    @RequestMapping(value = "/{path:[^\\.]*}")
//    public String redirect() {
//        return "forward:/homepage"; // hoặc forward:/index nếu bạn dùng trang index.html
//    }
    @GetMapping("/")
    public String rootRedirect() {
        return "forward:/homepage";
    }
    @PostMapping("/register")
    public String createNewRecruiter(@ModelAttribute Intern intern, HttpServletRequest request,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam("avatar") MultipartFile multipartFile) throws MessagingException, IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        intern.setImageUrl(fileName);
        String uploadDir = "photos/user-photos/" + internService
                .register(intern, CommonUtils.getSiteURL(request),new HashMap<>()).getId();
        if(!multipartFile.isEmpty())
            FileUploadUtil.cleanDir(uploadDir);
        FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        redirectAttributes.addFlashAttribute("message", "Tạo tài khoản thành công.\nVui lòng xác thực");
        return "redirect:/recruiter/login";
    }

//    @GetMapping("/homepage")
//    public String homepage(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        model.addAttribute("postJobs", postJobService.getAll());
//        model.addAttribute("p",p);
//        return "intern/homepage";
//    }

    @GetMapping("/kma/verify")
    public String verifyAcc(@RequestParam(value = "code") String code, Model model)  {
        System.out.println(decrypt(code));
        System.out.println(code);
        if (userService.verify(code).equals("Success")) {
            model.addAttribute("url",userService.url(code));
            return "account/verify-success";
        } else if(userService.verify(code).equals("Fail")){
            return "account/verify-fail";
        }
        model.addAttribute("code",code);
        return "account/verify-expire";

    }
    @PostMapping("/kma/re_verify/{code}")
    public String reVerify(@PathVariable("code") String code, HttpServletRequest req, Model model) throws MessagingException, IOException {
        userService.reSendVerifyEmail(CommonUtils.getSiteURL(req),code);
        model.addAttribute("message","Đã gửi lại mã xác nhận");
        return "account/verify-expire";
    }

//    @GetMapping("/403")
//    public String e403(Model model) {
//
//        return "error/403";
//    }
}
