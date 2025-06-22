package com.jobportal.service;

import com.jobportal.dto.response.NotificationDTO;
import com.jobportal.entity.Notification;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.repostory.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class NotificationService {
    NotificationRepository notificationRepository;
    SimpMessagingTemplate messagingTemplate;

    public Notification notification(String content,
            String url,String image, User sender, User receiver){
        Notification notification = new Notification();
        notification.setContent(content);
        notification.setSender(sender);
        notification.setUrl(url);
        notification.setReceiver(receiver);
        notification.setImage(image);
        notificationRepository.save(notification);
        if(notification.getReceiver()!=null) {
            NotificationDTO dto = new NotificationDTO(
                    notification.getId(),
                    notification.getContent(),
                    imageClear(notification),
                    notification.getUrl(),
                    false, notification.getCreated(),
                    notification.getReceiver() == null ? null : notification.getReceiver().getId()
            );

            messagingTemplate.convertAndSendToUser(receiver.getId().toString(), "/queue/notify", dto);
        }
        return notification;
    }
    private String imageClear(Notification notification){
        if(notification.getImage().contains("admin"))
            return  notification.getImage();
        else if(notification.getImage().contains("user-photos"))
            return"/images/user-photos/"+notification.getSender().getId()+"/"+notification.getSender().getImageUrl();
        else {
            Recruiter r = (Recruiter)notification.getSender();
            return "/images/companies/"+notification.getSender().getId()+"/"+r.getCompany().getImageUrl();
        }
    }
    public List<NotificationDTO> getNotifications(Integer receiverId) {
        return notificationRepository.findByReceiverIdOrderByCreatedDesc(receiverId)
                .stream().map(n -> new NotificationDTO(
                        n.getId() ,n.getContent(),  imageClear(n), n.getUrl(),n.isSeen(), n.getCreated(), (n.getReceiver().getId()==null)? null : n.getReceiver().getId()
                )).toList();

    }


    public void markAllAsRead(Integer receiverId) {
        List<Notification> notis = notificationRepository.
                findByReceiverIdOrderByCreatedDesc(receiverId);
        notis.forEach(n -> n.setSeen(true));
        notificationRepository.saveAll(notis);
    }

    public void markOneAsRead(Integer id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setSeen(true);
            notificationRepository.save(n);
        });
    }

    public long countNotSeen(Integer receiverId) {
        return notificationRepository.countByReceiverIdAndSeenFalse(receiverId);
    }
}
