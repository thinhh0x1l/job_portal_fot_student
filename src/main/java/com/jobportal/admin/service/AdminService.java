package com.jobportal.admin.service;


import com.jobportal.dto.response.DetailPostJobRes;
import com.jobportal.entity.*;
import com.jobportal.model.TagType;
import com.jobportal.repostory.*;
import com.jobportal.service.MailerService;
import com.jobportal.service.NotificationService;
import com.jobportal.service.OnlineService;
import com.jobportal.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jasypt.util.text.BasicTextEncryptor;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AdminService {
    private final PrettyTime p = new PrettyTime(new Locale("vi"));
    MailerService mailer;
    UserRepository userRepository;
    OnlineService onlineService;
    NotificationRepository notificationRepository;
    SendOutCvRepository sendOutCvRepository;
    TaskRepository taskRepository;
    BasicTextEncryptor stringEncryptor;
    PostJobRepository postJobRepository;
    TagRepository tagRepository;
    RecruiterRepository recruiterRepository;
    UserService userService;
    InternRepository internRepository;
    AiSystemRepository aiSystemRepository;
    NotificationService notificationService;
    public List<Notification> getNotificationByAdmin(){
        return notificationRepository.findByAdmin(LocalDateTime.now().minusWeeks(2));
    }
    public Integer countNotSeenByAdmin(){
        return notificationRepository.countNotSeenByAdmin(LocalDateTime.now().minusWeeks(2));
    }

    public void updateSeen(){
        notificationRepository.updateSeen(LocalDateTime.now().minusWeeks(2));
    }
    public void updateSeenByOne(int id){
        notificationRepository.updateSeenByOne(LocalDateTime.now().minusWeeks(2),id);
    }

    public List<Tag> getAllTags(String typeTag){
        if(typeTag.equals("0")){
            return tagRepository.findByAllTag();
        }
        return tagRepository.findByAllTagByTypeTag(TagType.valueOf(typeTag));
    }

    public void newTag(Tag tag){
        tagRepository.save(tag);
    }
    public void updateTag(int idTag, String tagType, String tagName){
        Tag tag = tagRepository.findById(idTag).get();
        tag.setTagType(TagType.valueOf(tagType));
        tag.setName(tagName);
    }
    public void deleteTag(int idTag){
        Tag tag = tagRepository.findById(idTag).get();;
        internRepository.findAll().forEach(
                i-> i.getTags().remove(tag)
        );
        // Cắt liên kết với các post
        List<PostJob> posts = postJobRepository.findAll();
        for (PostJob post : posts) {
            post.getTags().remove(tag);
        }
        tagRepository.delete(tag);
    }

    public final static int PAGE_POSTJOB = 12;
    public Page<PostJob> getPostJob(int page,int cty, int status){
        boolean approved = false;
        boolean hidden = false;
        if(status ==2)
            hidden = true;
        else if (status == 3)
            approved = true;
        if(cty == 0){
            if(status == 0){
                return postJobRepository.
                        findAllStatsuAndCty(PageRequest.of(page-1,PAGE_POSTJOB, Sort.by("id").descending()));
            }else{
                return postJobRepository.findAllStatsuAndCty(approved,hidden,
                        PageRequest.of(page-1,PAGE_POSTJOB, Sort.by("id").descending()));
            }
        } else{
            if(status == 0){
                return postJobRepository.findAllStatsuAnd1Cty(cty,
                        PageRequest.of(page-1,PAGE_POSTJOB, Sort.by("id").descending()));
            }
            return postJobRepository.findStatsuAnd1Cty(approved,hidden, cty,
                    PageRequest.of(page-1,PAGE_POSTJOB, Sort.by("id").descending()));
        }
    }

    public List<Recruiter> getAllRecruiter(){
        return recruiterRepository.recruitersByCompany();
    }

    public DetailPostJobRes detailPostJobRes(int id){
        PostJob postJob = postJobRepository.findById(id).get();
        DetailPostJobRes detailPostJobRes = new DetailPostJobRes(
                postJob.getRecruiter().getCompany().getCompanyName(),
                postJob.getRecruiter().getCompany().getDistrict().getDisplayName()+", TpHCM",
                "/images/companies/"+postJob.getRecruiter().getCompany().getId()+"/"+postJob.getRecruiter().getCompany().getImageUrl(),
                postJob.getName(),
                p.format(postJob.getTimePost()),
                postJob.getDescription(),
                postJob.getCandidateRequirements(),
                postJob.getBenefits(),
                postJob.statusV4()
        );

        for (Tag tag : postJob.getTags())
            detailPostJobRes.getTagJobList().add(detailPostJobRes.new TagJob(tag.getName(),tag.getColorClassV1()));

        return detailPostJobRes;
    }
    public void approvePostJob(int id, String status,String note){
        PostJob postJob = postJobRepository.findById(id).get();
        String content;
        Recruiter r = recruiterRepository.findById(postJob.getRecruiter().getId()).get();
        if(status.equals("1")){
            postJob.setApproved(true);
            content="Bài tuyển dụng <strong>"+postJob.getName()+"</strong> đã được phê duyêt.<br>"+
                    (note.isEmpty()?"":"<strong>Lời nhắc của Admin</strong> "+note);
        }else {
            postJob.setHidden(true);
            content="Bài tuyển dụng <strong>"+postJob.getName()+"</strong> đã bị từ chối.<br>"+
                    (note.isEmpty()?"":"<strong>Lời nhắc của Admin</strong> "+note);
        }
        userService.notification(
                content,
                "/recruiter/quanly-baidang/"+id,
                "/images/admin/img.png",
                null,
                r
        );
    }

    public AiSystem getAisystem(){
        return aiSystemRepository.findById(1).get();
    }
    public void updateApiKey(String apikey){
        AiSystem ai =  aiSystemRepository.findById(1).get();
        ai.setApiKey(apikey);
        ai.setExpire(false);
    }
    public void updateBody(String body){
        AiSystem ai =  aiSystemRepository.findById(1).get();
        System.out.println(body);
        ai.setBodyContent(body);
        ai.setExpire(false);
    }

    public void aiBad(){
        AiSystem ai =  aiSystemRepository.findById(1).get();

        if(!ai.isExpire()){
            ai.setExpire(true);
            notificationService.notification(
                "Hệ thống AI của bạn gặp Sự có hãy sữa nó",
                    "/admin/manage-ai",
                    "/images/admin/aino1.png",
                    null,
                    null
            );
        }
    }


//    public void updateAisystem(Object object){
//        AiSystem system = getAisystem();
//        AiSystem
//    }
}
