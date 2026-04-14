package com.trungquan.nongsan.repository;

import com.trungquan.nongsan.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByBlogIdOrderByCreatedAtDesc(Long blogId);

    List<Comment> findByBlogIdAndParentCommentIsNullOrderByCreatedAtDesc(Long blogId);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId ORDER BY c.createdAt DESC")
    List<Comment> findByParentCommentIdOrderByCreatedAtDesc(Long parentId);

    @Query("SELECT DISTINCT c FROM Comment c LEFT JOIN FETCH c.replies WHERE c.blog.id = :blogId AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    List<Comment> findTopLevelCommentsWithReplies(Long blogId);

}
