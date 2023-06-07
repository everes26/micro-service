package com.status.api.controller;

import com.status.api.request.FeelingPostRequest;
import com.status.api.response.FeelingPostResponse;
import com.status.api.response.PostResponse;
import com.status.api.service.FeelingPostService;
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
public class FeelingPostController {

    private final FeelingPostService feelingPostService;

    public FeelingPostController(FeelingPostService feelingPostService) {
        this.feelingPostService = feelingPostService;
    }

    @Operation(description = "Create feeling")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create feeling successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/feeling")
    public ResponseEntity<FeelingPostResponse> addFeeling(HttpServletRequest httpServletRequest, @RequestBody FeelingPostRequest request) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(feelingPostService.addFeelingPost(request), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }

    @Operation(description = "Delete feeling by postId and userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete feeling successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/delete-feeling")
    public ResponseEntity<Boolean> deleteFeeling(HttpServletRequest httpServletRequest, @RequestBody FeelingPostRequest request) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        feelingPostService.removeFeelingPost(request);
        return new ResponseEntity<>(Boolean.TRUE, getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }

    @Operation(description = "Check feeling post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check feeling post successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/check-feeling")
    public ResponseEntity<Boolean> checkFeeling(HttpServletRequest httpServletRequest, @RequestBody FeelingPostRequest request) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(feelingPostService.existsByPostIdAndUserId(request), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }
}
