package com.sms.smsservice.service.impl;

import com.sms.smsservice.dto.ResponseBean;
import com.sms.smsservice.dto.SmsRequest;
import com.sms.smsservice.listener.MessageReceiverListenerImpl;
import com.sms.smsservice.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.jsmpp.bean.Address;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.ReplaceIfPresentFlag;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.SubmitMultiResult;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SmsSendImpl implements SmsService {
    private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();

    private final String smppIp = "127.0.0.1";

    private int port = 8086;

    private final String username = "localhost";

    private final String password = "password";

    private final String address = "AX-DEV";

    private static final String SERVICE_TYPE = "CMT";

    public ResponseBean sendSMS(SmsRequest smsRequest) {
        log.info("Broadcasting sms");
        SubmitMultiResult result = null;
        Address[] addresses = prepareAddress(Arrays.asList(smsRequest.getNumber()));
        SMPPSession session = initSession();
        ResponseBean responseBean = null;
        if (session != null) {
            try {
                result = session.submitMultiple(SERVICE_TYPE, TypeOfNumber.NATIONAL, NumberingPlanIndicator.UNKNOWN, address,
                        addresses, new ESMClass(), (byte) 0, (byte) 1, TIME_FORMATTER.format(new Date()), null,
                        new RegisteredDelivery(SMSCDeliveryReceipt.FAILURE), ReplaceIfPresentFlag.REPLACE,
                        new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), (byte) 0,
                        smsRequest.getMessage().getBytes());

                log.info("Messages submitted, result is {}", result);
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Error occurred while sending sms {}", e);
            }
        } else {
            log.error("Session creation failed with SMPP broker.");
            responseBean = getResponse("Session creation failed with SMPP broker.", 400);

        }
        if (result != null && result.getUnsuccessDeliveries() != null && result.getUnsuccessDeliveries().length > 0) {
            log.error("message not delivered");
            responseBean = getResponse("message not delivered", 400);


        } else {
            log.info("Pushed message to broker successfully");
            responseBean = getResponse("Pushed message to broker successfully", 200);
        }
        if (session != null) {
            session.unbindAndClose();
        }
        return responseBean;
    }

    public ResponseBean getResponse(String errorMessage, Integer errorCode) {
        return ResponseBean.builder().message(errorMessage).code(errorCode).build();
    }

    private Address[] prepareAddress(List numbers) {
        Address[] addresses = new Address[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            addresses[i] = new Address(TypeOfNumber.NATIONAL, NumberingPlanIndicator.UNKNOWN, (String) numbers.get(i));
        }
        return addresses;
    }

    private SMPPSession initSession() {
        SMPPSession session = new SMPPSession();
        try {
            session.setMessageReceiverListener(new MessageReceiverListenerImpl());
            String systemId = session.connectAndBind(smppIp, Integer.valueOf(port), new BindParameter(BindType.BIND_TX, username, password, "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
            log.info("Connected with SMPP with system id {}", systemId);
        } catch (IOException e) {
            log.error("I/O error occured", e);
            session = null;
        }
        return session;
    }
}

