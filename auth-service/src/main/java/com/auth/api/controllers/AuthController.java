package com.auth.api.controllers;

import com.auth.api.dto.AuthResponseDTO;
import com.auth.api.dto.LoginDto;
import com.auth.api.dto.RegisterDto;
import com.auth.api.enums.ErrorCode;
import com.auth.api.exception.BasicException;
import com.auth.api.models.Role;
import com.auth.api.models.Users;
import com.auth.api.repository.RoleRepository;
import com.auth.api.repository.UserRepository;
import com.auth.api.security.JWTGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @Operation(description = "Login account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login account successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(HttpServletRequest httpServletRequest, @RequestBody LoginDto loginDto){

        log.info("Path: " + httpServletRequest.getRequestURI());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerator.generateToken(authentication);
        Users user = userRepository.findByUsername(loginDto.getUsername()).get();
        AuthResponseDTO response = new AuthResponseDTO(token, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(description = "Register account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Register account successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server Error")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(HttpServletRequest httpServletRequest, @RequestBody RegisterDto registerDto) {

        log.info("Path: " + httpServletRequest.getRequestURI());

        if (StringUtils.isBlank(registerDto.getUsername())
                || StringUtils.isBlank(registerDto.getPassword())
                || StringUtils.isBlank(registerDto.getUsername()))
            throw BasicException.builder()
                    .code(ErrorCode.BAD_REQUEST.getCode())
                    .message("Username, Password and Email cannot be blank")
                    .errors(Collections.singletonList("Username, Password and Email cannot be blank"))
                    .build();

        if (Boolean.TRUE.equals(userRepository.existsByUsername(registerDto.getUsername())))
            throw BasicException.builder()
                    .code(ErrorCode.BAD_REQUEST.getCode())
                    .message("Username already registered")
                    .errors(Collections.singletonList("Username " + registerDto.getUsername() + " is already registered"))
                    .build();

        if (Boolean.TRUE.equals(userRepository.existsByEmail(registerDto.getEmail())))
            throw BasicException.builder()
                    .code(ErrorCode.BAD_REQUEST.getCode())
                    .message("Email already registered")
                    .errors(Collections.singletonList("Email " + registerDto.getEmail() + " is already registered"))
                    .build();

        Users user = new Users();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));
        user.setEmail(registerDto.getEmail());

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered success!", HttpStatus.OK);
    }
}
