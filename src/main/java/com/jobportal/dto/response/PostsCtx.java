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
public class PostsCtx  {
    List<String> labels;
    List<Data> data = new ArrayList<Data>();
    public PostsCtx(List<String> labels, List<Integer> data1,List<Integer> data2) {
        this.labels = labels;
        data.add(new Data(
                 "Đang hoạt động",
                data1,
                "rgba(59, 130, 246, 0.1)",
                "rgba(59, 130, 246, 0.7)"
        ));
        data.add(new Data(
                "Đã quá hạn",
                data2,
                "rgba(59, 130, 246, 0.1)",
                "rgba(156, 163, 175, 0.7)")
        );
    }

    @lombok.Data
    public class Data {
        String label;
        List<Integer> data;
        String backgroundColor;
        String borderColor;
        int borderWidth= 2;
        double tension= 0.3;
        boolean fill=true;

        public Data( String label, List<Integer> data, String backgroundColor, String borderColor) {
            this.label = label;
            this.data = data;
            this.backgroundColor = backgroundColor;
            this.borderColor = borderColor;
        }
    }
}
