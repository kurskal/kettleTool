package com.das.kettle.service;

import com.das.kettle.exception.KettleWorkPathException;
import org.pentaho.di.core.exception.KettleException;

import java.util.Map;

public interface KettleWork {

    void init(String kettleName) throws KettleException, KettleWorkPathException, Exception;

    Map<String, Object> runKettleWork(Map<String, String> params, String path) throws KettleException;

    String getWorkResult(String jobName, String lastCarteObjectId) throws Exception;

    String getWorkResult(Map<String,Object> map) throws Exception;

}
