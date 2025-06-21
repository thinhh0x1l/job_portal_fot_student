package com.jobportal.service.recruiter;

import com.jobportal.dto.request.PostJobUpdate;
import com.jobportal.entity.PostJob;
import com.jobportal.entity.SendOutCv;
import com.jobportal.entity.Tag;
import com.jobportal.model.District;
import com.jobportal.model.Major;
import com.jobportal.model.Salary;
import com.jobportal.repostory.OffsetBasedPageRequest;
import com.jobportal.repostory.PostJobRepository;
import com.jobportal.repostory.TagRepository;
import com.jobportal.security.CustomUserDetails;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class PostJobService {
    public final static int HOMEPAGE_INTERN = 9;
    public final static int RECOMMEND = 6;

    PostJobRepository postJobRepository;
    TagRepository tagRepository;
    public Page<PostJob> getAll(String district, String salary, String major, String keyword,
                                int urgent, Set<PostJob> postJobs, int page) {
        List<Boolean> list = Stream.of(1,urgent).map(i -> i == 1).toList();
        Set<Integer> jobIds = postJobs==null ?  new HashSet<>()
                :postJobs.stream().map(PostJob::getId).collect(Collectors.toSet());

        Pageable pageable = PageRequest.of(page-1,HOMEPAGE_INTERN,Sort.by("id").descending());

        if(district.equals("0") && major.equals("0") && salary.equals("0"))
            return postJobRepository.searchByJobOrCompany(LocalDate.now() ,jobIds,pageable,keyword,list);
        if(!district.equals("0"))
            return postJobRepository.findByDistrict(District.valueOf(district),LocalDate.now(),jobIds ,pageable,keyword,list);
        if(!salary.equals("0"))
            return postJobRepository.findBySalary(Salary.valueOf(salary).getMin(), Salary.valueOf(salary).getMax(),
                    LocalDate.now(),jobIds ,pageable,keyword,list);
        return postJobRepository.findByMajor(Major.valueOf(major),LocalDate.now(),jobIds ,pageable,keyword,list);
    }

    public Set<PostJob> relatedJobs(Integer id,String keyword){
        return postJobRepository.relatedJobs(id, RandomString.make(10),RECOMMEND, keyword,LocalDate.now());
    }

    public PostJob updatePostJob(PostJobUpdate postJob) {
        PostJob persistent = postJobRepository.findById(postJob.getId()).orElse(null);
        assert persistent != null;
        Set<Tag> tags = tagRepository.findByTagByIds(postJob.getTagIds());
        persistent.setName(postJob.getName());
        persistent.setTags(tags);
        persistent.setDescription(postJob.getDescription());
        persistent.setBenefits(postJob.getBenefits());
        persistent.setSalary(postJob.getSalary());
        persistent.setMajor(Major.valueOf(postJob.getMajor()));
        persistent.setUrgent(postJob.isUrgent());
        persistent.setCandidateRequirements(postJob.getCandidateRequirements());
        persistent.setUpdatedTime(LocalDateTime.now());
        postJobRepository.save(persistent);
        return null;
    }




}
