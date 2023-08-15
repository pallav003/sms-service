package com.sms.smsservice.controller;

import com.sms.smsservice.dto.ResponseBean;
import com.sms.smsservice.dto.SmsRequest;
import com.sms.smsservice.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send-sms")
    public ResponseEntity<ResponseBean> sendSms(@RequestBody SmsRequest smsRequest){
        return ResponseEntity.ok().body(smsService.sendSMS(smsRequest));
    }

}
