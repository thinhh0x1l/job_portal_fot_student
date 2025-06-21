package com.jobportal.dto.dummy;

import com.jobportal.entity.Notification;
import com.jobportal.entity.SendOutCv;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ICN {
    SendOutCv sendOutCv;
    Notification notification;
}
