package com.jobportal.service.intern;

import com.jobportal.dto.dummy.ScoredJob;
import com.jobportal.dto.request.*;
import com.jobportal.dto.response.DetailStudent;
import com.jobportal.dto.response.NotificationDTO;
import com.jobportal.dto.response.UpdateTagsRes;
import com.jobportal.entity.*;
import com.jobportal.model.*;
import com.jobportal.repostory.*;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.MailerService;
import com.jobportal.service.NotificationService;
import com.jobportal.service.OnlineService;
import com.jobportal.service.UserService;
import com.jobportal.utils.FileUploadUtil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.jobportal.service.recruiter.PostJobService.HOMEPAGE_INTERN;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class InternService {
    InternRepository internRepository;
    CompanyRepository companyRepository;
    PasswordEncoder encoder;
    MailerService mailer;
    OnlineService onlineService;
    UserService userService;
    PostCvRepository postCvRepository;
    PostJobRepository postJobRepository;
    SendOutCvRepository sendOutCvRepository;
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    RecruiterRepository recruiterRepository;
    TaskRepository taskRepository;
    LecturerRepository lecturerRepository;
    TagRepository tagRepository;
    ProjectInternRepository projectInternRepository;
    SimpMessagingTemplate messagingTemplate;
    NotificationService notificationService;

    public Intern register(Intern intern, String url, Map<String, byte[]> files) throws MessagingException, IOException {
        intern.setStatus(InternshipStatus.NOT_PRACTICED);
        intern.setPassword(encoder.encode(intern.getPassword()));
        intern.setProvider(AuthProvider.DATABASE);
        intern.setRole(Role.INTERN);
        return internRepository.findById(userService.sendVerifyEmail(intern, url,files)).get();
    }
    public Intern register(Intern intern,int lecturerId) {
        System.out.println("i "+intern.getId());
        Lecturer lecturer = lecturerRepository.findById(lecturerId).get();
        if(intern.getId()!=null) {
            Intern I  = internRepository.findById(intern.getId()).get();
            I.getLecturer().removeIntern(I);
            lecturer.addIntern(I);
            if(intern.getPassword()!=null&&!intern.getPassword().isEmpty()) {
                I.setPassword(encoder.encode(intern.getPassword()));
            }
            I.setPhoneNumber(intern.getPhoneNumber());
            I.setFirstName(intern.getFirstName());
            I.setLastName(intern.getLastName());
            I.setEmail(intern.getEmail());
            if(!intern.getImageUrl().isEmpty())
                I.setImageUrl(intern.getImageUrl());
            return I;
        }
        lecturer.addIntern(intern);

        intern.setStatus(InternshipStatus.NOT_PRACTICED);
        intern.setPassword(encoder.encode(intern.getPassword()));
        intern.setProvider(AuthProvider.DATABASE);
        intern.setRole(Role.INTERN);
        return internRepository.save(intern);
    }
    public int updateAvatar(String imageName) {
        Intern i = internRepository.findById(getIntern().getId()).get();
        i.setImageUrl(imageName);
        return i.getId();
    }

    public int updateCover(String imageCover){
        Intern i = internRepository.findById(getIntern().getId()).get();
        i.setImageCover(imageCover);
        return i.getId();
    }


    public PostCv postCv(PostCv postCv, String filename){
        Intern internPersistent = internRepository.findById(getIntern().getId()).get();
        if(internPersistent.getPostCv()!= null){
            postCvRepository.delete(internPersistent.getPostCv());
            internPersistent.setPostCv(null);
        }
        postCv.setTimePost(LocalDateTime.now());
        postCv.setFileCvUrl(filename);
        internPersistent.addPostCv(postCv);
        return postCvRepository.save(postCv);
    }

    public List<Intern> getInterns(){
        return internRepository.findAll();
    }
    public Intern getInternById(int id){
        return internRepository.findById(id).get();
    }

    public Intern getIntern(Principal principal) {
        String username = principal.getName();
        return internRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy intern"));
    }
    public Intern getIntern(){
        if(SecurityContextHolder.getContext().getAuthentication() == null)
            System.out.println("Authentication is null");
        else if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() == null)
            System.out.println("getPrincipal is null");
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return onlineService.getInternAccounts().get(((CustomUserDetails)principal).getUsername());
    }

    public void addFavorites(Integer postJobId){
        PostJob postJob = postJobRepository.findById(postJobId).get();
        Intern intern = internRepository.findById(getIntern().getId()).get();
        intern.addFavorites(postJob);
        internRepository.save(intern);
    }
    public void removeFavorite(Integer postJobId) {
        PostJob postJob = postJobRepository.findById(postJobId).orElseThrow();
        Intern intern = internRepository.findById(getIntern().getId()).orElseThrow();

        intern.removeFavorites(postJob);
        internRepository.save(intern); // chỉ cần save phía chủ sở hữu
    }
    public Intern getInternWithFavorites(Integer internId) {
        return internRepository.findByIdWithFavorites(internId)
            .orElseThrow(() -> new RuntimeException("Intern not found"));
    }

    public int sendOutCv(Integer internId,Integer jobId, SendOutCv sendOutCv,String filename){
        PostJob p = postJobRepository.findById(jobId).orElseThrow();
        Intern intern = internRepository.findById(internId).orElseThrow();
        sendOutCv.setIntern(intern);
        sendOutCv.setFileCvUrl(filename);
        sendOutCv.setPostJob(p);
        sendOutCv.setTimeSent(LocalDateTime.now());
        sendOutCv.setCvStatus(CVStatus.PENDING_APPROVAL);
        sendOutCvRepository.save(sendOutCv);
        notificationSendCv(sendOutCv);
        return intern.getId();
    }
    public int sendOutCv2(Integer id, SendOutCvDto sendOutCvDto){
        SendOutCv sv = new SendOutCv();
        sv.setEmail(sendOutCvDto.getEmail());
        sv.setFullname(sendOutCvDto.getFullname());
        sv.setIntroduction(sendOutCvDto.getIntroduction());
        sv.setPhoneNumber(sendOutCvDto.getPhoneNumber());
        return sendOutCv(id,sendOutCvDto.getJobId(),sv,sendOutCvDto.getFileCvUrl());
    }

    public Page<SendOutCv> findTopByInternId(Integer internId) {
        return sendOutCvRepository.findTopByInternId(internId,
                PageRequest.of(0,1, Sort.by("id").descending()));
    }

    public Integer countByInternIdAndJobPostId(Integer internId, Integer jobPostId){
        return sendOutCvRepository.countByInternIdAndJobPostId(internId, jobPostId);
    }

    public List<SendOutCv> findTopByInternId2(Integer internId, int status){
        if(status == 0)
            return sendOutCvRepository.findTopByInternId(internId);
        return sendOutCvRepository.findByInternIdAndSeen(internId,status ==2);
    }
    public Integer getAllCVByInternId(){
        return sendOutCvRepository.getAllCVByInternId(getIntern().getId());
    }



    public Map<String, Object> internProperties() {
        Map<String, Object> map = new HashMap<>();
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if(a==null || a instanceof AnonymousAuthenticationToken )
            return map;
        boolean hasRoleX = a.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_INTERN"));
        if(!hasRoleX)
            return map;
        Intern i = internRepository.findById(getIntern().getId()).get();
        map.put("fullnamei", i.getLastName() + " " + i.getFirstName());
        map.put("imageUrli", "/images/user-photos/" + i.getId() + "/" + i.getImageUrl());
        map.put("emaili", i.getEmail());
        map.put("idi", i.getId());
        //   map.put("postJobs", r.getPostJobs());
        return map;
    }
    private void notificationSendCv(SendOutCv cv){
        Notification notification = new Notification();
        User sender = userRepository.findById(cv.getIntern().getId()).get();
        User receiver = userRepository.findById(cv.getPostJob().getRecruiter().getId()).get();
        notificationService.notification(
                "Ứng viên " + sender.getFirstName()+" "+sender.getLastName()+
                        " đã ứng tuyển công việc ["+cv.getPostJob().getName()+"]",
                "/recruiter/chitiet-cv/"+cv.getId(),
                "user-photos/",
                sender,
                receiver
        );
    }

    public void rejectCv(Integer cvId){
        SendOutCv cv = sendOutCvRepository.findById(cvId).get();
        User receiver = userRepository.findById(cv.getPostJob().getRecruiter().getId()).get();
        Intern sender = internRepository.findById(cv.getIntern().getId()).get();
        notificationService.notification(
                "Ứng viên " + sender.getFirstName()+" "+sender.getLastName()+
                        " đã <strong>Từ Chối</strong> tuyển công việc ["+cv.getPostJob().getName()+"]",
                "/recruiter/chitiet-cv/"+cv.getId(),
                "user-photos/",
                sender,
                receiver);
        sendOutCvRepository.rejectCv(cvId,CVStatus.REJECTED);
    }
    private void notificationSendCv(String content,
                                            String url,String image, User sender, User receiver){
        notificationService.notification(
                content,
                url,
                image,
                sender,
                receiver);

    }

    public void hired(Integer cvId){
        SendOutCv cv = sendOutCvRepository.findById(cvId).get();

        User receiver = userRepository.findById(cv.getPostJob().getRecruiter().getId()).get();
        Intern sender = internRepository.findById(cv.getIntern().getId()).get();
        sender.setDateToInternShip(LocalDate.now());
        sender.setStatus(InternshipStatus.IS_PRACTICING);
        notificationService.notification(
                "Chúc mừng Ứng viên " + sender.getFirstName()+" "+sender.getLastName()+
                        " đã <strong>Chấp nhận</strong> tuyển công việc ["+cv.getPostJob().getName()+"]",
                "/recruiter/chitiet-cv/"+cv.getId(),
                "user-photos/",
                sender,
                receiver);
        sendOutCvRepository.rejectCv(cvId,CVStatus.HIRED);
        Company company = companyRepository.findById(cv.getPostJob().getRecruiter().getId()).get();
        company.addIntern(sender);
    }

    public boolean checkCompany(){
        return internRepository.existsByIdAndCompanyIsNull(getIntern().getId());
    }

    public Page<Task> getTaskWithWeedByIntern(int page ,String status,String week){
        Pageable pageable = PageRequest.of(page - 1, HOMEPAGE_INTERN, Sort.by("id").descending());
        List<SecWeeks> listWeek = new ArrayList<>(List.of(SecWeeks.values()));
        List<TaskStatus> listTaskStatus = new ArrayList<>(List.of(TaskStatus.values()));
        if(!status.isEmpty())
            listTaskStatus = List.of(TaskStatus.valueOf(status));
        if(!week.isEmpty())
            listWeek = List.of(SecWeeks.valueOf(week));

        return taskRepository.getTaskWithWeedByIntern(getIntern().getId(),listTaskStatus,listWeek,pageable);
    }



    public Object submitReport(Integer taskId, MultipartFile file,String studentComment){
        Task task = userService.getTaskById(taskId);
        Intern intern = internRepository.findById(getIntern().getId()).get();
        Recruiter recruiter = recruiterRepository.findById(task.getRecruiter().getId()).get();

        task.setSubmitTime(LocalDateTime.now());
        task.setTaskStatus(task.getEndTime().isAfter(task.getSubmitTime())?
                TaskStatus.DA_HOAN_THANH:TaskStatus.HOAN_THANH_TRE);
        String uploadDir = "photos/reports/"+task.getId();
        task.setReport(file.getOriginalFilename());
        task.setDescription(studentComment);
        FileUploadUtil.saveFile(uploadDir,file.getOriginalFilename(),file);

        notificationService.notification(
                "Thực tập sinh " + intern.getFirstName()+" "+intern.getLastName() +
                        " đã <strong>Nộp bài ["+task.getName()+"]</strong> bạn hay nhanh chóng chấm điểm!",
                "/recruiter/nhiem-vu/"+task.getId(),
                "user-photos/",
                intern,
                recruiter);
        return null;
    }
    public Object cancelReport(Integer taskId){
        Task task = userService.getTaskById(taskId);
        task.setTaskStatus(TaskStatus.CHUA_HOAN_THANH);
        String uploadDir = "photos/reports/"+task.getId();
        task.setReport(null);
        FileUploadUtil.cleanDir(uploadDir);
        return null;
    }

    public List<SendOutCv> recruitedByInternId(){
        return sendOutCvRepository.recruitedByInternId(getIntern().getId(), CVStatus.SUCCESS);
    }

    public Company getCompany(int id) {
        return companyRepository.findById(id).get();
    }

    public DetailStudent getDetailStudent(int id) {
        Intern intern = internRepository.findById(id).get();
        Recruiter r =null;
        DetailStudent d = new DetailStudent();
        if(intern.getCompany()!=null) {
            r = recruiterRepository.findById(intern.getCompany().getId()).get();
            d.setCompanyName(r.getCompany().getCompanyName());
            List<SendOutCv> cv = sendOutCvRepository.a(id,CVStatus.HIRED);
            if(!cv.isEmpty()){
                d.setPositionApplied(cv.getFirst().getPostJob().getMajor().getLabe2l()+" Intern");
            }
        }
        d.setLecturerId(intern.getLecturer().getId());
        d.setLastname(intern.getLastName());
        d.setFirstname(intern.getFirstName());
        d.setImage("/images/user-photos/"+id+"/"+intern.getImageUrl());
        d.setPointInternShip(intern.getPointInternShip()==null?null:intern.getPointInternShip().toString());
        d.setReviewOfLecturer(intern.getReviewOfLecturer().getLabel());
        d.setStatusInternShip(intern.getStatus().getLabel());
        d.setClassAndFaculty(intern.getLop()+" - "+
                (intern.getLecturer().getFaculty().equals(Faculty.INFORMATION_SECURITY)?"An toàn thông tin":"Công nghệ phần mềm"));
        d.setMssv(intern.getMSSV());
        d.setFullname(intern.getFirstName() + " " + intern.getLastName());
        d.setEmail(intern.getEmail());
        d.setPhone(intern.getPhoneNumber());
        d.setLecturerName(intern.getLecturer().getFirstName() + " " + intern.getLecturer().getLastName());
        return d;
    }


    public String descriptionDetail(int internId){
        if(internId == 0)
            internId = getIntern().getId();
        Intern intern = internRepository.findById(internId).get();
        return intern.getDescription();
    }
    public void updateDescriptionDetail(UpdateDescriptionDetailRequest updateDescriptionDetailRequest){
        if(updateDescriptionDetailRequest.getInternId() == 0)
            updateDescriptionDetailRequest.setInternId( getIntern().getId());
        Intern intern = internRepository.findById(updateDescriptionDetailRequest.getInternId()).get();
        intern.setDescription(updateDescriptionDetailRequest.getDescription());
    }
    public List<ProjectIntern> projectInternDetail(int internId){
        if(internId == 0)
            internId = getIntern().getId();
        Intern intern = internRepository.findById(internId).get();
        return intern.getProjectInterns();
    }
    public ProjectIntern projectInternDetailById(int idProject){
        return projectInternRepository.findById(idProject).get();
    }
    public Intern getInternByIdOrNo(int internId){
        if(internId == 0)
            internId = getIntern().getId();
        return internRepository.findById(internId).get();
    }
    public void deleteProjectInternDetailById(int idProject){
        Intern intern = internRepository.findById(getIntern().getId()).get();
        ProjectIntern projectIntern = projectInternRepository.findById(idProject).get();
        intern.getProjectInterns().remove(projectIntern);
        projectInternRepository.delete(projectIntern);
    }
    public void updateProjectInternDetail(ProjectInternRequest projectInternRequest){
        if(projectInternRequest.getProjectId()==null){
            ProjectIntern pi = new ProjectIntern();
            pi.setName(projectInternRequest.getProjectName());
            pi.setDescription(projectInternRequest.getProjectDescription());
            pi.setLinkGit(projectInternRequest.getLinkGit());
            Intern intern = internRepository.findById(getIntern().getId()).get();
            intern.getProjectInterns().add(pi);
            projectInternRepository.save(pi);
            return;
        }
        ProjectIntern pi = projectInternRepository.findById(projectInternRequest.getProjectId()).get();
        pi.setName(projectInternRequest.getProjectName());
        pi.setDescription(projectInternRequest.getProjectDescription());
        pi.setLinkGit(projectInternRequest.getLinkGit());
    }
    public UpdateTagsRes getTagDetail(int internId){
        if(internId == 0)
            internId = getIntern().getId();
        Intern intern = internRepository.findById(internId).get();
        Set<Tag> tags = intern.getTags();
        com.jobportal.dto.response.UpdateTagsRes updateTag = new UpdateTagsRes();
        for(Tag tag : tags){
            UpdateTagsRes.TypeTagRes typeTag = new UpdateTagsRes.TypeTagRes();
            typeTag.setId(tag.getId());
            typeTag.setName(tag.getName());
            typeTag.setClassName(tag.getColorClassV1());
            typeTag.setChecked("peer-checked:text-white px-1 rounded-full transition "+tag.peerChecked());
            if(tag.getTagType().equals(TagType.PROGRAMMING_LANGUAGE))
                updateTag.getProgrammingLanguage().add(typeTag);
            else if(tag.getTagType().equals(TagType.BACKEND))
                updateTag.getBackend().add(typeTag);
            else if(tag.getTagType().equals(TagType.FRONTEND))
                updateTag.getFrontend().add(typeTag);
            else if(tag.getTagType().equals(TagType.DATABASE))
                updateTag.getDatabase().add(typeTag);
            else
                updateTag.getOrder().add(typeTag);
        }
        return updateTag;
    }

    public void updateTagDetail(TagDetailsRequest tagDetailsRequest){
        Intern intern = internRepository.findById(getIntern().getId()).get();
        Set<Tag> tags = tagRepository.findByTagByIds(tagDetailsRequest.getTagIds());
        intern.setTags(tags);
    }


    public List<SuggestJobs> suggestJobsWithMatchCount(Integer id) {
        Intern intern = internRepository.findById(id).orElse(null);
        if (intern == null) return Collections.emptyList();

        Set<Tag> internTags = intern.getTags();
        if (internTags == null || internTags.isEmpty())
            return Collections.emptyList();

//        long percentTag = Math.max(Math.round(internTags.size() * 0.3), 1);

        List<ScoredJob> scoredJobs = postJobRepository.findByApprovedAndPreEndTime(LocalDate.now()).stream()
                .map(job -> {
                    Set<Tag> jobTags = new HashSet<>(job.getTags());
                    jobTags.retainAll(internTags); //
                    int matchCount = jobTags.size();
                    return new ScoredJob(job, matchCount, jobTags); //
                })
                .filter(scored -> scored.getMatchCount() >= 1)
                .sorted(Comparator.comparingInt(ScoredJob::getMatchCount).reversed())
                .toList();

        int size = scoredJobs.size();
        if (size == 0) return Collections.emptyList();

        int to = (int) (Math.random() * (size + 1));
        int from = Math.max(to - 10, 0);

        return scoredJobs.subList(from, to).stream()
                .map(scored -> new SuggestJobs(
                        scored.getJob().getId(),
                        scored.getJob().getName(),
                        scored.getMatchCount(),
                        scored.getTags() // this is the matched tag set
                ))
                .collect(Collectors.toList());
    }




    private int getMatchingSkillCount(PostJob job, Intern intern) {
        Set<Tag> jobSkills = new HashSet<>(job.getTags());
        Set<Tag> studentSkills = new HashSet<>(intern.getTags());

        jobSkills.retainAll(studentSkills); // giữ lại phần giao
        return jobSkills.size();
    }
}
