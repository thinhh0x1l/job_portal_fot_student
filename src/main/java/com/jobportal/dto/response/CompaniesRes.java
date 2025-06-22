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
@NoArgsConstructor
public class CompaniesRes {
    int currentPage;
    int totalPages;
    long elementStart;
    long elementEnd;
    long totalElements;

    String keywords;
    String filter;
    List<District> districts = new ArrayList<District>();
    List<Company> companies = new ArrayList<Company>();

    public CompaniesRes(String districtCode){
        districts.add(this.new District("Tất cả ","0",districtCode.equals("0")));
    }

    @Data
    @AllArgsConstructor
    public class District{
        String name;
        String code;
        boolean selected;
    }
    @Data
    public class Company{
        int id;
        String companyName;
        String image;
        String district;
        String description;

        Integer jobs;
        Double avgStars;
    }
}
