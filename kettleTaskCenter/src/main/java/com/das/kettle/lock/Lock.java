package com.das.kettle.lock;

import com.das.kettle.util.RedisUtil;

import org.ops4j.pax.exam.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Component
@Configuration
public class Lock {
    private Map<String, Semaphore> semaphoreMap = new ConcurrentHashMap<>();
    private Map<String, Integer> semaphoreTotalMap = new ConcurrentHashMap<>();
    private static final String REDIS_KEY_OF_KETTLE_NAMES = "kettleNames";
    private static final int DEFAULT_CONCURRENCY_NUM = 1;
    private static final String CONCURRENCY_KEY = "concurrency";
    private static final String KETTLE_NAME_KEY = "kettleName";

    @Autowired
    RedisUtil redisUtil;

    @Bean
    public Lock lockUtil(){
        Lock lockUtil = new Lock();
        List<Map<String,Object>> kettleConfigMap = listOfKettleConfig();
        for(Map<String,Object> kettleConfig : kettleConfigMap) {
            int concurrency = kettleConfig.containsKey(CONCURRENCY_KEY) ? Integer.parseInt((String)kettleConfig.get(CONCURRENCY_KEY)) : DEFAULT_CONCURRENCY_NUM;
            lockUtil.semaphoreMap.put((String)kettleConfig.get(KETTLE_NAME_KEY), new Semaphore(concurrency));
            lockUtil.semaphoreTotalMap.put((String)kettleConfig.get(KETTLE_NAME_KEY), concurrency);
        }
        return lockUtil;
    }

    public List<Map<String,Object>> listOfKettleConfig(){
        List<Map<String,Object>> listOfKettleConfig = new LinkedList<Map<String,Object>>();
        Set<Object> kettleNameSet = redisUtil.smembers(REDIS_KEY_OF_KETTLE_NAMES);
        Iterator<Object> iterator = kettleNameSet.iterator();
        while(iterator.hasNext()){
            String kettleName = iterator.next().toString();
            Map<String,Object> kettleConfigMap = redisUtil.hgetall(kettleName);
            kettleConfigMap.put(KETTLE_NAME_KEY, kettleName);
            listOfKettleConfig.add(kettleConfigMap);
        }
        return listOfKettleConfig;
    }

    public int getLockTotalNum(String kettleName) {
        if("".equals(kettleName) || kettleName == null || ! semaphoreTotalMap.containsKey(kettleName)) {
            return 0;
        }
        return semaphoreTotalMap.get(kettleName);
    }

    public Semaphore getSemaphore(String name){
        return semaphoreMap.get(name);
    }

    public void resetSemaphore(String name, int num) {
        Semaphore semaphore = new Semaphore(num);
        semaphoreMap.put(name, semaphore);
        semaphoreTotalMap.put(name, num);
    }
}
