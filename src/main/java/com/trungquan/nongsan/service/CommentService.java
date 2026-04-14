package com.trungquan.nongsan.service;

import com.trungquan.nongsan.entity.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getAll();
    Comment saveComment(String content, Long blogId, Long userId, Long parentId);

    List<Comment> findByBlogIdOrderByCreatedAtDesc(Long blogId);

    List<Comment> findTopLevelCommentsByBlogId(Long blogId);

    List<Comment> findTopLevelCommentsWithReplies(Long blogId);
}
