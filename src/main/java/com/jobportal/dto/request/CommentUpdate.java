package com.jobportal.dto.request;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentUpdate {
    int commentId;
    int editRating;
    String title;
    String content;
}
