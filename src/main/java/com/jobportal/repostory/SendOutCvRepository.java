package com.jobportal.repostory;

import com.jobportal.entity.Intern;
import com.jobportal.entity.SendOutCv;
import com.jobportal.entity.User;
import com.jobportal.model.CVStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SendOutCvRepository extends JpaRepository<SendOutCv,Integer> {

    @Query("SELECT COUNT (s) FROM SendOutCv s WHERE s.intern.id = ?1 ")
    Integer getAllCVByInternId(int internId);


    @Query("SELECT s FROM SendOutCv s WHERE s.intern.id = :id " )
    Page<SendOutCv> findTopByInternId(@Param("id") Integer internId, Pageable pageable);

    @Query("SELECT COUNT (s) FROM SendOutCv s WHERE s.intern.id = ?1 AND s.postJob.id = ?2")
    Integer countByInternIdAndJobPostId(Integer internId, Integer jobPostId);

    @Query("SELECT s FROM SendOutCv s WHERE s.intern.id = :id ORDER BY s.id DESC " )
    List<SendOutCv> findTopByInternId(@Param("id") Integer internId);

    @Query("SELECT s FROM SendOutCv s WHERE s.intern.id = :id AND s.seen = :status ORDER BY s.id DESC " )
    List<SendOutCv> findByInternIdAndSeen(@Param("id") Integer internId,@Param("status") boolean status);


    @Query("SELECT s FROM SendOutCv s WHERE s.postJob.recruiter.id = ?1 AND s.cvStatus = :cvStatus ")
    List<SendOutCv> getThreeInternLasted(Integer recruiterId,@Param("cvStatus") CVStatus cvStatus,
                                         Pageable pageable);

    @Query("SELECT s FROM SendOutCv s WHERE s.postJob.recruiter.id = :id AND " +
            "CAST(s.postJob.id as string) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
            "(CONCAT('cv', s.id) LIKE LOWER(CONCAT('%', :nameJob, '%'))OR " +
            "LOWER(s.intern.email) LIKE LOWER(CONCAT('%', :nameJob, '%')) ) AND " +
            "s.seen IN :seenList AND s.postJob.urgent IN :urgentList AND s.cvStatus = :cvStatus")
    Page<SendOutCv> getByRecruiterId(@Param("id") Integer recruiterId,
                                     @Param("keyword") String keyword,
                                     @Param("nameJob") String nameJob,
                                     @Param("seenList") List<Boolean> seen,
                                     @Param("urgentList") List<Boolean> urgent,
                                     @Param("cvStatus") CVStatus cvStatus,
                                     Pageable pageable);
    @Query("SELECT s FROM SendOutCv s WHERE s.postJob.recruiter.id = :id AND " +
            "CAST(s.postJob.id as string) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
            "(CONCAT('cv', s.id) LIKE LOWER(CONCAT('%', :nameJob, '%'))OR " +
            "LOWER(s.intern.email) LIKE LOWER(CONCAT('%', :nameJob, '%')) ) AND " +
            " s.postJob.urgent IN :urgentList AND s.cvStatus NOT IN :cvStatus")
    Page<SendOutCv> getByRecruiterIdTab3(@Param("id") Integer recruiterId,
                                     @Param("keyword") String keyword,
                                     @Param("nameJob") String nameJob,
                                     @Param("urgentList") List<Boolean> urgent,
                                     @Param("cvStatus") List<CVStatus> cvStatus,
                                     Pageable pageable);


    @Query("SELECT COUNT (*) FROM SendOutCv s WHERE s.postJob.id = ?1 AND s.cvStatus = ?2")
    Integer countApplicantsByPostJobId(Integer postJobId, CVStatus status);

    @Query("SELECT s FROM SendOutCv s WHERE s.id = ?1")
    SendOutCv getCvBySotId(Integer id);

    @Query("SELECT COUNT (s) FROM SendOutCv s WHERE s.intern.id = ?1 AND s.postJob.recruiter.id = ?2 AND s.cvStatus = ?3")
    Integer getNumberOfCvsBySotInternIdAndRecruiterId(Integer internId,Integer recruiterId, CVStatus status);

    @Modifying
    @Query("UPDATE SendOutCv s SET s.seen = TRUE  WHERE s.id = ?1")
    void seenCv(Integer cvId);

    @Modifying
    @Query("UPDATE SendOutCv s SET s.cvStatus = ?2  WHERE s.id = ?1")
    void rejectCv(Integer cvId, CVStatus cvStatus);

    @Modifying
    @Query("UPDATE SendOutCv s SET s.cvStatus = ?2  WHERE s.id = ?1")
    void beingInterviewedCv(Integer cvId, CVStatus cvStatus);

    @Query("SELECT s FROM SendOutCv s WHERE s.intern.id = ?1 AND s.cvStatus = ?2 ORDER BY s.id DESC ")
    List<SendOutCv> recruitedByInternId(Integer internId, CVStatus status );

    @Query("SELECT COUNT (s) FROM SendOutCv s WHERE s.postJob.id = ?1 ")
    Integer totalCv(Integer postJobId);

    @Query("SELECT COUNT (s) FROM SendOutCv s WHERE s.postJob.id = ?1 AND s.cvStatus = ?2")
    Integer totalCvNotApproved(Integer postJobId, CVStatus status);

    @Query("SELECT s FROM SendOutCv s WHERE s.intern.id = ?1 AND s.cvStatus = ?2")
    List<SendOutCv> a(int id, CVStatus status);

    @Query("SELECT DISTINCT s.intern FROM SendOutCv s WHERE s.postJob.id = ?1")
    List<Intern> findUniqueInternsSentCv(Integer idJob);
}
