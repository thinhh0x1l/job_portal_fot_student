package com.jobportal.repostory;

import com.jobportal.entity.Tag;
import com.jobportal.model.TagType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Integer> {


    @Query("SELECT t FROM Tag t WHERE t.id IN ?1")
    Set<Tag> findByTagByIds(Set<Integer> ids);

    @Query("SELECT t FROM Tag t ORDER BY t.id DESC ")
    List<Tag> findByAllTag();

    @Query("SELECT t FROM Tag t WHERE t.tagType = ?1 ORDER BY t.id DESC ")
    List<Tag> findByAllTagByTypeTag(TagType tagType);
}
