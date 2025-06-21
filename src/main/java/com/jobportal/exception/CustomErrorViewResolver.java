package com.jobportal.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
public class CustomErrorViewResolver implements ErrorViewResolver {
    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request,
                                         HttpStatus status,
                                         Map<String, Object> model) {
        if (status == HttpStatus.METHOD_NOT_ALLOWED) {
            ModelAndView mav = new ModelAndView("error/405");
            mav.addObject("url", request.getRequestURL());
            mav.addObject("exception", model.get("message"));
            return mav;
        }
        return null; //
    }
}