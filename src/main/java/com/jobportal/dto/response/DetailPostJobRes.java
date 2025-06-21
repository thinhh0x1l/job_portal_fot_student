package com.jobportal.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class DetailPostJobRes {
    String companyName;
    String district;
    String imageUrl;
    String jobName;
    String timePost;

    String description;
    String recruitment;
    String benefits;
    int approved=0; /// 0 = choduyet, 1 daduyet, 2 tuchoi
    List<TagJob> tagJobList;

    public DetailPostJobRes(String companyName, String district, String imageUrl, String jobName, String timePost, String description, String recruitment, String benefits,int approved) {
        this.companyName = companyName;
        this.district = district;
        this.imageUrl = imageUrl;
        this.jobName = jobName;
        this.timePost = timePost;
        this.description = description;
        this.recruitment = recruitment;
        this.benefits = benefits;
        this.approved = approved;
        tagJobList = new ArrayList<>();
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public class TagJob{
        String tagName;
        String color;
    }
}
