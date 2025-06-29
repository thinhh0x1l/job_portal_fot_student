package com.jobportal.service;

import com.jobportal.dto.request.CommentDto;
import com.jobportal.dto.request.CommentUpdate;
import com.jobportal.dto.request.CompaniesReq;
import com.jobportal.dto.response.CommentRes;
import com.jobportal.dto.response.CompaniesRes;
import com.jobportal.dto.response.JobInCompanyRes;
import com.jobportal.dto.response.NotificationDTO;
import com.jobportal.entity.*;
import com.jobportal.model.District;
import com.jobportal.model.MailInfo;
import com.jobportal.model.Role;
import com.jobportal.repostory.*;
import com.jobportal.security.CustomDefaultOidcUser;
import com.jobportal.security.CustomUserDetails;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.bytebuddy.utility.RandomString;
import org.jasypt.util.text.BasicTextEncryptor;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.jobportal.utils.UrlSafeEncryptorUtil.decrypt;
import static com.jobportal.utils.UrlSafeEncryptorUtil.encrypt;

@Service
@Transactional
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserService {
    MailerService mailer;
    UserRepository userRepository;
    OnlineService onlineService;
    NotificationRepository notificationRepository;
    SendOutCvRepository sendOutCvRepository;
    TaskRepository taskRepository;
    BasicTextEncryptor stringEncryptor;
    PostJobRepository postJobRepository;
    CompanyRepository companyRepository;
    CommentRepository commentRepository;
    PasswordEncoder passwordEncoder;
    SimpMessagingTemplate messagingTemplate;
    NotificationService notificationService;
    AiSystemRepository aiSystemRepository;
    PrettyTime p = new PrettyTime(new Locale("vi"));
    private final TagRepository tagRepository;

    public String url(String code){
        String decryptToken = decrypt(code);
        String getEmail = decryptToken.split("\\[]")[2];
        User user = userRepository.findByEmail(getEmail).get();
        return switch (user.getRole()){
            case Role.RECRUITER -> "/recruiter/login";
            case Role.INTERN -> "/login";
            default -> "/lecture/login";
        };
    }
    public User getUser(Principal principal) {
        String username = "";

        if (principal instanceof Authentication authentication) {
            Object user = authentication.getPrincipal();

            if (user instanceof CustomUserDetails userDetails) {
                username = userDetails.getUsername();

            } else if (user instanceof CustomDefaultOidcUser oidcUser) {
                username = oidcUser.getUser().getEmail();
            }
        }

        System.out.println("Username: " + username);

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy intern"));
    }
    public void uniqueViewData(int userId, int postJobid){
        if(!postJobRepository.existsUserInJobPost(userId,postJobid)){
            User user = userRepository.findById(userId).orElseThrow();
            PostJob jobPost = postJobRepository.findById(postJobid).orElseThrow();

            // Liên kết 2 thực thể
            jobPost.getUsers().add(user);

            // Nếu mappedBy ở bên kia, cần cập nhật cả hai bên
            user.getPostJobs().add(jobPost);

            // Lưu lại
            postJobRepository.save(jobPost);
            userRepository.save(user); //
        }

    }

    public int sendVerifyEmail(User user, String url, Map<String, byte[]> files) throws MessagingException, IOException {
        String encryptToken =
                (RandomString.make(64) + "[]" + LocalDateTime.now().plusMinutes(1) + "[]" + user.getEmail());
        user.setVerifyToken(encryptToken);

        userRepository.save(user);
        String dear = switch (user.getRole()){
            case Role.INTERN -> "sinh viên";
            case Role.RECRUITER -> "nhà tuyển dụng";
            default -> "giảng viên";
        };
        String content = "Chào "+dear+" [[name]], <br>"
                + "Làm ơn hãy click vào đường dẫn bên dưới để xác nhận đăng kí:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY EMAIL</a></h3>"
                + "Cảm ơn, <br>";
        content = content.replace("[[name]]", user.getEmail());
        String verifyURL = url + "/kma/verify?code=" + encrypt(user.getVerifyToken());
        content = content.replace("[[URL]]", verifyURL);
        mailer.send(MailInfo.builder()
        ///iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
        ///iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
        ///iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
///++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                .to(user.getEmail())
///++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        ///iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
        ///iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
        ///iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii
                .subject("KMA tuyển dụng - Verify your email")
                .body(content)
                .files(files)
                .build());
        return user.getId();
    }

    public void reSendVerifyEmail(String url, String code) throws MessagingException, IOException {
        String decryptToken = decrypt(code);
        String getEmail = decryptToken.split("\\[]")[2];
        User user = userRepository.findByEmail(getEmail).get();
        sendVerifyEmail(user, url, new HashMap<String, byte[]>());
    }

    public String verify(String verifyCode) {
        String decryptToken = decrypt(verifyCode);
        Optional<User> u = userRepository.findByVerifyToken(decryptToken);
        if (u.isEmpty() || u.get().isEnabled())
            return "Fail";
        String time = decryptToken.split("\\[]")[1];
        if (LocalDateTime.parse(time).isBefore(LocalDateTime.now()))
            return "Expire";
        u.get().setEnabled(true);
        u.get().setVerifyToken("0");
        userRepository.save(u.get());
        return "Success";

    }
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
    public void changePassword(String password){
        User user = userRepository.findById(getUserId()).get();
        user.setPassword(passwordEncoder.encode(password));
    }

    private Integer getUserId(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof CustomUserDetails customUserDetails)
            return customUserDetails.getId();
        if(principal instanceof CustomDefaultOidcUser customDefaultOidcUser)
            return customDefaultOidcUser.getUser().getId();
        return null;
    }


    public List<Notification> findByReceiverId(){
        return notificationRepository.findByReceiverId(getUserId(),LocalDateTime.now().minusWeeks(2));
    }

    public void updateSeen(){
        notificationRepository.updateSeen(getUserId(),LocalDateTime.now().minusWeeks(2));
    }
    public void updateSeenByOne(int id){
        notificationRepository.updateSeenByOne(getUserId(),LocalDateTime.now().minusWeeks(2),id);
    }

    public Integer countNotSeen(){
        return notificationRepository.countNotSeen(getUserId(),LocalDateTime.now().minusWeeks(2));
    }

    public SendOutCv getCvBySotId(Integer sotId){
        return sendOutCvRepository.getCvBySotId(sotId);
    }

    public Notification findNotificationById(Integer Id){
        return notificationRepository.findById(Id).get();
    }

    public Task getTaskById(Integer taskId){
        return taskRepository.findById(taskId).get();
    }
    public Comment createComment(CommentDto commentDto){
        Comment c = new Comment();
        User u = userRepository.findById(getUserId()).get();
        Company company = companyRepository.findById(commentDto.getCompanyId()).get();
        User re = userRepository.findById(company.getRecruiter().getId()).get();
        c.setStar(commentDto.getStar());
        c.setContent(commentDto.getContent());
        c.setTitle(commentDto.getTitle());
        c.setUser(u);
        c.setCompany(company);
        notificationService.notification(
                u.getFirstName()+ " " + u.getLastName() +" vừa đánh giá công ty của bạn",
                "/compy/"+company.getId()+"?tab=reviews",
                "user-photos/",
                u,
                re
        );
        return commentRepository.save(c);
    }

    public void updateComment(CommentUpdate commentUpdate){
        Comment comment = commentRepository.findById(commentUpdate.getCommentId()).get();
        comment.setStar(commentUpdate.getEditRating());
        comment.setContent(commentUpdate.getContent());
        comment.setTitle(commentUpdate.getTitle());
    }
    public CommentRes getComments(Integer companyId,int star, int page){
        int sizePage = 5;
        Page<Comment> pageCom;
        if(star==0)
            pageCom= commentRepository.getCommentsByCompanyId(companyId,
                PageRequest.of(page-1,sizePage, Sort.by("id").descending()));
        else
            pageCom= commentRepository.getCommentsByCompanyIdAndStar(companyId,star,
                    PageRequest.of(page-1,sizePage, Sort.by("id").descending()));

        List<Comment> list = commentRepository.getCommentsByCompanyId(companyId) ;
        CommentRes res = new CommentRes();
        if(list.isEmpty())
            return res;
        double avgStar =0;
        List<Integer> starAnalysis = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            starAnalysis.add(0);
        CommentRes r = new CommentRes();
        res.setNumberOfPage(pageCom.getTotalPages());
        res.setCurrentPage(page);
        pageCom.getContent().forEach(c -> {
            CommentRes.CommentResV2 cr = r.new CommentResV2();
            cr.setContent(c.getContent());
            cr.setTitle(c.getTitle());
            cr.setStar(c.getStar());
            cr.setLike(c.getLikeF());
            String role = switch (c.getUser().getRole()){
                case RECRUITER -> "Nhà tuyển dụng";
                case INTERN -> "Sinh viên";
                default -> "Giảng viên";
            };
            cr.setCommentId(c.getId());
            cr.setLiked(commentRepository.hasUserLikedComment(c.getId(),getUserId()));
            cr.setLike(commentRepository.countUsersLikedComment(c.getId()));
            cr.setRole(role);
            cr.setCreated(c.getTimeCreated());
            cr.setItsMe(Objects.equals(c.getUser().getId(), getUserId()));
            cr.setImage("/images/user-photos/"+c.getUser().getId()+"/"+c.getUser().getImageUrl());
            cr.setFullname(c.getUser().getFirstName() + " " + c.getUser().getLastName()
                    + (Objects.equals(c.getUser().getId(), getUserId()) ?" (Bạn)":""));
            res.getComments().add(cr);
        });
        for(Comment c : list){
            avgStar+= c.getStar();
            int idx=c.getStar()-1;
            starAnalysis.set(idx,starAnalysis.get(idx)+1 );
        }
        res.setTotalComments(list.size());
        res.setAvgStar(avgStar/list.size());
        res.setStarAnalysis(starAnalysis);
        return res;
    }

    public List<Tag> getAllTags(){
        return tagRepository.findAll();
    }

    public void checkLike(int commentId){
        boolean hasLike = commentRepository.hasUserLikedComment(commentId,getUserId());
        User user = userRepository.findById(getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if(hasLike){

            user.getLikedComments().remove(comment);
            comment.getLikedByUser().remove(user);
//            // 2. Lưu lại (ít nhất 1 bên)
//            userRepository.save(user); /
        }
        else{
            user.getLikedComments().add(comment);
            comment.getLikedByUser().add(user);
        }

    }

    public void notification(String content,
                                            String url,String image, User sender, User receiver){
        notificationService.notification(content,url,image,sender,receiver);
    }
    public User getUser(){
        return userRepository.findById(getUserId()).get();
    }


    public AiSystem getAisystem(){
        return aiSystemRepository.findById(1).get();
    }

    public static final int JOB_IN_COMPANY = 4;

    public JobInCompanyRes getJobsInCompany(Integer companyId, int page){
        User user = getUserId() != null
                ? userRepository.findById(getUserId()).orElse(null)
                : null;

        Page<PostJob> postJobs = postJobRepository.
                findByCompanyIdInCompany(companyId, LocalDate.now(),
                        PageRequest.of(page - 1 ,  JOB_IN_COMPANY,Sort.by("id").descending()));

        JobInCompanyRes resIn = new JobInCompanyRes();
        if(postJobs.isEmpty())return resIn;

        long startCount =  (page - 1L) * JOB_IN_COMPANY + 1;
        long endCount = startCount + JOB_IN_COMPANY -1;
        if(endCount > postJobs.getTotalElements());
        endCount = postJobs.getTotalElements()  ;
        resIn.setCurrentPage(page);
        resIn.setTotalPages(postJobs.getTotalPages());
        resIn.setElementEnd(endCount);
        resIn.setElementStart(startCount);
        resIn.setTotalElements(postJobs.getTotalElements());
        List<JobInCompanyRes.Job> listRes = new ArrayList<>();
        for(PostJob p : postJobs.getContent()){
            JobInCompanyRes.Job res = resIn.new Job();
            if(user!= null && user.getRole().equals(Role.INTERN))
                res.setFavorite(p.getInterns().contains((Intern) user));
            res.setId(p.getId());
            res.setName(p.getName());
            res.setCreatedAt(p.getTimePost());
            res.setViews(p.getView());
            res.setSalary(p.getSalary() >= 1 ? p.getSalary()+" triệu" :
                    (p.getSalary()!=0 ?p.getSalary()*1000000 +" nghìn":"Không lương"));
            for (Tag t : p.getTags())
                res.getTags().add(
                        res.new Tags(
                                t.getId(),
                                t.getName(),
                                t.getColorClassV1()
                        )
                );
            listRes.add(res);
        }
        resIn.setJobs(listRes);
        return resIn;
    }

    public static final int PAGE_COMPANY_SIZE = 9;

    public CompaniesRes companiesRes(CompaniesReq companiesReq){

        Page<Company> companies;
        if(companiesReq.getDistrictCode().equals("0"))
            companies = companyRepository.getCompanies(companiesReq.getKeywords(),
                    PageRequest.of(companiesReq.getPage()-1, PAGE_COMPANY_SIZE));
        else
            companies = companyRepository.getCompaniesByDistrict(companiesReq.getKeywords(),
                    District.valueOf(companiesReq.getDistrictCode()),
                    PageRequest.of(companiesReq.getPage()-1, PAGE_COMPANY_SIZE));

        long startCount =  (companiesReq.getPage() - 1L) * PAGE_COMPANY_SIZE + 1;
        long endCount = startCount + PAGE_COMPANY_SIZE -1;
        if(endCount > companies.getTotalElements());
        endCount = companies.getTotalElements()  ;


        CompaniesRes res = new CompaniesRes(companiesReq.getDistrictCode());
        res.setCurrentPage(companiesReq.getPage());
        res.setTotalElements(companies.getTotalElements());
        res.setTotalPages(companies.getTotalPages());
        res.setElementEnd(endCount);
        res.setElementStart(startCount);

        res.setKeywords(companiesReq.getKeywords());
        res.setFilter(companiesReq.getFilter());
        for (District d : District.values()){
            res.getDistricts().add(res.new District(d.getDisplayName()
                    ,d.name(),companiesReq.getDistrictCode().equals(d.name())));
        }
        if(companies.isEmpty())return res;
        List<Object[]> result;
        if(companiesReq.getFilter().equals("1")||companiesReq.getFilter().equals("2"))
            if(companiesReq.getFilter().equals("1"))
                result = companyRepository.getCompaniesOrderByAverageStarASC(companies.getContent(),
                        LocalDate.now());
            else
                result = companyRepository.getCompaniesOrderByAverageStarDesc(companies.getContent(),
                        LocalDate.now());

         else if (!companiesReq.getFilter().equals("0"))
            if(companiesReq.getFilter().equals("3"))
                 result = companyRepository.getCompaniesWithJobCountAndAverageStarASC(companies.getContent(),
                         LocalDate.now());
            else
                result = companyRepository.getCompaniesWithJobCountAndAverageStarDESC(companies.getContent(),
                        LocalDate.now());

        else{
            result = companyRepository.getCompaniesWithoutOrdering(companies.getContent(),
                    LocalDate.now());
        }
        for (Object[] row : result) {
            Company company = (Company) row[0];
            Integer jobCount = ((Long) row[1]).intValue();

            Double avgStar = (Double) row[2];
            CompaniesRes.Company compy = res.new Company();

            compy.setId(company.getId());
            compy.setCompanyName(company.getCompanyName());
            compy.setImage("/images/companies/"+company.getId()+"/"+company.getImageUrl());
            compy.setDistrict(company.getDistrict().getDisplayName()+", TpHCM");
            String plainText = company.getDescription().replaceAll("<[^>]*>", "");
            compy.setDescription(plainText.substring(0, Math.min(80,plainText.length())));

            compy.setJobs(jobCount);
            compy.setAvgStars(avgStar);
            res.getCompanies().add(compy);

        }


        return res;
    }
}

