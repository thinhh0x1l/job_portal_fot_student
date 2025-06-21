package com.jobportal.service;

import com.jobportal.dto.request.InternDto;
import com.jobportal.entity.Intern;
import com.jobportal.entity.Recruiter;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class OnlineService {
    private final Map<String, Recruiter> recruiterAccounts = new HashMap<>();
    private final Map<String, Intern> internAccounts = new HashMap<>();
    private final Map<String, InternDto> internDtoAccounts = new HashMap<>();


}
