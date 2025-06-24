package com.jobportal.controller.webSocketController.webSocketService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class WebSocketPresenceListener {


    OnlineInternTracker onlineInternTracker;

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        // Lấy internId từ header custom
        String internIdStr = accessor.getFirstNativeHeader("internId");
        if (internIdStr != null) {
            onlineInternTracker.addOnlineIntern(Integer.valueOf(internIdStr));
        }
    }

    @EventListener
    public void handleSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        // Giả sử bạn lưu internId vào session
        Integer internId = (Integer) accessor.getSessionAttributes().get("internId");
        if (internId != null) {
            onlineInternTracker.removeOnlineIntern(internId);
        }
    }
}

