package com.status.api.controller;

import com.status.api.request.CommentRequest;
import com.status.api.response.CommentResponse;
import com.status.api.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.status.api.util.FunctionUtil.getHttpHeaders;

@Slf4j
@RestController
@RequestMapping("/")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @Operation(description = "Get all comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all comment successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @GetMapping("/comments")
    public ResponseEntity<List<CommentResponse>> getComments(HttpServletRequest httpServletRequest, Long postId) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(commentService.getComments(postId), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }

    @Operation(description = "Create comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create comment successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/comment")
    public ResponseEntity<CommentResponse> addComment(HttpServletRequest httpServletRequest, @RequestBody CommentRequest request) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(commentService.addComment(request), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }

    @Operation(description = "Delete comment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete comment successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @DeleteMapping("/comment")
    public ResponseEntity<Long> deleteComment(HttpServletRequest httpServletRequest, @RequestParam Long id) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(commentService.deleteComment(id), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }
}
