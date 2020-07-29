package com.das.kettle.service;

import com.das.kettle.lock.Lock;
import com.das.kettle.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Service
public class ConfigService {

    @Autowired
    Lock lockUtil;

    @Autowired
    RedisUtil redisUtil;

    public boolean isSemaphoreFree(String kettleName){
        Semaphore semaphore = lockUtil.getSemaphore(kettleName);
        if(semaphore == null){
            return true;
        }
        if(lockUtil.getLockTotalNum(kettleName) == semaphore.availablePermits()) {
            return true;
        }
        return false;
    }

    public void editLockNum(String kettleName, int num) throws Exception{
        if(isSemaphoreFree(kettleName) && setKettleLock(kettleName)) {
            lockUtil.resetSemaphore(kettleName, num);
            releaseKettleLock(kettleName);
        }else {
            throw new Exception("this kettle is in use, please try again when all tasks finished");
        }
    }

    public boolean setKettleLock(String kettleName) {
        return redisUtil.set("lock:" + kettleName, "lock");
    }

    public boolean releaseKettleLock(String kettleName) {
        return redisUtil.del("lock:" + kettleName);
    }

    public Map<String, Object> getKettleLockInfo(String kettleName) {
        if("".equals(kettleName)) {
            return null;
        }
        Semaphore semaphore = lockUtil.getSemaphore(kettleName);
        Map<String, Object> map = new HashMap<>();
        map.put("concurrencyTotal", String.valueOf(lockUtil.getLockTotalNum(kettleName)));
        map.put("concurrencyAvailable", semaphore == null ? "0" : String.valueOf(semaphore.availablePermits()));
        return map;
    }
}
