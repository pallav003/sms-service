package com.sms.smsservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SmsRequest implements Serializable {
    private static final Long serialVersionUID = 1L;
    private String number;
    private String message;
}
