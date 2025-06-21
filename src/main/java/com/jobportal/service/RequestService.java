package com.jobportal.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class RequestService {
    private final String [] req = new String[2];

    public String addCurr(String uri) {
        if(req[0] == null ||uri.contains("login") && req[0].contains("login")) {
            return "";
        }
        req[1] = req[0];
        return req[0] = uri;
    }
    public String getPrev(){
        return req[1];
    }
}
