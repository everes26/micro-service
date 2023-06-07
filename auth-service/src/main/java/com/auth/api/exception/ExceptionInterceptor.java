package com.auth.api.exception;

import com.auth.api.enums.ErrorCode;
import com.auth.api.factory.ResponseFactory;
import io.jsonwebtoken.MalformedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@ControllerAdvice
public class ExceptionInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionInterceptor.class);

    public static final List<String> ACCEPT_LANGUAGES = new ArrayList<>();

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity handleMalformedJwtException(HttpServletRequest req, Exception e) {
        logger.error(e.getMessage(), e.toString());
        return ResponseFactory.error(ErrorCode.BAD_REQUEST.getCode(), "JWT error", Collections.singletonList(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(HttpServletRequest req, Exception e) throws Exception {
        if (AnnotationUtils.findAnnotation(e.getClass(), org.springframework.web.bind.annotation.ResponseStatus.class) != null) {
            throw e;
        }
        logger.error(e.getMessage(), e.toString());
        return ResponseFactory.generalError(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(HttpServletRequest req, Exception e) {
        logger.error(e.getMessage(), e.toString());
        MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            FieldError fieldError = (FieldError) error;

            String defaultMessage = error.getDefaultMessage();
            String message = getErrorMessageLanguage(req, defaultMessage);
            if (StringUtils.isNotEmpty(message)) {
                details.add(String.format("%s: %s", fieldError.getField(), message));
            } else {
                details.add(String.format("%s: %s", fieldError.getField(), defaultMessage));
            }
        }

        return ResponseFactory.error(ErrorCode.INVALID_ARGUMENT.getCode(), ErrorCode.INVALID_ARGUMENT.getMessage(), details);
    }

    @ExceptionHandler(BasicException.class)
    public ResponseEntity handleBasicException(HttpServletRequest req, Exception e) {
        logger.error("Basic Exception with details: {}", e.toString());

        BasicException baseException = (BasicException) e;
        String code = baseException.getCode();
        String message = baseException.getMessage();

        return ResponseFactory.error(code, message, baseException.getErrors());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handleAccessDeniedException(HttpServletRequest req, Exception e) {
        logger.error(e.getMessage(), e.toString());
        return ResponseFactory.error(ErrorCode.ACCESS_DENIED.getCode(), ErrorCode.ACCESS_DENIED.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handleBadCredentialsException(HttpServletRequest req, Exception e) {
        logger.error(e.getMessage(), e.toString());
        return ResponseFactory.error(ErrorCode.BAD_REQUEST.getCode(),
                "Username or password incorrect",
                Collections.singletonList("Username or password incorrect"));
    }

    public String getErrorMessageLanguage(HttpServletRequest req, String messageCode) {
        try {

            String acceptLanguage = req.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
            if (StringUtils.isEmpty(acceptLanguage) || !ACCEPT_LANGUAGES.contains(acceptLanguage)) {
                acceptLanguage = "";
            }

            Locale locale = new Locale(acceptLanguage, "");
            ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale, new UTF8Control());

            return resourceBundle.getString(messageCode);
        } catch (Exception e) {
            logger.error("Can not found message with code {}", messageCode);
            return null;
        }
    }
}

