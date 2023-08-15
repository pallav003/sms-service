package com.sms.smsservice.service;

import com.sms.smsservice.dto.ResponseBean;
import com.sms.smsservice.dto.SmsRequest;

import java.util.List;

public interface SmsService {
    ResponseBean sendSMS(SmsRequest smsRequest);

    }
