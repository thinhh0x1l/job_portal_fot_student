package com.jobportal.dto.request;

import com.jobportal.entity.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class SuggestJobs {
    private Integer id;
    private String name;
    int matchCount;
    String tag;
    public SuggestJobs(Integer id, String name, int matchCount, Set<Tag> tags) {
        this.id = id;
        this.name = name;
        this.matchCount = matchCount;
        StringBuilder sb = new StringBuilder("[");
        for (Tag tag : tags){
            sb.append(tag.getName()).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("]");
        this.tag = sb.toString();
    }
}
