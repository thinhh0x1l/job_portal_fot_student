package com.jobportal.dto.dummy;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagHomePageRes {
    int id;
    String name;
    String className;
    String checked;
}
