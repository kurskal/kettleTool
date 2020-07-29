package com.das.kettle.config;


import org.pentaho.di.cluster.SlaveServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RemoteSlaveServerConfig {

    @Autowired
    private RedisConfig redisConfig;

    private Map<String,SlaveServer> remoteSlaveServerMap = new HashMap<String,SlaveServer>();

    public synchronized SlaveServer getRemoteSlaveServer(String kettleName)throws Exception{
        boolean slaveServerInitialized = remoteSlaveServerMap.containsKey(kettleName);
        if(slaveServerInitialized){
            return  remoteSlaveServerMap.get(kettleName);
        }
        Map<String, Object> kettleConfigMap = redisConfig.getKettleConfigFromRedisByName(kettleName);
        if (kettleConfigMap.size() > 0) {
            SlaveServer remoteSlaveServer = new SlaveServer(null, (String) kettleConfigMap.get("host")
                    , (String) kettleConfigMap.get("port"), (String) kettleConfigMap.get("username")
                    , (String) kettleConfigMap.get("pwd"));
            remoteSlaveServerMap.put(kettleName, remoteSlaveServer);
            return remoteSlaveServer;
        } else {
            throw new Exception("找不到对应的kettle配置");
        }
    }
}
