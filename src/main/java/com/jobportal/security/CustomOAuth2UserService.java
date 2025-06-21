package com.jobportal.security;


import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.model.AuthProvider;
import com.jobportal.model.Role;
import com.jobportal.repostory.RecruiterRepository;
import com.jobportal.repostory.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;

import static com.jobportal.utils.CommonUtils.checkUriv;


@Component
public class CustomOAuth2UserService extends OidcUserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    /*** public String someProtectedPage(HttpServletRequest request) {
             String currentUrl = request.getRequestURL().toString();
             request.getSession().setAttribute("redirect_uri", currentUrl);
             return "redirect:/oauth2/authorization/google"; // hoặc để Spring auto redirect
     }*/

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // Gọi service gốc để lấy OidcUser (chứa claims từ Google)
        OidcUser oidcUser = super.loadUser(userRequest);
//        HttpServletRequest currentRequest =
//                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
//                        .getRequest();
//        String redirectUri = (String) currentRequest.getSession().getAttribute("redirect_uri");

        // Lấy thông tin từ claims (ID Token)
        String email = oidcUser.getEmail(); // hoặc: oidcUser.getClaims().get("email")
        String providerId = oidcUser.getSubject(); // Google: "sub"
        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"

        System.out.println(oidcUser.getClaims());
        // Kiểm tra user đã tồn tại trong DB chưa
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // Tạo user mới nếu chưa tồn tại
            if(checkUriv("recruiter")) {
                Recruiter newUser = new Recruiter(
                        oidcUser.getEmail(),
                        oidcUser.getFamilyName(),
                        oidcUser.getGivenName(),
                        Role.RECRUITER,
                        AuthProvider.GOOGLE
                );
                return recruiterRepository.save(newUser);
            }else {
                return null;
            }
        });
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole())
        );
        System.out.println(authorities.getFirst());
        // Có thể thêm xử lý gán vai trò hoặc thêm thông tin ở đây

        return new CustomDefaultOidcUser(authorities,oidcUser.getIdToken(),oidcUser.getUserInfo(),user); // Trả về cho Spring Security
    }

}
