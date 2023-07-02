package com.vladimirpandurov.springSecurity01B.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import static com.twilio.rest.api.v2010.account.Message.creator;

public class SmsUtils {

    public static final String FROM_NUMBER = "+13612824006";
    public static final String SID_KEY = "AC6772183c93d2d62dfacc96c1aa8cb517";
    public static final String TOKE_KEY = "";

    public static void sendSms(String toNumber, String messageBody){
        Twilio.init(SID_KEY, TOKE_KEY);
        Message message = creator(new PhoneNumber("+" + toNumber), new PhoneNumber(FROM_NUMBER), messageBody).create();
        System.out.println(message);
    }
}
