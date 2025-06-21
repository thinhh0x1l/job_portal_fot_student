package com.jobportal.service.lecturer;

import com.jobportal.dto.dummy.DetailStudent;
import com.jobportal.dto.dummy.FollowIntern;
import com.jobportal.dto.dummy.WeekTask;
import com.jobportal.entity.*;
import com.jobportal.model.AuthProvider;
import com.jobportal.model.CVStatus;
import com.jobportal.model.Role;
import com.jobportal.repostory.*;
import com.jobportal.security.CustomDefaultOidcUser;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.MailerService;
import com.jobportal.service.OnlineService;
import com.jobportal.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class LecturerService {
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
    LecturerRepository lecturerRepository;
    PasswordEncoder passwordEncoder;

    public List<Lecturer> getAllLecturers() {
        return lecturerRepository.findAll();
    }

    public Lecturer register(Lecturer lecturer) {
        lecturer.setProvider(AuthProvider.DATABASE);
        lecturer.setRole(Role.RECRUITER);
        lecturer.setPassword(passwordEncoder.encode(lecturer.getPassword()));
        return lecturerRepository.save(lecturer);
    }

    public Lecturer getLecturer() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;
        return lecturerRepository.findById(customUserDetails.getId()).get();

    }
    public List<Intern> getAllInterns() {
        return internRepository.getAllInternsByLecturerId(getLecturer().getId());
    }

    public List<FollowIntern> followIntern(Integer id){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        List<Task> tasks = taskRepository.getTaskByInternId(id);
        List<FollowIntern> followInterns = new ArrayList<>();
        if(tasks.isEmpty())
            return followInterns;
        int w = tasks.getFirst().getWeekOrder();
        int all = 0;
        int s =0;
        int count =0;
        Task tDum= tasks.get(0);
        FollowIntern followIntern = new FollowIntern();
        for (Task t : tasks) {
            if(w!=t.getWeekOrder()){
                followIntern = new FollowIntern();
                followIntern.setWeek(tDum.getWeeks().label());

                followIntern.setWeekOrder(tDum.getWeekOrder());
                followIntern.setCount(count++);

                w = t.getWeekOrder();
                followIntern.setTryi("Hoàn thành "+String.format("%.2f",((double)s*100/all))+"% tiến độ ("+s+"/"+all+")");
                all=1;
                s=t.getPoints()!=null?1:0;
                followInterns.add(followIntern);
            }else{
                all++;
                s+= t.getPoints()!=null?1:0;
            }
            tDum =t ;
        }
        if(w==tDum.getWeekOrder()){
            followIntern = new FollowIntern();
            followIntern.setWeek(tDum.getWeeks().label());

            followIntern.setWeekOrder(tDum.getWeekOrder());
            followIntern.setCount(count++);
            all=1;
            s= tDum.getPoints()!=null?1:0;
            followIntern.setTryi("Hoàn thành "+String.format("%.2f",((double)s*100/all))+"% tiến độ ("+s+"/"+all+")");
            followInterns.add(followIntern);
        }else{
            all++;
            s+= tDum.getPoints()!=null?1:0;
            followIntern.setTryi("Hoàn thành "+String.format("%.2f",((double)s*100/all))+"% tiến độ ("+s+"/"+all+")");
            followInterns.add(followIntern);
        }
        return followInterns;
    }

    public WeekTask getWeekTask(Integer internId,Integer week) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        List<Task> tasks = taskRepository.getTaskByInternIdAndWeek(internId,week);
        WeekTask weekTask = new WeekTask();
        List<String> titles = new ArrayList<>();
        List<Integer> statuss = new ArrayList<>();
        List<Double> dc1 = new ArrayList<>();
        for (Task t : tasks) {
            titles.add(t.getName());
            WeekTask.Task task = weekTask.new Task();
            task.setTaskId(t.getId());
            int status=2;
            if (t.getReport() == null) {
                // 1. Chưa hoàn thành - chưa nộp báo cáo
                status = 1;
            } else if (t.getPoints() == null) {
                // 4. Đã nộp nhưng chưa chấm điểm
                status = 4;
            } else if (t.getSubmitTime().isAfter(t.getEndTime())) {
                // 3. Hoàn thành trễ
                status = 3;
            }
            dc1.add(t.getPoints()==null?0.0:t.getPoints());

            statuss.add(status);
            task.setDescription(t.getDescription());
            if(t.getReport()!=null)
                task.setReport("/images/reports/"+t.getId()+"/"+t.getReport());

            task.setReviewCompany(t.getReview());
            task.setPointCompany(t.getPoints()==null?"------":t.getPoints().toString()+"/10");
            task.setTime(t.getWeeks().label()+" ("+t.getTimePost().format(formatter)+
                    " - "+t.getEndTime().format(formatter)+")");
            task.setStatus(status);
            task.setTitle(t.getName());
            weekTask.getTasks().add(task);
        }
        weekTask.setStatus(statuss);
        weekTask.setTitles(titles);
        weekTask.setDc1(dc1);
        return weekTask;
    }

    public DetailStudent detailStudent(Integer id) {
        Intern i = internRepository.findById(id).get();
        DetailStudent d = new DetailStudent();
        d.setStudentName(i.getFirstName() + " " + i.getLastName());
        d.setStatusIntern(i.getStatus().getLabel());
        d.setImage("/images/user-photos/"+i.getId()+"/"+i.getImageUrl());
        d.setMssv(i.getMSSV() + " - " +i.getLop());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        d.setEmail(i.getEmail());
        d.setGvHd("TH.S/T.S "+i.getLecturer().getFirstName()+" "+i.getLecturer().getLastName());
        if(i.getCompany()!=null){
            Company company = companyRepository.findById(i.getCompany().getId()).get();
            SendOutCv sv = sendOutCvRepository. a(i.getId(), CVStatus.HIRED).getFirst();
            d.setNtd(i.getCompany().getRecruiter().getFirstName()+" "+i.getCompany().getRecruiter().getLastName() +" (Nhà tuyển dụng nhiệt huyết)");
            d.setVitriTT(sv.getPostJob().getMajor().getLabe2l()+" Intern");
            d.setCompanyName(company.getCompanyName());
            d.setTimeTT(i.getDateToInternShip().format(formatter) +" - " + i.getDateToInternShip().plusWeeks(10).format(formatter) +" (10 tuần)");
            d.setLinkCompany("/compy/"+company.getId());
            d.setEmailCompany(company.getEmail());
        }
        return d;
    }

}
