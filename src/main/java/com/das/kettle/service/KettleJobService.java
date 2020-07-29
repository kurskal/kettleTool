package com.das.kettle.service;

import com.das.kettle.config.RemoteSlaveServerConfig;
import com.das.kettle.web.KettleController;
import org.pentaho.di.ExecutionConfiguration;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.www.SlaveServerJobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KettleJobService extends AbstractKettleWork {
    private static final Logger LOGGER = LoggerFactory.getLogger(KettleJobService.class);

    @Autowired
    RemoteSlaveServerConfig remoteSlaveServerConfig;

    /**
     * 设置kjb参数
     * @param remoteSlaveServer
     * @param workParams
     * @return
     * @throws KettleXMLException
     */
    @Override
    public ExecutionConfiguration setKettleWorkParams(SlaveServer remoteSlaveServer, Map<String,String> workParams) throws KettleXMLException {
        //组装参数进作业
        JobExecutionConfiguration jobExecutionConfiguration  = new JobExecutionConfiguration();
        jobExecutionConfiguration.setRemoteServer(remoteSlaveServer);
        if (workParams != null && workParams.size() > 0) {
            jobExecutionConfiguration.setParams(workParams);
        }
        LOGGER.info("kjb开始连接远程服务");
        return jobExecutionConfiguration;
    };

    /**
     * 运行kjb
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> run(Map<String,String> params, String jobPath) throws KettleException {
        SlaveServer remoteSlaveServer = super.localRemoteSlaveServer.get();
        ExecutionConfiguration executionConfiguration = setKettleWorkParams(remoteSlaveServer,(Map<String, String>)params);
        Map map = new HashMap<String,Object>();
        //根目录 /u01/kettle/data-integration/
        JobMeta jobMeta = new JobMeta(jobPath, null);
        //执行远程作业
        String lastCarteObjectId = Job.sendToSlaveServer(jobMeta, (JobExecutionConfiguration)executionConfiguration, null, null);
        map.put("workMeta",jobMeta);
        map.put("lastCarteObjectId",lastCarteObjectId);
        return map;
    }

    /**
     * 获取kjb运行结果
     * @param jobName
     * @param lastCarteObjectId
     * @return
     */
    @Override
    public String getWorkResult(String jobName, String lastCarteObjectId){
        SlaveServer remoteSlaveServer = super.localRemoteSlaveServer.get();
        //获取执行结果
        SlaveServerJobStatus jobStatus = null;
        try {
            do {
                Thread.sleep(5000);
                jobStatus = remoteSlaveServer.getJobStatus(jobName, lastCarteObjectId, 0);
            } while (jobStatus != null && jobStatus.isRunning());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
