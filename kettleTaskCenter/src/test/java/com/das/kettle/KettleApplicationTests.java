package com.das.kettle;

import com.das.kettle.aop.KettleRunAop;
import com.das.kettle.config.RedisConfig;
import com.das.kettle.config.RemoteSlaveServerConfig;
import com.das.kettle.util.RedisUtil;
import com.das.kettle.web.KettleController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.cluster.SlaveServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAspectJAutoProxy(exposeProxy = true)
public class KettleApplicationTests {

    @Autowired
    SlaveServer remoteSlaveServer;

    @Autowired
    KettleController kettleController;

    @Autowired
    KettleRunAop kettleRunAop;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    RedisConfig redisConfig;

    @Autowired
    RemoteSlaveServerConfig remoteSlaveServerConfig;


    public void testRemoteSlaveServer(){
        System.out.println(remoteSlaveServer);
    }

    @Test
    public void testRedis() {
        System.out.println(redisUtil.hget("kettle","a"));
    }

    @Test
    public void testRedisString() {
        redisUtil.set("test","1");
        System.out.println(redisUtil.get("test"));
    }

    @Test
    public void testKettleConfig(){
        try {
            SlaveServer slaveServer = remoteSlaveServerConfig.getRemoteSlaveServer("kettle1");
            System.out.println(slaveServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
