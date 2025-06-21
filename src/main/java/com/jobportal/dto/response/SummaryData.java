package com.jobportal.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor

public class SummaryData {
    int totalPosts;
    Object totalChange;
    int activePosts;
    Object activeChange;
    int expiredPosts;
    Object expiredChange;
    int urgentPosts;
    Object urgentChange;
    ViewRes viewRes;

    public SummaryData() {
        this.totalPosts = 0;
        this.totalChange = null;
        this.activePosts = 0;
        this.activeChange = null;
        this.expiredPosts = 0;
        this.expiredChange = null;
        this.urgentPosts = 0;
        this.urgentChange = null;
        this.viewRes = new ViewRes();
    }
}
