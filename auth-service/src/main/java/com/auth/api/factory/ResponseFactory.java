package com.auth.api.factory;

import com.auth.api.enums.ErrorCode;
import com.auth.api.util.MessageConst;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

public class ResponseFactory {

    public static ResponseEntity error(String code, String message) {
        return error(HttpStatus.BAD_REQUEST, code, message, null);
    }

    public static ResponseEntity error(String code, String message, List<String> details) {
        return error(HttpStatus.BAD_REQUEST, code, message, details);
    }

    public static ResponseEntity error(HttpStatus httpStatus, String code, String message, List<String> details) {
        GenericResponse responseObject = new GenericResponse();
        responseObject
                .setSuccess(false)
                .setCode(code)
                .setMessage(message)
                .setDetails(details)
        ;
        return new ResponseEntity(responseObject, httpStatus);
    }

    public static ResponseEntity generalError() {
        GenericResponse responseObject = new GenericResponse();
        responseObject.setSuccess(false);
        responseObject.setCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        responseObject.setMessage(MessageConst.INTERNAL_SERVER_ERROR);
        responseObject.setDetails(Collections.singletonList(MessageConst.A0000));
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity generalError(String details) {
        GenericResponse responseObject = new GenericResponse();
        responseObject.setSuccess(false);
        responseObject.setCode(ErrorCode.INTERNAL_SERVER_ERROR.getCode());
        responseObject.setMessage(MessageConst.INTERNAL_SERVER_ERROR);
        responseObject.setDetails(Collections.singletonList(details));
        return new ResponseEntity<>(responseObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
