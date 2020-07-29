package com.das.kettle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableEurekaClient
@SpringBootApplication
public class KettleApplication {

    @RequestMapping("/")
    public String index(){
        return "this server has been started";
    }

    public static void main(String[] args) {
        SpringApplication.run(KettleApplication.class, args);
    }

}
