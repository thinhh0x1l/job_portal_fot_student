package com.jobportal.controller.webSocketController;

import com.jobportal.dto.request.SuggestJobs;
import com.jobportal.entity.Intern;
import com.jobportal.service.intern.InternService;
import com.jobportal.service.recruiter.PostJobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class SuggestJobController {


    SimpMessagingTemplate messagingTemplate;


    InternService internService ;

    @MessageMapping("/suggest")
    public void handleSuggestRequest(Principal principal) {
        Integer internId = internService.getIntern(principal).getId();
        List<SuggestJobs> suggested = internService.suggestJobsWithMatchCount(internId);

        messagingTemplate.convertAndSend("/topic/suggestions/" + internId, suggested);
    }
}