package com.jobportal.exception;


import com.jobportal.service.RequestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.time.Instant;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandle {
    private final RequestService requestService;

//    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
//    public ModelAndView handleError405(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
//
//    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleNoResourceFoundException(NoResourceFoundException e,HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error/404");
        mav.addObject("exception", e.getMessage());
        mav.addObject("url", request.getRequestURL());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public String handleInternalError(Model model, Exception e, HttpServletRequest request) {
        model.addAttribute("exception", e);
        model.addAttribute("url", request.getRequestURL());
        model.addAttribute("timestamp", Instant.now());
        return "error/500"; // Trả về view templates/error/500.html
    }
}
