package com.jobportal.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class MajorsCtx {
    List<String> labels;
    List<Integer> data;
    public MajorsCtx() {
        this.labels = new ArrayList<>();
        this.data = new ArrayList<>();
    }
}
