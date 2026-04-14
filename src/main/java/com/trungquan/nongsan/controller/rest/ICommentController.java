package com.trungquan.nongsan.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.UnsupportedEncodingException;

@Validated
@Tag(name = "Get revenue controller", description = "Operations related to comment function using web socket")
public interface ICommentController {

    @Operation(summary = "Get Comment History", description = "Get all comment history")
    @GetMapping("/history")
    ResponseEntity<?> getCommentHistory();

}
