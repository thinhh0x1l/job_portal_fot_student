package com.jobportal.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TagDetailsRequest {
    Set<Integer> tagIds;
}
