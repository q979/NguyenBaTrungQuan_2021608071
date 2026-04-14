package com.trungquan.nongsan.dto;

import lombok.Data;

@Data
public class CommentDto {

    String content;

    Long blogId;

    Long userId;

    Long parentId;

    Long parentCommentId;

}
