package com.sms.smsservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ResponseBean implements Serializable {
    private static final Long serialVersionUID = 1L;
    private Integer code;
    private String message;
}
