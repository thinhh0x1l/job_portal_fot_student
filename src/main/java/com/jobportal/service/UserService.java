package com.jobportal.service;

import com.jobportal.dto.request.CommentDto;
import com.jobportal.dto.request.CommentUpdate;
import com.jobportal.dto.response.CommentRes;
import com.jobportal.dto.response.NotificationDTO;
import com.jobportal.entity.*;
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
                .to("minjulin18200@gmail.com")
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
}

