package com.das.kettle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
