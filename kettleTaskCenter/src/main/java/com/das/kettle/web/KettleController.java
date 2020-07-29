package com.das.kettle.web;

import com.das.kettle.aop.KettleRunAop;
import com.das.kettle.service.ConfigService;
import com.das.kettle.service.KettleJobService;
import com.das.kettle.service.KettleTransformationService;
import com.das.kettle.service.KettleWork;
import com.das.kettle.util.KettleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class KettleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KettleController.class);

    @Autowired
    KettleJobService kettleJobService;
    @Autowired
    KettleTransformationService kettleTransformationService;

    @Autowired
    KettleRunAop kettleRunAop;

    @Autowired
    ConfigService configService;


    @RequestMapping(value = "/runJob",method = RequestMethod.POST)
    public void runJob(@RequestBody Map<String,String> params, @RequestParam("path") String kettlePath,
                       @RequestParam("kettleName") String kettleName, HttpServletResponse response){
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String kettleWorkType = KettleUtil.getKettleWorkType(kettlePath);
            KettleWork kettleWork = "kjb".equals(kettleWorkType) ? kettleJobService : kettleTransformationService;
            kettleWork.init(kettleName);
            Map<String,Object> runInfoMap = kettleWork.runKettleWork(params, kettlePath);
            String kettleTaskInfo = kettleWork.getWorkResult(runInfoMap);
            response.getWriter().write(kettleTaskInfo);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            try {
                response.getWriter().write("server error : " + e.getMessage());
            } catch (IOException ex) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    @RequestMapping(value = "/editKettleConfig",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> editKettleConfig(String kettleName, Integer lockNum) {
        Map<String, Object> map = new HashMap<>();
        try {
            configService.editLockNum(kettleName, lockNum);
            map.put("message","S");
        } catch (Exception e) {
            map.put("message",e.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "/getKettleLockInfo",method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getKettleLockInfo(String kettleName) {
        return configService.getKettleLockInfo(kettleName);
    }
}
