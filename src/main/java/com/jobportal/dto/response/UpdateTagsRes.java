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
public class UpdateTagsRes {
    List<TypeTagRes> programmingLanguage;
    List<TypeTagRes> backend;
    List<TypeTagRes> frontend;
    List<TypeTagRes> database;
    List<TypeTagRes> order;


    public UpdateTagsRes(){
        this.programmingLanguage =new ArrayList<>();
        this.backend =new ArrayList<>();
        this.frontend =new ArrayList<>();
        this.database=new ArrayList<>();
        this.order =new ArrayList<>();
    }

    @Data
    public static class TypeTagRes{
        int id;
        String name;
        String className;
        String checked;
    }

}
