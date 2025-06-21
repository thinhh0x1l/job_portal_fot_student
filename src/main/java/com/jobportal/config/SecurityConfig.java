
package com.jobportal.config;

import com.jobportal.filter.RequestFilter;

import com.jobportal.handlesuccess.CustomAuthenticationSuccessHandler;
import com.jobportal.model.Role;
import com.jobportal.repostory.PostJobRepository;
import com.jobportal.security.CustomDefaultOidcUser;
import com.jobportal.security.CustomOAuth2UserService;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.recruiter.RecruiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private RecruiterService recruiterService;



    @Autowired
    CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("customUserDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private RequestFilter requestFilter;

    @Bean
    @Order(4)
    public SecurityFilterChain securityFilterChainIntern(HttpSecurity http) throws Exception {
        System.out.println(3);
        http
                .securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/**"),
                        new AntPathRequestMatcher("/oauth2/**"),
                        new AntPathRequestMatcher("/user/api/**")
                ))
                .authenticationProvider(authProvider())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                           "/", "/home", "/{path:[^\\.]*}",
                                "/api/**",
                                "/kma/**",
                                "/ws-notify/**",
                                "/files/**",
                                "/register",
                                "/homepage",
                                "/403",
                                "/404",
                                "/405",
                                "/compy/**",
                                "/companies",
                                "/user/api/**",
                                "/intern/api/**",
                                "/homepage",
                                "/tim-viec/*",
                                "/test",
                                "/online/**",
                                "/.well-known/appspecific/**").permitAll()
//                        .anyRequest().hasAnyRole("INTERN","ADMIN"))
                        .requestMatchers("/profile").authenticated()
                        .anyRequest().hasAnyRole("INTERN"))
                .formLogin(login->login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureUrl("/login?error")
                        .defaultSuccessUrl("/homepage",true)
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            request.getRequestDispatcher("/403").forward(request, response);
                        }))

                .addFilterBefore(requestFilter, AuthorizationFilter.class);
        return http.build();
    }
    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChainRecruiter(HttpSecurity http) throws Exception {
        System.out.println(1);
        return http
                .securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/recruiter/**"),
                        new AntPathRequestMatcher("/oauth2/**"),
                        new AntPathRequestMatcher("/login/oauth2/**"),
                        new AntPathRequestMatcher("/recruiter/files/**")

                ))
                .authenticationProvider(authProvider())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/recruiter/register",
                                "/recruiter/api/**",
                                "/recruiter/dang-ky"
                            )
                            .permitAll()
                        .requestMatchers("/recruiter/quanly-baidang/{id}/**").access(
                                AuthorizationManagers.anyOf(
                                        new PathVariableAuthorizationManager("id")
                                )
                        )
                        .anyRequest().hasRole(Role.RECRUITER.name()))
                .formLogin(login->login
                        .loginPage("/recruiter/login")
                        .loginProcessingUrl("/recruiter/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureUrl("/recruiter/login?error")
                       // .successHandler(customAuthenticationSuccessHandler)
                        .defaultSuccessUrl("/recruiter/homepage",true)
                        .permitAll())
                .oauth2Login(oauth -> oauth
                        .loginPage("/oauth2/authorization/{registrationId}")// VD: /oauth2/authorization/google
                        .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(redir -> redir.baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(user -> user.oidcUserService(customOAuth2UserService))
                        //.successHandler(defaultSuccessHandler)
                        //.failureHandler(defaultFailureHandler)
                )
                .headers(header->header
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            request.getRequestDispatcher("/403").forward(request, response);
                        }))
                .logout(logout -> logout
                        .logoutUrl("/recruiter/logout"))

                .addFilterBefore(requestFilter, AuthorizationFilter.class)
                .build();
    }
    @Bean
    @Order(3)
    public SecurityFilterChain securityFilterChainLecturer(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/lecturer/**"),
                        new AntPathRequestMatcher("/lecturer/files/**")

                ))
                .authenticationProvider(authProvider())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/lecturer/api/**"
                        )
                        .permitAll()
//                        .requestMatchers("/recruiter/quanly-baidang/{id}/**").access(
//                                AuthorizationManagers.anyOf(
//                                        new PathVariableAuthorizationManager("id")
//                                )
//                        )
                        .anyRequest().hasRole(Role.LECTURER.name()))
                .formLogin(login->login
                        .loginPage("/lecturer/login")
                        .loginProcessingUrl("/lecturer/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureUrl("/lecturer/login?error")
                        // .successHandler(customAuthenticationSuccessHandler)
                        .defaultSuccessUrl("/lecturer/manage-student",true)
                        .permitAll())
                .headers(header->header
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            request.getRequestDispatcher("/403").forward(request, response);
                        }))
                .logout(logout -> logout
                        .logoutUrl("/lecturer/logout"))

                .addFilterBefore(requestFilter, AuthorizationFilter.class)
                .build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChainAdmin (HttpSecurity http,@Qualifier("customAdminDetailsService")UserDetailsService userDetailsService) throws Exception {
        System.out.println(2);
        return http
                .securityMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/admin/**")
                ))
                .csrf(AbstractHttpConfigurer::disable)
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/api/").permitAll()
                        .anyRequest().hasAnyRole("ADMIN"))
                .formLogin(login->login
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
//                        .successHandler(customAuthenticationSuccessHandler)
                                .defaultSuccessUrl("/admin/dashboard",true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout"))
                .build();
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> webSecurity
                .ignoring().requestMatchers("/photos/**","/css/**", "/js/**", "/images/**", "/favicon.ico",
                        "/webjars/**","/fontawesome/**","/webfonts/**", "/style/**","/files/**");
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }




    private class PathVariableAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext>{
        private final String path;
        public PathVariableAuthorizationManager(String path) {
            this.path = path;
        }

        @Override
        public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
            if(authentication.get() == null)
                return new AuthorizationDecision(false);
            var pathValue = object.getVariables().get(this.path);
            Object principal = authentication.get().getPrincipal();;
            if(principal instanceof CustomUserDetails userDetails) {
                System.out.println(userDetails.getId() + " " + path);
                return new AuthorizationDecision(recruiterService.checkAuthz(userDetails.getId(), Integer.parseInt(pathValue)));
            }
            if(principal instanceof CustomDefaultOidcUser c ) {
                return new AuthorizationDecision(recruiterService.checkAuthz(c.getUser().getId(),Integer.parseInt(pathValue)));
            }
            return new AuthorizationDecision(false);
        }

    }
}

