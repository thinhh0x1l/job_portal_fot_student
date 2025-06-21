package com.jobportal.dto.dummy;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class FollowIntern {
    int taskId;
    String week;
    String title;

    int weekOrder;
    String tryi;
    int count=0;
    //1 chưa hoàn thành - report == null
    //2 hoàn thành point != null && submit Time <= timeEnd

    //3 hoàn thành trễ point != null && submit Time > timeEnd
    //4 chưa chấm điểm - report != null && point == null
}
