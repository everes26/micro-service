package com.auth.api.factory;

import com.auth.api.enums.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GenericResponse implements Serializable {

    @JsonProperty(value = "success")
    private boolean success;
    @JsonProperty(value = "code")
    private String code;
    @JsonProperty(value = "message")
    private String message;
    @JsonProperty(value = "details")
    private List<String> details;

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.code = errorMessage.name().toLowerCase();
    }

}
