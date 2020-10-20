package com.avengereug.mall.auth;

import com.avengereug.mall.auth.thirdpart.client.SMSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ServiceAuthApplications {


    @Autowired
    SMSClient smsClient;

    @Test
    void sendSMS() {
        smsClient.send("15575489985", "78195");
    }
}
