package com.jobportal.repostory;

import com.jobportal.entity.Task;
import com.jobportal.model.InternshipStatus;
import com.jobportal.model.SecWeeks;
import com.jobportal.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Integer> {

//    @Query("SELECT t FROM Task t WHERE t.recruiter.id = :id AND t.intern.status = :internShipStatus AND " +
//            "(LOWER(t.intern.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
//            "t.completed NOT IN :completed")
//    Page<Task> getTask(@Param("id") Integer companyId,
//                       @Param("internShipStatus") InternshipStatus status,
//                       @Param("keyword") String keyword,
//                       @Param("completed") List<Boolean> completed,
//                       Pageable pageable);
    @Query("SELECT t FROM Task t WHERE t.recruiter.id = :id AND t.intern.status = :internShipStatus AND " +
            "(LOWER(t.intern.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "t.taskStatus IN :taskStatus AND t.weeks IN :week")
    Page<Task> getTaskWithWeed(@Param("id") Integer companyId,
                       @Param("internShipStatus") InternshipStatus status,
                       @Param("keyword") String keyword,
                       @Param("taskStatus") List<TaskStatus> taskStatus,
                       @Param("week") List<SecWeeks> week,
                       Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.recruiter.id = :id AND t.intern.status = :internShipStatus")
    List<Task> getAllByInternshipStatus(@Param("id") Integer companyId,@Param("internShipStatus") InternshipStatus internshipStatus);
    @Query("SELECT t FROM Task t WHERE t.intern.id = :id  AND " +
            "t.taskStatus IN :taskStatus AND t.weeks IN :week")
    Page<Task> getTaskWithWeedByIntern(@Param("id") Integer internId,
                                       @Param("taskStatus") List<TaskStatus> taskStatus,
                                       @Param("week") List<SecWeeks> week,
                                       Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.intern.id = :id ORDER BY t.weekOrder ASC ")
    List<Task> getTaskByInternshipId(@Param("id") Integer internshipId);

    @Query("SELECT t FROM Task t WHERE t.intern.id = :id  " +
            " ORDER BY t.weekOrder ASC ")
    List<Task> getTaskByInternId(@Param("id") Integer internId );

    @Query("SELECT t FROM Task t WHERE t.intern.id = :id AND t.weekOrder =:week")
    List<Task> getTaskByInternIdAndWeek(@Param("id") Integer internId ,
                                        @Param("week") Integer week );
}
