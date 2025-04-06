package org.itech.framework.javafxapp.demo.api.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class BasedResponse<T> {
    private Boolean ok;
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object error;
}
