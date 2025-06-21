package com.jobportal.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class MailInfo {
    @Builder.Default
    String from = "Kma-tuyển-dụng";
    String to;
    String[] cc;
    String[] bcc;
    String subject;
    String body;
    Map<String, byte[]> files = new HashMap<>();
}
