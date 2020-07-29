package com.das.kettle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Configuration
@PropertySource(value = {"classpath:kettleConfig.properties"})
public class KettleConfig {

    @Value("${fina_host}")
    private String fina_host;

    @Value("${port}")
    private String port;

    @Value("${username}")
    private String username;

    @Value("${pwd}")
    private String pwd;

    @Value("${log}")
    private String log;

    @Value("${lockNum}")
    private String lockNum;

    public String getFina_host() {
        return fina_host;
    }

    public void setFina_host(String fina_host) {
        this.fina_host = fina_host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLockNum() {
        return lockNum;
    }

    public void setLockNum(String lockNum) {
        this.lockNum = lockNum;
    }

    @Override
    public String toString(){
        StringBuilder configInfo = new StringBuilder();
        configInfo.append("fina_host is ").append(fina_host)
                .append(" ;port is ").append(port).append(" ;username is ").append(username);
        return configInfo.toString();
    }
}
