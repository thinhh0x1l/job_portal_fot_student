package com.jobportal.dto.dummy;

import com.jobportal.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowViewPj {
    int totalViewCount;
    int loggedInUserViewCount;
    int anonymousViewCount;
    List<User> viewerList = new ArrayList<>();
}
