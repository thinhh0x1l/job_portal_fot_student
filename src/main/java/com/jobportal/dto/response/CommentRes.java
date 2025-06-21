package com.jobportal.dto.response;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRes {
    double avgStar;
    int totalComments;
    List<Integer> starAnalysis;
    List<CommentResV2> comments = new ArrayList<>();
    int numberOfPage;
    int currentPage;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public class CommentResV2{
        int commentId;
        String fullname;
        String image;
        String role;

        int star;
        LocalDateTime created;

        String title;
        String content;

        int like;
        boolean liked;
        boolean itsMe;
    }
}
