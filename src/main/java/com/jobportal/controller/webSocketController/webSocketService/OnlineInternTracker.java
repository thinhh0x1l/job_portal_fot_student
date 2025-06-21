package com.jobportal.controller.webSocketController.webSocketService;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineInternTracker {

    private final Set<Integer> onlineInterns = ConcurrentHashMap.newKeySet();

    public void addOnlineIntern(Integer internId) {
        onlineInterns.add(internId);
    }

    public void removeOnlineIntern(Integer internId) {
        onlineInterns.remove(internId);
    }

    public Set<Integer> getOnlineInterns() {
        return Collections.unmodifiableSet(onlineInterns);
    }
}
