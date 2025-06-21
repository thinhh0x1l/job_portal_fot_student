package com.jobportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalDateTime;
import java.util.Locale;

@Data
@NoArgsConstructor
public class NotificationDTO {
//    PrettyTime p = new PrettyTime(new Locale("vi"));
    private Integer id;
    private String content;
    private String image;
    private String url;
    private boolean seen;
    private LocalDateTime created;
    private Integer receiverId;

    public NotificationDTO(Integer id, String content, String image, String url, boolean seen, LocalDateTime created, Integer receiverId) {
        this.id = id;
        this.content = content;
        this.image = image;
        this.url = url;
        this.seen = seen;
        this.created = created;
        this.receiverId = receiverId;

    }
}