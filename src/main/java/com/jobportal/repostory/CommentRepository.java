package com.jobportal.repostory;

import com.jobportal.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c WHERE c.company.id = ?1 ")
    Page<Comment> getCommentsByCompanyId(Integer companyId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.company.id = ?1 AND c.star = ?2")
    Page<Comment> getCommentsByCompanyIdAndStar(Integer companyId, int star ,Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.company.id = ?1 ")
    List<Comment> getCommentsByCompanyId(Integer companyId);

    @Query("SELECT COUNT(u) FROM Comment c JOIN c.likedByUser u WHERE c.id = :commentId")
    Integer countUsersLikedComment(@Param("commentId") Integer commentId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Comment c JOIN c.likedByUser u " +
            "WHERE c.id = :commentId AND u.id = :userId")
    boolean hasUserLikedComment(@Param("commentId") Integer commentId,
                                @Param("userId") Integer userId);


}
