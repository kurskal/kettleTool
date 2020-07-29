package com.das.kettle.service;

import com.das.kettle.config.RemoteSlaveServerConfig;
import org.pentaho.di.ExecutionConfiguration;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.www.SlaveServerTransStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KettleTransformationService extends AbstractKettleWork {
    private static final Logger LOGGER = LoggerFactory.getLogger(KettleTransformationService.class);

    @Autowired
    protected RemoteSlaveServerConfig remoteSlaveServerConfig;

    /**
     * 设置ktr参数
     * @param remoteSlaveServer
     * @param workParams
     * @return
     */
    @Override
    public ExecutionConfiguration setKettleWorkParams(SlaveServer remoteSlaveServer, Map<String,String> workParams){
        //组装作业
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        transExecutionConfiguration.setRemoteServer(remoteSlaveServer);
        if (workParams != null && workParams.size() > 0) {
            transExecutionConfiguration.setParams(workParams);
        }
        LOGGER.info("ktr开始连接远程服务");
        return transExecutionConfiguration;
    };

    /**
     * 运行ktr作业
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> run(Map<String,String> params, String jobPath) throws KettleException {
        SlaveServer remoteSlaveServer = super.localRemoteSlaveServer.get();
        ExecutionConfiguration executionConfiguration = setKettleWorkParams(remoteSlaveServer,params);
        Map map = new HashMap<String,Object>();
        //根目录 /u01/kettle/data-integration/
        TransMeta transMeta = new TransMeta(jobPath);
        //执行远程作业
        String lastCarteObjectId = Trans.sendToSlaveServer(transMeta, (TransExecutionConfiguration)executionConfiguration, null, null);
        map.put("workMeta",transMeta);
        map.put("lastCarteObjectId",lastCarteObjectId);
        return map;
    }

    /**
     * 获取ktr运行结果
     * @param jobName
     * @param lastCarteObjectId
     * @return
     */
    @Override
    public String getWorkResult(String jobName, String lastCarteObjectId) throws Exception {
        SlaveServer remoteSlaveServer = super.localRemoteSlaveServer.get();
        SlaveServerTransStatus jobStatus = null;
        do {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jobStatus = remoteSlaveServer.getTransStatus(jobName, lastCarteObjectId, 0);
        } while (jobStatus != null && jobStatus.isRunning());
        LOGGER.debug(jobStatus.getStatusDescription());
        return jobStatus.getStatusDescription();
    }

    public String getWorkResult(Map<String,Object> map) throws Exception {
        return getWorkResult((String)map.get("workMate"), (String)map.get("lastCarteObjectId"));
    }

    public SlaveServer initRemoteSlaveServer(String kettleName)throws Exception{
        SlaveServer slaveServer = remoteSlaveServerConfig.getRemoteSlaveServer("kettle1");
        return slaveServer;
    }
}
