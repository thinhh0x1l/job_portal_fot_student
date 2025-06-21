package com.jobportal.controller.recruiter_controller;

import com.jobportal.dto.dummy.RegisterRecruiter;
import com.jobportal.entity.Recruiter;
import com.jobportal.model.District;
import com.jobportal.service.recruiter.RecruiterService;
import com.jobportal.utils.CommonUtils;
import com.jobportal.utils.FileUploadUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static com.jobportal.utils.CommonUtils.setUriv;


@Controller
@RequiredArgsConstructor
@RequestMapping("/recruiter")
public class RecruiterAuth {
    private final BasicTextEncryptor stringEncryptor;
    private final RecruiterService recruiterService;
    @GetMapping("/login")
    public String register(Model model, HttpServletRequest request, Authentication authentication) {

        if(authentication ==null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("recruiter", new Recruiter());
            setUriv("recruiter");
            return "recruiter/login";
        }
       return "redirect:/recruiter/homepage";
    }
    @PostMapping("/dang-ky")
    public String createNewRecruiter(@ModelAttribute RegisterRecruiter registerRecruiter, HttpServletRequest request,
                                     RedirectAttributes redirectAttributes,
                                     @RequestParam("avatar") MultipartFile multipartFile) throws MessagingException, IOException {


//        ///aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
/////aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
//        registerRecruiter.setEmail(stringEncryptor.encrypt("minjulin18200@gmail.com"));
/////aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
/////aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String uploadDir = "photos/user-photos/" + recruiterService
                .register(registerRecruiter, CommonUtils.getSiteURL(request),new HashMap<>(),fileName).getId();
        if(!multipartFile.isEmpty())
            FileUploadUtil.cleanDir(uploadDir);
        FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        redirectAttributes.addFlashAttribute("registerSuccess", "Tạo tài khoản thành công.\nVui lòng kiểm tra email.");
        return "redirect:/recruiter/dang-ky";
    }

    @GetMapping("/dang-ky")
    public String register(Model model, HttpServletRequest request) {
        model.addAttribute("registerRecruiter", new RegisterRecruiter());
        model.addAttribute("district", District.values());
        return "recruiter/create_company_v3";
    }
}
