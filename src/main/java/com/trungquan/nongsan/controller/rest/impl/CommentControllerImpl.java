package com.trungquan.nongsan.controller.rest.impl;

import com.trungquan.nongsan.controller.rest.ICommentController;
import com.trungquan.nongsan.controller.rest.base.VsResponseUtil;
import com.trungquan.nongsan.dto.CommentDto;
import com.trungquan.nongsan.entity.Comment;
import com.trungquan.nongsan.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentControllerImpl implements ICommentController {

    CommentService commentService;

    @Override
    public ResponseEntity<?> getCommentHistory() {
        return VsResponseUtil.ok(HttpStatus.OK, commentService.getAll());
    }


    @MessageMapping("/comment")
    @SendTo("/blog/comments")
    public Comment sendComment(CommentDto comment) {
        Comment saved = commentService.saveComment(comment.getContent(), comment.getBlogId(), comment.getUserId(), comment.getParentId());
        // Set parentCommentId để Jackson serialize gửi về browser
        if (comment.getParentId() != null) {
            saved.setParentCommentId(comment.getParentId());
        }
        return saved;
    }

}
