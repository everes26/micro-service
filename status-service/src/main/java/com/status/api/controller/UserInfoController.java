package com.status.api.controller;

import com.status.api.request.UserInfoRequest;
import com.status.api.response.UserInfoResponse;
import com.status.api.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {
    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Operation(description = "Create userInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Create userInfo successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/userInfo")
    public ResponseEntity<UserInfoResponse> addUserInfo(@RequestBody UserInfoRequest request) {
        return new ResponseEntity<>(userInfoService.addUserInfo(request), HttpStatus.OK);
    }

    @Operation(description = "Get userInfo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get userInfo successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @GetMapping("/userInfo")
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestParam Long id) {
        return new ResponseEntity<>(userInfoService.getUserInfo(id), HttpStatus.OK);
    }
}
