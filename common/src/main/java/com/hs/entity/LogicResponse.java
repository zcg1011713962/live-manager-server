package com.hs.entity;

import com.hs.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogicResponse<T> {
    private ErrorCode status;
    private T data;
    private String msg;
}
