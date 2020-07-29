package manage.web;

import manage.entity.KettleConfig;
import manage.service.ManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 单个kettle机器管理controller
 */
@Controller
@RequestMapping("/manage")
public class ManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManageController.class);

    @Autowired
    ManageService manageService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        manageService.listOfKettleConfig();
        return "index";
    }

    @RequestMapping(value = "/getListOfKettleConfig",method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String,Object>> getListOfKettleConfig(){
        return manageService.listOfKettleConfig();
    }

    @RequestMapping(value = "/edit")
    public ModelAndView editPage(String kettleName) {
        Map<String, Object> map = manageService.getKettleConfigByName(kettleName);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("editPage");
        mv.addObject("configMap", map);
        mv.addObject("kettleName", kettleName);
        return mv;
    }

    @RequestMapping(value = "/saveKettleConfig", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveKettleConfig(String kettleName, @RequestBody Map<String,Object> kettleConfigMap) {
        Map<String, Object> resultMap = new HashMap<>();
        String newKettleName = (String)kettleConfigMap.get("kettleName");
        try {
            if (!"".equals(kettleName) && null != kettleName) {
                //edit
                manageService.updateKettleName(kettleName, newKettleName);
            } else {
                //new
                if (manageService.checkKettleName(newKettleName)) {
                    throw new Exception("存在重复kettle名");
                }
            }
            int rowNum = manageService.saveKettleConfig(newKettleName, kettleConfigMap);
            resultMap.put("message", rowNum == 1 ? "S" : "保存时发生错误");
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            resultMap.put("message", e.getMessage());
        }
        return resultMap;
    }

    @RequestMapping(value = "/removeKettleConfig", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> removeKettleConfig(String kettleName) {
        boolean delResult = manageService.deleteKettleConfig(kettleName);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("message", delResult);
        return resultMap;
    }

    @RequestMapping(value = "/getKettleConfigByName", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getKettleConfigByName(String kettleName) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("config", manageService.getKettleConfigByName(kettleName));
        return resultMap;
    }

    @RequestMapping(value = "/concurrencyInfo")
    public ModelAndView concurrencyInfo(String kettleName) {
        Map<String, Object> map = manageService.getKettleConfigByName(kettleName);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("concurrencyInfoPage");
        mv.addObject("kettleName", kettleName);
        return mv;
    }

    @RequestMapping(value = "/getConcurrencyInfoByName", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getConcurrencyInfoByName(String kettleName) {
        Map<String, Object> concurrencyInfo = manageService.getConcurrencyInfoByName(kettleName);
        return concurrencyInfo;
    }
}
