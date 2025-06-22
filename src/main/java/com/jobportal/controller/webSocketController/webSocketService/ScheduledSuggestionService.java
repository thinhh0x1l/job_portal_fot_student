package com.jobportal.controller.webSocketController.webSocketService;

import com.jobportal.dto.request.SuggestJobs;
import com.jobportal.entity.Intern;
import com.jobportal.service.intern.InternService;
import com.jobportal.service.recruiter.PostJobService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class ScheduledSuggestionService {
    private final OnlineInternTracker onlineInternTracker;
    private final InternService internService;
    private final SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 5*1000)
    public void sendSuggestionsToOnlineInterns() {
        for (Integer internId : onlineInternTracker.getOnlineInterns()) {
            List<SuggestJobs> suggestions = internService.suggestJobsWithMatchCount(internId);
            messagingTemplate.convertAndSend("/topic/suggestions/" + internId, suggestions);

        }
    }
}
