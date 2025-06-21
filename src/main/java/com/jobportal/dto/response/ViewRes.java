package com.jobportal.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ViewRes {
    MonthlyViews monthlyViews = new MonthlyViews();
    RatioData ratioData = new RatioData();
    ViewsByMajor viewsByMajor = new ViewsByMajor();

    public ViewRes(int viewData, int uniqueViewData,int anymoreViewData, List<String> labels, List<Integer> data){
        monthlyViews.getViewData().add(viewData);
        monthlyViews.getUniqueViewData().add(uniqueViewData);
        monthlyViews.getAnymoreViewData().add(anymoreViewData);
        viewsByMajor.labels=labels;
        viewsByMajor.data=data;
    }

    @Getter
    @Setter
    public static class MonthlyViews{
        List<String> labels = List.of("Luợt Xem ");
        List<Integer> viewData = new ArrayList<>();
        List<Integer> uniqueViewData = new ArrayList<>();;
        List<Integer> anymoreViewData = new ArrayList<>();;
    }
    @Getter
    @Setter
    private static class RatioData{
        List<String> labels;
        List<Integer> viewData;
        List<Integer> applicationData;
    }
    @Getter
    @Setter
    private static class ViewsByMajor{
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
    }
}
