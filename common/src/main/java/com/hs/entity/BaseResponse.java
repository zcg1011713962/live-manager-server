package com.hs.entity;

import cn.hutool.http.HttpStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
@Builder
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 9172006565980035526L;
    private int status;
    private String message;
    private T data;
}
