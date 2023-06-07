package com.status.api.controller;

import com.status.api.request.UserStatusRequest;
import com.status.api.response.StatusResponse;
import com.status.api.response.UserStatusResponse;
import com.status.api.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.status.api.util.FunctionUtil.getHttpHeaders;

@Slf4j
@RestController
@RequestMapping("/")
public class StatusController {
    private final UserStatusService userStatusService;

    public StatusController(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    @Operation(description = "Create status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create status successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/status")
    public ResponseEntity<Long> addStatus(HttpServletRequest httpServletRequest, @RequestBody UserStatusRequest request) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(userStatusService.addStatus(request), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }

    @Operation(description = "Get all status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all status successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @GetMapping("/status")
    public ResponseEntity<List<UserStatusResponse>> getAllStatus(HttpServletRequest httpServletRequest) {
        log.info(String.format("Path: %s", httpServletRequest.getRequestURI()));
        return new ResponseEntity<>(userStatusService.getAllUserStatus(), getHttpHeaders(httpServletRequest.getRequestURI()), HttpStatus.OK);
    }

}
