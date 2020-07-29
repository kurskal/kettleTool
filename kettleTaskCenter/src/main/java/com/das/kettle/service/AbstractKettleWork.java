package com.das.kettle.service;

import com.das.kettle.exception.KettleWorkPathException;
import org.osgi.service.component.annotations.Component;
import org.pentaho.di.ExecutionConfiguration;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AbstractKettleWork implements KettleWork {

     ThreadLocal<SlaveServer> localRemoteSlaveServer = new ThreadLocal<SlaveServer>();

    /**
     * 初始化kettle，拆分参数
     * @param kettleName
     * @throws KettleException
     * @throws KettleWorkPathException
     */
    public void init(String kettleName) throws KettleException, KettleWorkPathException, Exception {
        SlaveServer slaveServer = null;
        slaveServer = initRemoteSlaveServer(kettleName);
        this.localRemoteSlaveServer.set(slaveServer);
        //配置远程服务
        KettleEnvironment.init();
    }

    /**
     * 运行kettle作业，run方法由子类实现
     * @return
     * @throws KettleXMLException
     */
    public Map<String, Object> runKettleWork(Map<String, String> params,String path) throws KettleException {
        return run(params, path);
    }

    /**
     * 将参数中的作业路径与其他参数拆分
     * @param paramsMap
     * @return
     */
    private Map splitParams(Map<String,Object> paramsMap){
        Map afterSplitParams = new HashMap<String,Object>();
        Iterator<String> iterator = paramsMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if ("path".equals(key)) {
                afterSplitParams.put("jobPath",(String)paramsMap.get("path"));
                iterator.remove();
                break;
            }
        }
        afterSplitParams.put("workParams",paramsMap);
        return afterSplitParams;
    }

    public ExecutionConfiguration setKettleWorkParams(SlaveServer remoteSlaveServer, Map<String,String> workParams) throws KettleXMLException {
        return null;
    };

    public Map<String, Object> run(Map<String,String> params,String path) throws KettleException {
        return null;
    }

    public String getWorkResult(String jobName, String lastCarteObjectId) throws Exception {
        return null;
    }

    public String getWorkResult(Map<String, Object> map) throws Exception {
        return null;
    }

    public SlaveServer initRemoteSlaveServer(String kettleName)throws Exception{return null;};

}
