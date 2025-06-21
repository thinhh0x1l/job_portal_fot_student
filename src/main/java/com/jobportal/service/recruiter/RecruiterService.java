package com.jobportal.service.recruiter;

import com.jobportal.dto.dummy.*;
import com.jobportal.dto.request.AddTaskRequest;
import com.jobportal.dto.request.JobPostRequest;
import com.jobportal.dto.request.TaskRequest;
import com.jobportal.dto.response.*;
import com.jobportal.entity.*;
import com.jobportal.model.*;
import com.jobportal.repostory.*;
import com.jobportal.security.CustomDefaultOidcUser;
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
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class RecruiterService {
    public final static int PAGE_SEEN_OUT_CV=9;
    private final PrettyTime p = new PrettyTime(new Locale("vi"));
    RecruiterRepository recruiterRepository;
    CompanyRepository companyRepository;
    PostJobRepository postJobRepository;
    PasswordEncoder encoder;
    MailerService mailer;
    OnlineService onlineService;
    UserService userService;
    PostCvRepository postCvRepository;
    SendOutCvRepository sendOutCvRepository;
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    InternRepository internRepository;
    TaskRepository taskRepository;
    TagRepository tagRepository;
    NotificationService notificationService;
    public Recruiter register(RegisterRecruiter r1, String url, Map<String, byte[]> files,String fileName) throws MessagingException, IOException {
        Recruiter recruiter = new Recruiter();
        String[] fullname = r1.getFullName().split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< fullname.length-1;i++)
            sb.append(fullname[i]).append(" ");
        recruiter.setLastName(fullname[fullname.length-1]);
        recruiter.setFirstName(sb.toString().trim());

        recruiter.setImageUrl(fileName);
        recruiter.setPhoneNumber(r1.getPhoneNumber());
        recruiter.setEmail(r1.getEmail());
        recruiter.setPassword(encoder.encode(r1.getPassword()));
        recruiter.setProvider(AuthProvider.DATABASE);
        recruiter.setRole(Role.RECRUITER);
        Recruiter recruiterSaved = recruiterRepository.findById(userService.sendVerifyEmail(recruiter, url, files)).get();
        createCompany(r1, recruiterSaved);
        return recruiterSaved;
    }

    private void createCompany(RegisterRecruiter r1,Recruiter recruiterSaved){
        Company company = new Company();
        company.setCompanyName(r1.getCompanyName());
        company.setDistrict(District.valueOf(r1.getDistrict()));
        recruiterSaved.setCompany(company);
        companyRepository.save(company);
    }

    public Company updateCompany(UpdateCompany updateCompany, String fileName){
        Company company = companyRepository.findById(getRecruiter().getId()).get();
        Recruiter r = recruiterRepository.findById(getRecruiter().getId()).get();
        company.setCompanyName(updateCompany.getCompanyName());
        company.setDescription(updateCompany.getDescription());
        company.setNumberPhone(updateCompany.getNumberPhone());
        company.setEmail(updateCompany.getEmail());
        company.setAddress(updateCompany.getAddress());
        company.setCompanySize(CompanySize.valueOf(updateCompany.getCompanySize()));
        company.setWebsite(updateCompany.getWebsite());
        company.setTaxCode(updateCompany.getTaxCode());
        company.setCheckInformation(false);
        company.setDateInformationUpdated(LocalDateTime.now());
        if(!fileName.isEmpty())
            company.setImageUrl(fileName);
        ///nofi
        notificationService.notification(
                "Nhà tuyển dụng <em>"+r.getFirstName()+" "+r.getLastName()+"</em> vừa cập nhật <strong>Thông in công ty</strong> hãy xét duyệt",
                "/admin/manage-company#companyDetailModal-"+r.getId(),
                "/images/user-photos/"+r.getId()+"/",
                r,
                null
        );
        return company;
    }
    public Company updateCertificate(String certificate){
        Recruiter r = recruiterRepository.findById(getRecruiter().getId()).get();
        Company company = companyRepository.findById(getRecruiter().getId()).get();
        company.setCertificate(certificate);
        company.setCheckCertificate(false);
        notificationService.notification(
                "Nhà tuyển dụng <em>"+r.getFirstName()+" "+r.getLastName()+"</em> vừa cập nhật <strong>Chứng chỉ công ty</strong> hãy xét duyệt",
                "/admin/manage-company#companyDetailModal-"+r.getId(),
                "/images/user-photos/"+r.getId()+"/",
                r,
                null
        );
        return company;
    }

    public Integer companyLevel(){
       Company c= companyRepository.findById(getRecruiter().getId()).get();
       return ((c.getCheckInformation()!=null&& c.getCheckInformation())?1:0 )+
               ((c.getCheckCertificate()!=null&& c.getCheckCertificate())?1:0 );
    }

    public boolean existsByEmail(String email) {
        return recruiterRepository.existsByEmail(email);
    }

    public Object createCompany(Company company) {
//        Recruiter recruiter = (recruiterRepository.findById(getRecruiter().getId()).get());
        Recruiter recruiter = getRecruiter();
        recruiter.setCompany(company);
        companyRepository.save(company);
        return null;
    }

    public Integer newPostJobInLastedWeek(){
        return postJobRepository.newPostJobInLastedWeek(getRecruiter().getId(),
                LocalDateTime.now().minusWeeks(1),
                LocalDate.now());
    }

    public Integer postJobApprove(){
        return postJobRepository.postJobApprove(getRecruiter().getId(),
                LocalDate.now());
    }


    public Object createPostJob(JobPostRequest request) {
        Recruiter recruiter = recruiterRepository.findById(getRecruiter().getId()).get();
        Set<Tag> tags = tagRepository.findByTagByIds(request.getTagIds());
        PostJob postJob = new PostJob();
        postJob.setTags(tags);
        postJob.setName(request.getName());
        postJob.setMajor(Major.valueOf(request.getMajor()));
        postJob.setDescription(request.getDescription());
        postJob.setCandidateRequirements(request.getCandidateRequirements());
        postJob.setSalary(request.getSalary());
        postJob.setPostingDeadline(request.getPostingDeadline());
        postJob.setBenefits(request.getBenefits());
        postJob.setUrgent(request.isUrgent());
        postJob.setNumberOfApplications(request.getNumberOfApplications());
        recruiter.addPostJob(postJob);
        postJob.setTimePost(LocalDateTime.now());
        postJob.setUpdatedTime(LocalDateTime.now());
        postJobRepository.save(postJob);
        notificationService.notification(
                "Nhà tuyển dụng <em>"+recruiter.getFirstName()+" "+recruiter.getLastName()+"</em> đã đăng bài tuyển dụng <strong>"+postJob.getName()+"</strong> hãy xét duyệt",
                "/admin/manage-postjob#jobDetailModa-"+postJob.getId(),
                "/images/user-photos/"+recruiter.getId()+"/",
                recruiter,
                null
        );
        return null;
    }

    public UpdateTagsRes getAllTagsToUpdate(){
        List<Tag> tags = tagRepository.findAll();
        UpdateTagsRes updateTag = new UpdateTagsRes();

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
    public void hiddenPostJob(int id ){
        PostJob j = postJobRepository.findById(id).get();
        j.setHidden(true);
    }

    public Map<String, Object> recruiterProperties() {
        Map<String, Object> map = new HashMap<>();
        Recruiter r = getRecruiter();
        if (r.getProvider().equals(AuthProvider.DATABASE)) {
            map.put("fullname", r.getLastName() + " " + r.getFirstName());
            map.put("imageUrl", "/images/user-photos/" + r.getId() + "/" + r.getImageUrl());
            map.put("email", r.getEmail());
            map.put("id", r.getId());
            map.put("rNumberPhone", r.getPhoneNumber());
            //   map.put("postJobs", r.getPostJobs());
            return map;
        }
        CustomDefaultOidcUser o = getOidcUser();
        map.put("fullname", o.getFullName());
        map.put("imageUrl", o.getPicture());
        map.put("email", o.getEmail());
        map.put("id", o.getUser().getId());
        //map.put("postJobs", r.getPostJobs());
        return map;
    }

    public Recruiter getRecruiter() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails)
            return recruiterRepository.findById(customUserDetails.getId()).get();
        return recruiterRepository.findById(((CustomDefaultOidcUser) principal).getUser().getId()).get();
    }

    private CustomDefaultOidcUser getOidcUser() {
        return (CustomDefaultOidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public PostJob getPostJobById(Integer id) {
        return postJobRepository.findById(id).get();
    }
    public List<Intern> findUniqueInternsSentCv(Integer id) {
        return sendOutCvRepository.findUniqueInternsSentCv(id);
    }

    public List<PostJob> getJobPostByRecruiterId() {
        return recruiterRepository.findById(getRecruiter().getId()).get().getPostJobs();
    }


    public Page<PostJob> getLikeAndStatus(String keyword, int status, int page) {
        if(status==5)
            return postJobRepository.findByLikeReject(keyword,getRecruiter().getId(),LocalDate.now()
                    ,PageRequest.of(page - 1, 9,Sort.by("id").descending()));
        if(status==4)
            return postJobRepository.findByHidden(keyword,getRecruiter().getId(),LocalDate.now()
                ,PageRequest.of(page - 1, 9,Sort.by("id").descending()));
        if (status == 0)
            return postJobRepository.findByLike(keyword, getRecruiter().getId(),LocalDate.now(),
                    PageRequest.of(page - 1, 9, Sort.by("id").descending()));
        if (status == 3)
            return postJobRepository.findByLikeAndAfter(keyword, LocalDate.now(), getRecruiter().getId()
                    , PageRequest.of(page - 1, 9, Sort.by("id").descending()));
        return postJobRepository.findByLikeAndApproved(keyword, status == 1, getRecruiter().getId(),
                LocalDate.now(), PageRequest.of(page - 1, 9, Sort.by("id").descending()));

    }

    public boolean checkAuthz(int rid, int id) {
        return postJobRepository.existsByRecruiterId(rid, id);
    }

    public Page<PostCv> getListPostCvByNot(String keyword, String major, int page) {
        if (major.equals("0"))
            return postCvRepository.findPostCvByNotTitle(keyword,
                    PageRequest.of(page - 1, 6, Sort.by("timePost").descending()));

        return postCvRepository.findPostCvByNotTitleAndMajor(keyword, Major.valueOf(major)
                , PageRequest.of(page - 1, 6, Sort.by("timePost").descending()));
    }

    public boolean existsByCompany (){
        return recruiterRepository.existsByCompany(getRecruiter().getId());
    }

    public void increaseView(Integer id){
        postJobRepository.increaseView(id);
    }

    public List<SendOutCv> getThreeInternLasted(){
        return sendOutCvRepository.getThreeInternLasted(getRecruiter().getId(),
                CVStatus.PENDING_APPROVAL,PageRequest.of(0,4,Sort.by("id").descending()));
    }

    public Page<SendOutCv> getSendOutCvByRecruiterId(  String likeId, String nameJob, String seen,
                                                       int page,int urgent, String tab, String pending){
        CVStatus cvStatus = CVStatus.PENDING_APPROVAL;
        if(tab.equals("tab2"))
            cvStatus = CVStatus.BEING_INTERVIEWED;
        likeId = likeId.equals("0")?"":likeId;
        List<Boolean> list1 = Stream.of(1,urgent).map(i -> i == 1).toList();
        List<Boolean> list = Stream.of(seen.charAt(0),seen.charAt(1)).map(i -> i.equals('1')).toList();
        Pageable pageable = PageRequest.of(page - 1, PAGE_SEEN_OUT_CV, Sort.by("timeSent").descending());
        if(tab.equals("tab3")){
            List<CVStatus> listCvStatus = new ArrayList<>(List.of(CVStatus.PENDING_APPROVAL,CVStatus.BEING_INTERVIEWED));
            if(!pending.equals("0")){
                listCvStatus = new ArrayList<>(List.of(CVStatus.values()));
                listCvStatus.remove(CVStatus.valueOf(pending));
            }
            return sendOutCvRepository.getByRecruiterIdTab3(getRecruiter().getId(), likeId,nameJob,list1, listCvStatus,pageable);
        }
        return sendOutCvRepository.getByRecruiterId(getRecruiter().getId(), likeId,nameJob,list,list1, cvStatus,pageable);
    }

    public List<PostJob> getAllByRecruiterId(){
        return postJobRepository.getAllByRecruiterId(getRecruiter().getId());
    }

    public Integer countApplicantsByPostJobId(Integer postJobId){
        return sendOutCvRepository.countApplicantsByPostJobId(postJobId,CVStatus.PENDING_APPROVAL);
    }

    public ShowViewPj showViewPj(int postJobId){
        Optional<PostJob> p =  postJobRepository.getByIdAndApproved(postJobId);
        return p.map(postJob -> new ShowViewPj(postJob.getView(), postJob.getUsers().size(), postJob.getView() - postJob.getUsers().size(), postJob.getUsers().stream().toList())).orElse(null);
    }

    public Integer getNumberOfCvsBySotInternIdAndRecruiterId(Integer internId){
        return sendOutCvRepository.getNumberOfCvsBySotInternIdAndRecruiterId(internId,getRecruiter().getId(),CVStatus.PENDING_APPROVAL);
    }

    public void seenCv(Integer cvId){
        sendOutCvRepository.seenCv(cvId);
    }

    public void rejectCv(Integer cvId){
        SendOutCv cv = sendOutCvRepository.findById(cvId).get();
        Recruiter sender = recruiterRepository.findById(cv.getPostJob().getRecruiter().getId()).get();
        User receiver = userRepository.findById(cv.getIntern().getId()).get();
        notificationService.notification(
                "Công việc ["+cv.getPostJob().getName()+"] mà bạn ứng tuyển đã bị Nhà tuyển dụng từ chối",
                "/chitiet-cv/"+cv.getId(),
                "companies/",
                sender,
                receiver);
        sendOutCvRepository.rejectCv(cvId,CVStatus.REJECTED);
    }

    public void beingInterviewedCv(Integer cvId,String cvStatus){
        SendOutCv cv = sendOutCvRepository.findById(cvId).get();
        Recruiter sender = recruiterRepository.findById(cv.getPostJob().getRecruiter().getId()).get();
        User receiver = userRepository.findById(cv.getIntern().getId()).get();
        String content = "";
        if(CVStatus.valueOf(cvStatus).equals(CVStatus.BEING_INTERVIEWED))
            content = "Chúc mừng...Công việc ["+cv.getPostJob().getName()+"] mà bạn ứng tuyển đã được chấp thuận." +
                    " Nhà tuyển dụng sẽ sớm liên hệ với bạn!!";


        Notification n =notificationService.notification(
                content,
                "/chitiet-cv/"+cv.getId(),
                "companies/",
                sender,
                receiver);
        if (CVStatus.valueOf(cvStatus).equals(CVStatus.SUCCESS)) {
            content = "Chúc mừng...Công việc [" + cv.getPostJob().getName() + "] mà bạn ứng tuyển đã phỏng vấn thành công." +
                    " Bạn hãy sớm phản hồi !!";
            n.setContent(content);
            n.setUrl("/intern/api/thong-bao-v3/"+cv.getId()+"/"+n.getId());
            cv.setUrl("/intern/api/thong-bao-v3/"+cv.getId()+"/"+n.getId());
            n.setImage("companies/"+sender.getId()+"/"+sender.getCompany().getImageUrl());
        }
        sendOutCvRepository.beingInterviewedCv(cvId,CVStatus.valueOf(cvStatus));
    }



    private Notification notificationSendCv(String content,
                                            String url,String image, User sender, User receiver){
        Notification notification = new Notification();
        notificationService.notification(content,url,image,sender,receiver);

        return notificationRepository.save(notification);
    }

    public Page<Intern> getInternByCompanyId(int page, String internShipStatus,String keyword){
        Pageable pageable = PageRequest.of(page - 1, PAGE_SEEN_OUT_CV, Sort.by("firstName").ascending());
        List<InternshipStatus> internshipStatuses= new ArrayList<>(List.of(InternshipStatus.NOT_PRACTICED));
        if(internShipStatus.equals("1"))
            internshipStatuses.add(InternshipStatus.COMPLETED);
        if(internShipStatus.equals("2"))
            internshipStatuses.add(InternshipStatus.IS_PRACTICING);
        return internRepository.findInternsByCompany(getRecruiter().getId(),internshipStatuses,keyword,pageable);
    }


    public List<Intern> getInterns(){
        return internRepository.getInternsByIn(getRecruiter().getId(),InternshipStatus.IS_PRACTICING);
    }

    public Object addTask(MultipartFile file, AddTaskRequest data){
        data.getInternId().forEach(id-> {
            Intern intern = internRepository.findById(id).get();
            Recruiter recruiter = recruiterRepository.findById(getRecruiter().getId()).get();
            Task task = Task.builder()
                    .task(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())))
                    .name(data.getTaskName())
                    .weeks(SecWeeks.valueOf(data.getWeek()))
                    .weekOrder(SecWeeks.valueOf(data.getWeek()).getOrder())
                    .timePost(LocalDateTime.now())
                    .endTime(data.getEndTime())
                    .taskStatus(TaskStatus.CHUA_HOAN_THANH)
                    .build();
            task.setIntern(intern);
            task.setRecruiter(recruiter);
            taskRepository.save(task);

            String uploadDir = "photos/task/"+task.getId();
            FileUploadUtil.saveFile(uploadDir,file.getOriginalFilename(),file);
            notificationService.notification(
                    "Nhà tuyển dụng đã giao cho bạn <strong>Nhiệm Vụ Mới ["+task.getName()+"]</strong>. Hãy thực hiện ngay",
                    "/nhiem-vu/"+task.getId(),
                    "user-photos/",
                    recruiter,
                    intern);
        });
        return null;
    }

    public Page<Task> getTask(int page ,String status,String keyword,String week){
        Pageable pageable = PageRequest.of(page - 1, PAGE_SEEN_OUT_CV, Sort.by("id").descending());
        List<SecWeeks> listWeek = new ArrayList<>(List.of(SecWeeks.values()));
        List<TaskStatus> listTaskStatus = new ArrayList<>(List.of(TaskStatus.values()));
        if(!status.isEmpty())
            listTaskStatus = List.of(TaskStatus.valueOf(status));
        if(!week.isEmpty())
            listWeek = List.of(SecWeeks.valueOf(week));

        return taskRepository.getTaskWithWeed(getRecruiter().getId(),InternshipStatus.IS_PRACTICING,
                keyword,listTaskStatus,listWeek,pageable);
    }

    public TaskStatusDum taskStatusDumM(){
        List<Task> tasks =taskRepository.getAllByInternshipStatus(getRecruiter().getId(),InternshipStatus.IS_PRACTICING);
        int _1 =0, _2 =0,_3=0,_4 = 0,_5 =0;
        for(Task t:tasks){
            _1++;
            if(t.getReport()!=null)
                _2++;
            if (t.getReport()!=null && t.getSubmitTime().isAfter(t.getEndTime()))
                _3++;
            if(t.getReport()==null)
                _4++;
            if(t.getReport()!=null&&t.getPoints()==null)
                _5++;
        }
        return new TaskStatusDum(_1,_2,_3,_4,_5);
    }
    public void reviewTask(TaskRequest request,Integer id){
        Task task = taskRepository.findById(id).get();
        task.setPoints(request.getPoints());
        task.setReview(request.getReview());
        Intern intern = internRepository.findById(task.getIntern().getId()).get();
        Recruiter recruiter = recruiterRepository.findById(task.getRecruiter().getId()).get();
        notificationService.notification(
                "Nhiệm vụ <strong>["+task.getName()+"]</strong> này bạn được <strong>["+task.getPoints()+" điểm]</strong>" +
                        " . Hãy xem ngay",
                "/nhiem-vu/"+task.getId(),
                "user-photos/",
                recruiter,
                intern);
    }

    public Point getTasksByInternId(Integer id){
        Map<String, List<Task>> map = new HashMap<>();
        Map<String, Double> point = new HashMap<>();
        List<Task> tasks = taskRepository.getTaskByInternshipId(id);
        int i = tasks.isEmpty()?0:tasks.getFirst().getWeekOrder();
        int z =0 ;
        double sum = 0;
        double sumAll = 0;
        int taskCompleted=0;
        Task taskDum = tasks.isEmpty()?null:tasks.getFirst();
        for(Task task : tasks ) {
            taskCompleted += task.getReport()!=null? 1:0;
            map.computeIfAbsent(task.getWeeks().label(), k -> new ArrayList<>()).add(task);
            if( i != task.getWeekOrder()) {
               sumAll+= z==0?0:sum/z;
               point.put(taskDum.getWeeks().label(), z==0?0:sum/z);
                z=0;
                i=task.getWeekOrder();
                sum=0;
            }
            z++;
            sum += task.getPoints() == null ? 0 : task.getPoints();
            taskDum = task;
        }
        if(taskDum!=null) {
            sumAll+= z==0?0:sum/z;
            point.put(taskDum.getWeeks().label(), z==0?0:sum/z);
        }
        double totalPoint = point.isEmpty()?0:sumAll/point.size();
        return new Point(map,point,totalPoint,internRepository.findById(id).get(),taskCompleted,tasks.size());
    }
    public Company getCompany(){
        return companyRepository.findById(getRecruiter().getId()).get();
    }

    public PostJobChartResponse postJobChartResponse (int postJobId){
        int totalCv =  sendOutCvRepository.totalCv(postJobId);
        int totalNoApproved =  sendOutCvRepository.totalCvNotApproved(postJobId,CVStatus.PENDING_APPROVAL);
        return new PostJobChartResponse(totalCv,totalNoApproved,totalCv-totalNoApproved);
    }

    public PostJobChartV2Response chartPostJob(int by){
        List<PostJob> posts = postJobRepository.getAllByRecruiterIdOrderByPostTime(getRecruiter().getId());
        if(posts.isEmpty()) {
            return new PostJobChartV2Response();
        }
        String s ="";
        if(by == 1)
            s = "Tháng ";
        int month = 34;
        int startMonth = posts.getFirst().getTimePost().getMonthValue();
        int endMonth = posts.getLast().getTimePost().getMonthValue();
        List<String> labels = new ArrayList<>();
        List<Integer> totalPost = new ArrayList<>();
        List<Integer> totalPostApproved = new ArrayList<>();
        List<Integer> totalPostNotApproved = new ArrayList<>();
        List<Integer> totalPostRejected=new ArrayList<>();
        List<Integer> totalPostHidden=new ArrayList<>();
        for(PostJob p : posts){
            int time = 1==by?p.getTimePost().getMonthValue():p.getTimePost().getDayOfMonth();
            if(month !=time) {
                month =time;
                labels.add(s + month +(by!=1?"-"+ time:""));
                totalPost.add(1);
                totalPostApproved.add(p.isApproved()?1:0);
                totalPostNotApproved.add(p.isApproved()?0:1);
                totalPostRejected.add(!p.isApproved()&&p.isHidden()?1:0);
                totalPostHidden.add(p.isHidden() && p.isApproved() ?1:0);
            }else{
                int index =totalPost.size()-1;
                int oldValue = totalPost.get(index);
                totalPost.set(index,oldValue+1);
                totalPostApproved.set(index,p.isApproved()?totalPostApproved.get(index)+1:totalPostApproved.get(index));
                totalPostNotApproved.set(index,p.isApproved()?totalPostNotApproved.get(index):totalPostNotApproved.get(index)+1);
                if(!p.isApproved()&&p.isHidden())
                    totalPostRejected.set(index,totalPostRejected.get(index)+1);
                if(p.isHidden() && p.isApproved() )
                    totalPostHidden.set(index,totalPostHidden.get(index)+1);
            }
        }
        return new PostJobChartV2Response(labels,totalPost,totalPostApproved,totalPostNotApproved,totalPostRejected,totalPostHidden);
    }

    public PostsCtx chartPostJob1(int by){
        List<PostJob> posts = postJobRepository.getAllByRecruiterIdOrderByPostTime(getRecruiter().getId());
        if(posts.isEmpty()) {
            return new PostsCtx();
        }
        String s ="";
        if(by == 1)
            s = "Tháng ";
        int index;
        int month = 45;
        int startMonth = posts.getFirst().getTimePost().getDayOfMonth();
        List<String> labels = new ArrayList<>();
        List<Integer> totalPost1 = new ArrayList<>();
        List<Integer> totalPost2 = new ArrayList<>();
        for(PostJob p : posts){
            int time = 1==by?p.getTimePost().getMonthValue():p.getTimePost().getDayOfMonth();
            if(month != time) {
                month =time;
                labels.add(s + month +(by!=1?"-"+ time:""));
                totalPost1.add((p.isApproved() && !p.isHidden() && p.getPostingDeadline().isAfter(LocalDate.now()))?1:0);
                totalPost2.add(p.getPostingDeadline().isBefore(LocalDate.now())?1:0);
            }else{
                index = totalPost1.size()-1;
                totalPost1.set(index,(p.isApproved() && !p.isHidden() && p.getPostingDeadline().isAfter(LocalDate.now()))?totalPost1.get(index)+1:totalPost1.get(index));
                totalPost2.set(index,p.getPostingDeadline().isBefore(LocalDate.now())?totalPost2.get(index)+1:totalPost2.get(index));
            }
        }
        return new PostsCtx(labels,totalPost1,totalPost2);
    }


    public MajorsCtx getAllByRecruiterIdOrderByMajor(){
        List<PostJob> posts= postJobRepository.getAllByRecruiterIdOrderByMajor(getRecruiter().getId());
        if(posts.isEmpty()) {
            return new MajorsCtx();
        }
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        int index = -1;
        String label = "";
        for(PostJob p : posts){
            if(!label.equals(p.getMajor().getLabel())) {
                label=p.getMajor().getLabel();
                labels.add(label);
                data.add(1);
                index++;
            }else{
                data.set(index,data.get(index)+1);
            }
        }
        return new MajorsCtx(labels,data);
    }
    public SummaryData getAllByRecruiterIdChart(){
        List<PostJob> posts= postJobRepository.getAllByRecruiterIdOrderByPostTimeDESC(getRecruiter().getId());
       if(posts.isEmpty()) {
            System.out.println("asdfsdfempty");
            return new SummaryData();
        }
        SummaryData s = new SummaryData();
        s.setTotalPosts(posts.size());
        int currMonth = posts.getFirst().getTimePost().getMonthValue();
        int sumCurrMonth=0;
        int sumPreMonth=0;
        int activePosts = 0;
        int sumCurrActivePosts = 0;
        int sumPreActivePosts = 0;
        int expiredPosts = 0;
        int sumCurrExpiredPosts = 0;
        int sumPreExpiredPosts = 0;
        int urgentPosts = 0;
        int sumCurrUrgentPosts = 0;
        int sumPreUrgentPosts = 0;
        for(int i = 0 ; i < posts.size(); i++) {
            if(posts.get(i).isUrgent())
                urgentPosts++;
            if( posts.get(i).getPostingDeadline().isBefore(LocalDate.now()))
                expiredPosts++;
            if(posts.get(i).isApproved() && !posts.get(i).isHidden() && posts.get(i).getPostingDeadline().isAfter(LocalDate.now()))
                ++activePosts;
            if(posts.get(i).getTimePost().getMonthValue() == currMonth){
                if(posts.get(i).isUrgent())
                    sumCurrUrgentPosts++;
                if( posts.get(i).getPostingDeadline().isBefore(LocalDate.now()))
                    sumCurrExpiredPosts++;
                if(posts.get(i).isApproved() && !posts.get(i).isHidden() && posts.get(i).getPostingDeadline().isAfter(LocalDate.now()))
                    ++sumCurrActivePosts;
                sumCurrMonth++;
            }else if (posts.get(i).getTimePost().getMonthValue() == currMonth -1){
                if(posts.get(i).isUrgent())
                    sumPreUrgentPosts++;
                if( posts.get(i).getPostingDeadline().isBefore(LocalDate.now()))
                    sumPreExpiredPosts++;
                if(posts.get(i).isApproved() && !posts.get(i).isHidden() && posts.get(i).getPostingDeadline().isAfter(LocalDate.now()))
                    ++sumPreActivePosts;
                sumPreMonth++;
            }else {
                break;
            }
        }
        s.setActivePosts(activePosts);
        s.setExpiredPosts(expiredPosts);
        s.setUrgentPosts(urgentPosts);
        if(sumPreUrgentPosts == 0)
            s.setActiveChange(sumCurrUrgentPosts+"+ so với tháng trước");
        else
            s.setActiveChange(((double) (sumCurrUrgentPosts - sumPreUrgentPosts) / sumPreUrgentPosts) * 100);
        if(sumPreExpiredPosts == 0)
            s.setActiveChange(sumCurrExpiredPosts+"+ so với tháng trước");
        else
            s.setActiveChange(((double) (sumCurrExpiredPosts - sumPreExpiredPosts) / sumPreExpiredPosts) * 100);

        if(sumPreActivePosts == 0)
            s.setActiveChange(sumCurrActivePosts+"+ so với tháng trước");
        else
            s.setActiveChange(((double) (sumCurrActivePosts - sumPreActivePosts) / sumPreActivePosts) * 100);

        if(sumPreMonth == 0)
            s.setTotalChange(sumCurrMonth+"+ so với tháng trước");
        else
            s.setTotalChange(((double) (sumCurrMonth - sumPreMonth) / sumPreMonth) * 100);
        s.setViewRes(viewCharts());
        return s;
    }

    public ViewRes viewCharts(){
        List<PostJob> posts= postJobRepository.getAllByRecruiterIdOrderByPostTimeDESCAndApproved(getRecruiter().getId());
        if(posts.isEmpty()) {
            new ArrayList<ViewRes>();
        }
        List<String> lb3 = new ArrayList<>();
        List<Integer> d3 = new ArrayList<>();
        int viewData=0;
        int uniqueViewData=0;

        for (PostJob p:posts){
            viewData += p.getView();
            uniqueViewData += p.getUsers().size();
            lb3.add(p.getName());
            d3.add(p.getView());
        }
        return new ViewRes(viewData,uniqueViewData,viewData-uniqueViewData,lb3,d3);
    }

}
