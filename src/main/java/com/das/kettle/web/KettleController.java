package com.das.kettle.web;

import com.das.kettle.aop.KettleRunAop;
import com.das.kettle.service.KettleJobService;
import com.das.kettle.service.KettleTransformationService;
import com.das.kettle.service.KettleWork;
import com.das.kettle.util.KettleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @RequestMapping(value = "/runJob",method = RequestMethod.POST)
    public String runJob(@RequestBody Map<String,String> params, @RequestParam("path") String kettlePath, @RequestParam("kettleName") String kettleName){
        String message = null;
        try {
            String kettleWorkType = KettleUtil.getKettleWorkType(kettlePath);
            KettleWork kettleWork = "kjb".equals(kettleWorkType) ? kettleJobService : kettleTransformationService;
            kettleWork.init(kettleName);
            Map<String,Object> runInfoMap = kettleWork.runKettleWork(params, kettlePath);
            kettleWork.getWorkResult(runInfoMap);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return message;
    }
}
