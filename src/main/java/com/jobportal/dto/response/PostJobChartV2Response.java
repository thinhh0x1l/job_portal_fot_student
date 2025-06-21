package com.jobportal.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostJobChartV2Response {
    List<String> labels;
    List<Dataset> datasets = new ArrayList<>();

    public PostJobChartV2Response() {
        this.labels = new ArrayList<>();
        this.datasets = new ArrayList<>();
    }

    public PostJobChartV2Response(List<String> label, List<Integer> totalPost,
                                  List<Integer> totalPostApproved,
                                  List<Integer> totalPostNotApproved,
                                  List<Integer> totalPostRejected,
                                  List<Integer> totalPostHidden
                                  ) {
        this.labels = label;
        datasets.add(
                new Dataset(
                        "Toàn bộ",
                        totalPost,
                        "rgba(59, 130, 246, 0.7)",     // xanh dương nhạt
                        "rgba(59, 130, 246, 1)"        // xanh dương đậm
                )
        );
        datasets.add(
                new Dataset(
                        "Được duyệt",
                        totalPostApproved,
                        "rgba(16, 185, 129, 0.7)",     // xanh lá nhạt
                        "rgba(16, 185, 129, 1)"        // xanh lá đậm
                )
        );
        datasets.add(
                new Dataset(
                        "Chưa duyệt",
                        totalPostNotApproved,
                        "rgba(245, 158, 11, 0.7)",     // vàng nhạt
                        "rgba(245, 158, 11, 1)"        // vàng đậm
                )
        );
        datasets.add(
                new Dataset(
                        "Bị từ chối",
                        totalPostRejected,
                        "rgba(239, 68, 68, 0.7)",      // đỏ nhạt
                        "rgba(239, 68, 68, 1)"         // đỏ đậm
                )
        );
        datasets.add(
                new Dataset(
                        "Bài đã gỡ",
                        totalPostHidden,
                        "rgba(107, 114, 128, 0.7)",    // xám nhạt
                        "rgba(107, 114, 128, 1)"       // xám đậm
                )
        );

    }

    @Getter
    @Setter
    private static class Dataset{
        String label;
        List<Integer> data;
        String backgroundColor ;
        String borderColor;
        int borderWidth;
        boolean hidden;

        public Dataset(String label, List<Integer> data, String backgroundColor, String borderColor) {
             borderWidth= 1;
             hidden =  false;
             this.label = label;
             this.data = data;
             this.backgroundColor = backgroundColor;
             this.borderColor = borderColor;

        }
    }
}
