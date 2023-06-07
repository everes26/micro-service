package com.auth.api.exception;

import com.auth.api.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

@Data
@Builder
public class BasicException extends RuntimeException {
    public static final BasicException INVALID_ARGUMENT = new BasicException(ErrorCode.INVALID_ARGUMENT.name().toLowerCase());
    public static final BasicException NOT_FOUND = new BasicException(ErrorCode.NOT_FOUND.name().toLowerCase());

    public final String code;
    public String message;
    public List<String> errors = new ArrayList<>();

    public BasicException(String code) {
        this.code = code;
    }

    public BasicException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BasicException(String code, String message, List<String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public BasicException withMessage(String message) {
        return new BasicException(this.code, message, this.errors);
    }

    public BasicException addErrors(String error) {
        List<String> errors = newArrayList(this.errors);
        errors.add(checkNotNull(error));
        return new BasicException(this.code, this.message, errors);
    }
}
