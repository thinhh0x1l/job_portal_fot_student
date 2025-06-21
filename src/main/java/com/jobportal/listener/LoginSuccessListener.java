package com.jobportal.listener;

import com.jobportal.dto.request.InternDto;
import com.jobportal.entity.Intern;
import com.jobportal.entity.Recruiter;
import com.jobportal.model.Role;
import com.jobportal.repostory.InternRepository;
import com.jobportal.repostory.RecruiterRepository;
import com.jobportal.security.CustomDefaultOidcUser;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.OnlineService;
import com.jobportal.service.recruiter.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final OnlineService onlineService;
    private final RecruiterRepository recruiterRepository;
    private final InternRepository internRepository;
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        Role role = null;
        String email = null;
        if(principal instanceof CustomUserDetails customUserDetails) {
            role = customUserDetails.getUser().getRole();
            email = customUserDetails.getUser().getEmail();
        }else if (principal instanceof CustomDefaultOidcUser customDefaultOidcUser) {
            role = customDefaultOidcUser.getUser().getRole();
            email = customDefaultOidcUser.getUser().getEmail();
        }
        if(Role.RECRUITER.equals(role)){
            Recruiter recruiter =  recruiterRepository.findByEmail(email).get();
            Map<String, Recruiter> map = onlineService.getRecruiterAccounts();
            if(!map.containsKey(email))
                map.put(email, recruiter);
            System.out.println(map);
        } else if (Role.INTERN.equals(role)) {
            Intern intern =  internRepository.findByEmail(email).get();
            Map<String, Intern> map = onlineService.getInternAccounts();
            InternDto internDto = new InternDto(intern.getId(),intern.getEmail(),intern.getPassword(),intern.getFirstName(),intern.getLastName());
            Map<String, InternDto> map1 = onlineService.getInternDtoAccounts();
            if(!map.containsKey(email))
                map.put(email, intern);
            if(!map1.containsKey(email))
                map1.put(email, internDto);
            System.out.println(map1);
        }

    }

}
