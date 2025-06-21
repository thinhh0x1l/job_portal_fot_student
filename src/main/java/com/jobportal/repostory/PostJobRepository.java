package com.jobportal.repostory;

import com.jobportal.entity.Intern;
import com.jobportal.entity.PostJob;
import com.jobportal.model.CVStatus;
import com.jobportal.model.District;
import com.jobportal.model.Major;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostJobRepository extends JpaRepository<PostJob, Integer> {
    @Query("SELECT COUNT(j) > 0 FROM PostJob j WHERE j.recruiter.id = ?1 AND j.id = ?2 " +
            " AND j.recruiter.company IS NOT NULL " )
    boolean existsByRecruiterId(int rid,int id);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?3 AND j.name LIKE %?1% " +
            " AND j.recruiter.company IS NOT NULL AND " +
            "j.postingDeadline <= ?2" )

    Page<PostJob> findByLikeAndAfter(String keyword, LocalDate before, Integer id,Pageable pageable);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?3 AND j.name LIKE %?1% " +
            " AND  j.postingDeadline > ?4 " +
            "AND j.recruiter.company IS NOT NULL AND j.hidden = false  AND  j.approved = ?2")

    Page<PostJob> findByLikeAndApproved(String keyword, boolean approved, Integer id, LocalDate before, Pageable pageable);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?2 AND j.name LIKE %?1% " +
            " AND j.recruiter.company IS NOT NULL ")
    Page<PostJob> findByLike(String keyword, Integer id, LocalDate before,Pageable pageable);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?2 AND j.name LIKE %?1% " +
            " AND j.recruiter.company IS NOT NULL AND j.postingDeadline > ?3 AND j.hidden = true  " +
            "and  j.approved = true")
    Page<PostJob> findByHidden(String keyword, Integer id, LocalDate before,Pageable pageable);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?2 AND j.name LIKE %?1% " +
            " AND j.recruiter.company IS NOT NULL AND j.hidden = true AND j.approved = false AND  j.postingDeadline > ?3 ")
    Page<PostJob> findByLikeReject(String keyword, Integer id,LocalDate before ,Pageable pageable);


    @Query("SELECT COUNT (j) FROM PostJob  j WHERE j.recruiter.id = ?1 AND j.timePost >= ?2 " +
            "AND  j.postingDeadline > ?3 " +
            "AND j.hidden = FALSE AND j.approved = TRUE ")
    Integer newPostJobInLastedWeek(Integer id, LocalDateTime time, LocalDate before);

    @Query("SELECT COUNT (j) FROM PostJob  j WHERE j.recruiter.id = ?1 " +
            "AND  j.postingDeadline > ?2 " +
            "AND j.hidden = FALSE AND j.approved = TRUE ")
    Integer postJobApprove(Integer id, LocalDate before);

    /// /// nha tuyen dung
    /// /// sinh vien

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.company.district = ?1 AND j.postingDeadline > ?2 AND " +
            "j.urgent IN :urgentList AND (LOWER(j.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.recruiter.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            " AND j.recruiter.company IS NOT NULL  AND j.approved = TRUE ")
    Page<PostJob> findByDistrict(District district,LocalDate date, @Param("jobIds") Set<Integer> jobIds, Pageable pageable,
                                 @Param("keyword") String keyword ,@Param("urgentList") List<Boolean> urgent);

    @Query("SELECT j FROM PostJob j WHERE j.salary BETWEEN :min AND :max AND j.postingDeadline > :date AND " +
            "j.urgent IN :urgentList AND (LOWER(j.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.recruiter.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            " AND j.recruiter.company IS NOT NULL  AND j.approved = TRUE ")
    Page<PostJob> findBySalary(@Param("min")double min, @Param("max") double max,@Param("date") LocalDate date,
                               @Param("jobIds") Set<Integer> jobIds, Pageable pageable,
                                 @Param("keyword") String keyword ,@Param("urgentList") List<Boolean> urgent);

    @Query("SELECT j FROM PostJob j WHERE j.major = ?1 AND j.postingDeadline > ?2 AND "+
            "j.urgent IN :urgentList AND (LOWER(j.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(j.recruiter.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            " AND j.recruiter.company IS NOT NULL   AND j.approved = TRUE")
    Page<PostJob> findByMajor(Major major, LocalDate date, @Param("jobIds") Set<Integer> jobIds,Pageable pageable,
                              @Param("keyword") String keyword,@Param("urgentList") List<Boolean> urgent);

    @Query("SELECT j FROM PostJob j WHERE (LOWER(j.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "  OR LOWER(j.recruiter.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "  AND j.postingDeadline > ?1  AND j.urgent IN :urgentList AND j.approved = TRUE " +
            " AND j.recruiter.company IS NOT NULL  ")
    Page<PostJob> searchByJobOrCompany(LocalDate date,@Param("jobIds") Set<Integer> jobIds, Pageable pageable,
                                       @Param("keyword") String keyword,@Param("urgentList") List<Boolean> urgent);

    @Query(value = """
                SELECT DISTINCT pj.*
                FROM send_out_cvs s1
                JOIN send_out_cvs s2 ON s1.post_jobs_id = s2.post_jobs_id
                JOIN send_out_cvs s3 ON s3.intern_id = s2.intern_id
                JOIN post_jobs pj ON pj.id = s3.post_jobs_id
                WHERE s1.intern_id = :id
                    AND s2.intern_id != :id
                    AND s3.post_jobs_id NOT IN (
                    SELECT post_jobs_id FROM send_out_cvs WHERE intern_id = :id
                    )

                    AND pj.posting_deadline > :today
                    AND pj.approved = TRUE
                ORDER BY MD5(CONCAT(pj.id, :seed))
                LIMIT :limit
                        """, nativeQuery = true)
    Set<PostJob> relatedJobs(@Param("id") Integer internId, @Param("seed") String seed,
                             @Param("limit")int limit,@Param("keyword") String keyword,
                             @Param("today") LocalDate today);
//                    AND LOWER(pj.name) LIKE LOWER(CONCAT('%', :keyword, '%'))

    @Modifying
    @Query("UPDATE PostJob j SET j.view = j.view + 1 WHERE j.id = ?1")
    void increaseView(Integer id);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?1")
    List<PostJob> getAllByRecruiterId(Integer id);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?1 ORDER BY j.timePost ASC ")
    List<PostJob> getAllByRecruiterIdOrderByPostTime(Integer id);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?1 ORDER BY j.major ASC ")
    List<PostJob> getAllByRecruiterIdOrderByMajor(Integer id);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?1 ORDER BY j.timePost DESC ")
    List<PostJob> getAllByRecruiterIdOrderByPostTimeDESC(Integer id);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id = ?1 AND NOT (j.approved = FALSE AND j.hidden = TRUE )")
    List<PostJob> getAllByRecruiterIdOrderByPostTimeDESCAndApproved(Integer id);

    @Query("SELECT COUNT(j) > 0 FROM PostJob j JOIN j.users u WHERE j.id = :jobPostId AND u.id = :userId")
    boolean existsUserInJobPost(@Param("userId") int userId, @Param("jobPostId") int jobPostId);

    @Query("SELECT j FROM PostJob j WHERE j.id = ?1 AND NOT (j.approved = FALSE AND j.hidden = TRUE )")
    Optional<PostJob> getByIdAndApproved(Integer id);



    @Query("SELECT j FROM PostJob j WHERE " +
            "  j.recruiter.company.operated=TRUE "+
            "AND  j.recruiter.company.checkInformation=TRUE "+
            "AND  j.recruiter.company.checkCertificate=TRUE "+
            "AND  j.recruiter.enabled = TRUE "
    )
    Page<PostJob> findAllStatsuAndCty( Pageable pageable);

    @Query("SELECT j FROM PostJob j WHERE j.approved = ?1 AND j.hidden = ?2" +
            " AND j.recruiter.company.operated=TRUE "+
            "AND  j.recruiter.company.checkInformation=TRUE "+
            "AND  j.recruiter.company.checkCertificate=TRUE "+
            "AND  j.recruiter.enabled = TRUE "
    )
    Page<PostJob> findAllStatsuAndCty(boolean a , boolean b, Pageable pageable);

    @Query("SELECT j FROM PostJob j WHERE j.approved = ?1 AND j.hidden = ?2 AND j.recruiter.id=?3"+
            " AND j.recruiter.company.operated=TRUE "+
            "AND  j.recruiter.company.checkInformation=TRUE "+
            "AND  j.recruiter.company.checkCertificate=TRUE "+
            "AND  j.recruiter.enabled = TRUE ")
    Page<PostJob> findStatsuAnd1Cty(boolean a , boolean b, int cty ,Pageable pageable);

    @Query("SELECT j FROM PostJob j WHERE j.recruiter.id=?1"+
            " AND j.recruiter.company.operated=TRUE "+
            "AND  j.recruiter.company.checkInformation=TRUE "+
            "AND  j.recruiter.company.checkCertificate=TRUE "+
            "AND  j.recruiter.enabled = TRUE "
    )
    Page<PostJob> findAllStatsuAnd1Cty(int cty ,Pageable pageable);


    @Query("SELECT j FROM PostJob j WHERE j.approved = TRUE AND j.hidden = FALSE  AND j.postingDeadline > ?1")
    List<PostJob> findByApprovedAndPreEndTime(LocalDate date);
}

















