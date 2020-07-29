package com.das.kettle.util;

import com.das.kettle.exception.KettleWorkPathException;
import com.das.kettle.web.KettleController;
import net.sf.json.JSONObject;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.*;

/**
 * 远程执行工具类
 *
 * @author huangrui
 */

public class KettleUtil {

    static Logger log = Logger.getLogger("KettleUtil");

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(KettleUtil.class);

    /**
     * 日志文件初始化
     *
     * @author huangrui
     */
    private static enum KettleUtilsFileHandler {
        INSTANCE;
        FileHandler fileHandler;

        KettleUtilsFileHandler() {
            try {
                String logPath = "";
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(new Date());
                fileHandler = new FileHandler(logPath + dateString);
                fileHandler.setFormatter(new Formatter() {//定义一个匿名类
                    @Override
                    public String format(LogRecord record) {
                        Date date = new Date();
                        String sDate = date.toString();
                        return "[" + sDate + "]" + "[" + record.getLevel() + "]" + ":" + record.getMessage() + "\n";
                    }
                });
                log.addHandler(fileHandler);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public FileHandler getFileHandler() {
            return fileHandler;
        }
    }

    /**
     * 校验kettle作业路径参数
     *
     * @param path
     * @return
     */
    public static String checkPathParamter(String path) {
        String message = "";
        if (path == null || path.isEmpty()) {
            message = "parameter error, parameter must contain path";
        } else {
            String[] strArr = path.split("\\.");
            if (strArr.length < 2) {
                message = "job path error, path must be xxx.kjb or xxx.ktr";
            } else {
                if (!"kjb".equals(strArr[1]) && !"ktr".equals(strArr[1])) {
                    message = "job type error, path must be xxx.kjb or xxx.ktr";
                }
            }
        }
        //checkFileExist(path);
        LOGGER.error(message);
        return message;
    }


    public static String getKettleWorkType(String path) throws KettleWorkPathException {
        String message = checkPathParamter(path);
        if(!message.isEmpty()){
            throw new KettleWorkPathException(message);
        }
        String[] strArr = path.split("\\.");
        if(strArr.length == 2){
            return strArr[1];
        }
        throw new KettleWorkPathException("kettle作业参数path获取异常");
    }

    public String checkFileExist(String path){
        return null;
    }

}
