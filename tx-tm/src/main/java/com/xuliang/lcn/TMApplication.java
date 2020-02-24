package com.xuliang.lcn;

import com.xuliang.lcn.support.TxLcnManagerBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.xuliang.*"})
public class TMApplication {


    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(TMApplication.class);
        springApplication.setBanner(new TxLcnManagerBanner());
        springApplication.run(args);
    }


}

