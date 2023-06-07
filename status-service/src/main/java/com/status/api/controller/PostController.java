package com.status.api.controller;

import com.status.api.request.PostRequest;
import com.status.api.response.PostResponse;
import com.status.api.service.PostService;
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
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(description = "Get all post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all post successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getPosts(HttpServletRequest httpServletRequest) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(postService.getPosts(), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }

    @Operation(description = "Create post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create post successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/post")
    public ResponseEntity<PostResponse> addPost(HttpServletRequest httpServletRequest, @RequestBody PostRequest request) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(postService.addPost(request), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }

    @Operation(description = "Delete post by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete post successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @DeleteMapping("/post")
    public ResponseEntity<Long> deletePost(HttpServletRequest httpServletRequest, @RequestParam Long id) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(postService.deletePost(id), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }
}
