package com.jobportal.repostory;

import com.jobportal.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("SELECT n FROM Notification n WHERE n.receiver.id = ?1 AND n.created > ?2 ORDER BY n.id DESC ")
    List<Notification> findByReceiverId(int receiverId, LocalDateTime created);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver.id = ?1 AND n.created > ?2 AND n.seen = FALSE ")
    Integer countNotSeen(int receiverId, LocalDateTime created);

    @Modifying
    @Query("UPDATE Notification n SET n.seen = TRUE WHERE n.receiver.id = ?1 AND n.created > ?2 ")
    void updateSeen(int receiverId, LocalDateTime created);

    @Modifying
    @Query("UPDATE Notification n SET n.seen = TRUE WHERE n.receiver.id = ?1 AND n.created > ?2 AND n.id = ?3")
    void updateSeenByOne(int receiverId, LocalDateTime created,int id);

    @Modifying
    @Query("UPDATE Notification n SET n.seen = TRUE WHERE n.receiver IS NULL AND n.created > ?1 ")
    void updateSeen( LocalDateTime created);

    @Modifying
    @Query("UPDATE Notification n SET n.seen = TRUE WHERE n.receiver IS NULL  AND n.created > ?1 AND n.id = ?2")
    void updateSeenByOne(LocalDateTime created,int id);

    @Query("SELECT n FROM Notification n WHERE n.receiver IS NULL AND n.created > ?1 ORDER BY n.id DESC ")
    List<Notification> findByAdmin( LocalDateTime created);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver IS NULL AND n.created > ?1 AND n.seen = FALSE ")
    Integer countNotSeenByAdmin( LocalDateTime created);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver.id = ?1  AND n.seen = FALSE ")
    Integer countByReceiverIdAndSeenFalse(Integer receiverId  );


    @Query("SELECT n FROM Notification  n WHERE n.receiver.id = ?1  ")
    List<Notification> findByReceiverIdOrderByCreatedDesc(Integer receiverId);
}
