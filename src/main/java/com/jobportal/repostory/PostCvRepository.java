package com.jobportal.repostory;

import com.jobportal.entity.Intern;
import com.jobportal.entity.PostCv;
import com.jobportal.model.Major;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostCvRepository extends JpaRepository<PostCv, Integer> {

    @Query("SELECT p FROM PostCv p WHERE p.intern.status = 'NOT_PRACTICED' AND p.title LIKE %?1% AND p.major = ?2 ")
    Page<PostCv> findPostCvByNotTitleAndMajor(String keyword, Major major, Pageable pageable);
    @Query("SELECT p FROM PostCv p WHERE p.intern.status = 'NOT_PRACTICED' AND p.title LIKE %?1%")
    Page<PostCv> findPostCvByNotTitle(String keyword,  Pageable pageable);

    @Query("SELECT p.intern FROM PostCv p WHERE p.id = ?1")
    Optional<Intern> findInternByPostCvId(Integer id);
}
