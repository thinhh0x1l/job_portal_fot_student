package com.jobportal.dto.dummy;

import com.jobportal.entity.PostJob;
import com.jobportal.entity.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;



@Getter
@Setter
public class ScoredJob {
    private PostJob job;
    private int matchCount;
    private Set<Tag> tags;
    public ScoredJob(PostJob job, int matchCount ,Set<Tag> tags) {
        this.job = job;
        this.matchCount = matchCount;
        this.tags = tags;
    }
}
