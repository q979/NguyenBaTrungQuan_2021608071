package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.entity.Comment;
import com.trungquan.nongsan.repository.BlogRepository;
import com.trungquan.nongsan.repository.CommentRepository;
import com.trungquan.nongsan.service.CommentService;
import com.trungquan.nongsan.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    UserRepository userRepository;
    BlogRepository blogRepository;

    @Override
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    @Override
    public Comment saveComment(String content, Long blogId, Long userId, Long parentId) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(userRepository.findById(userId).orElse(null));
        comment.setBlog(blogRepository.findById(blogId).orElse(null));
        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId).orElse(null);
            comment.setParentComment(parent);
        }
        Comment saved = commentRepository.save(comment);
        // Set parentCommentId để WebSocket gửi về cho browser biết comment cha
        if (parentId != null) {
            saved.setParentCommentId(parentId);
        }
        return saved;
    }

    @Override
    public List<Comment> findByBlogIdOrderByCreatedAtDesc(Long blogId) {
        return commentRepository.findByBlogIdOrderByCreatedAtDesc(blogId);
    }

    @Override
    public List<Comment> findTopLevelCommentsByBlogId(Long blogId) {
        return commentRepository.findByBlogIdAndParentCommentIsNullOrderByCreatedAtDesc(blogId);
    }

    @Override
    public List<Comment> findTopLevelCommentsWithReplies(Long blogId) {
        return commentRepository.findTopLevelCommentsWithReplies(blogId);
    }
}
