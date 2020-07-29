package manage.service;

import manage.dao.ManageDao;
import manage.util.RedisUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ManageService implements ManageDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageService.class);
    private static final String REDIS_KEY_OF_KETTLE_NAMES = "kettleNames";
    private static final String EDIT_KETTLE_LOCK_URL = "http://kettleTask/ktask/editKettleConfig";
    private static final String GET_KETTLE_LOCK_INFO_URL = "http://kettleTask/ktask/getKettleLockInfo";
    private static final String CONCURRENCY_KEY = "concurrency";
    private static final String KETTLE_NAME_KEY = "kettleName";

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RestTemplate restTemplate;

    /**
     * 获取所有kettle配置列表
     * @return
     */
    @Override
    public List<Map<String,Object>> listOfKettleConfig(){
        List<Map<String,Object>> listOfKettleConfig = new LinkedList<Map<String,Object>>();
        Set<Object> kettleNameSet = redisUtil.smembers(REDIS_KEY_OF_KETTLE_NAMES);
        Iterator<Object> iterator = kettleNameSet.iterator();
        while(iterator.hasNext()){
            String kettleName = iterator.next().toString();
            Map<String,Object> kettleConfigMap = redisUtil.hgetall(kettleName);
            kettleConfigMap.put(KETTLE_NAME_KEY, kettleName);
            listOfKettleConfig.add(kettleConfigMap);
        }
        return listOfKettleConfig;
    }

    /**
     * 保存kettle配置，包括名字和配置
     * @param kettleName
     * @param kettleConfigMap
     * @return
     */
    @Override
    public int saveKettleConfig(String kettleName, Map<String,Object> kettleConfigMap)throws Exception {
        Integer lockNum = "".equals((String)kettleConfigMap.get(CONCURRENCY_KEY)) ? 1 : Integer.parseInt((String)kettleConfigMap.get(CONCURRENCY_KEY));
        editKettleLockNum(kettleName, lockNum);
        redisUtil.sadd(REDIS_KEY_OF_KETTLE_NAMES, kettleName);
        Iterator<Map.Entry<String, Object>> iterator = kettleConfigMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            redisUtil.hset(kettleName, entry.getKey(), entry.getValue());
        }
        return 1;
    }

    /**
     * 修改kettle名
     * @param oldKettleName
     * @param newKettleName
     * @return
     */
    @Override
    public int updateKettleName(String oldKettleName, String newKettleName) throws Exception{
        try {
            //check if repeat
            Set<Object> keySet = redisUtil.smembers(REDIS_KEY_OF_KETTLE_NAMES);
            keySet.remove(oldKettleName);
            if(keySet.contains(newKettleName)){
                throw new Exception("kettle名重复");
            }
            if(redisUtil.exists(oldKettleName)){
                redisUtil.rename(oldKettleName, newKettleName);
            }
            redisUtil.srem(REDIS_KEY_OF_KETTLE_NAMES, oldKettleName);
            redisUtil.sadd(REDIS_KEY_OF_KETTLE_NAMES, newKettleName);
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return 1;
    }

    /**
     * 删除指定name的kettle配置
     * @param kettleName
     * @return
     */
    @Override
    public boolean deleteKettleConfig(String kettleName) {
        boolean configRemoveSuccess = redisUtil.del(kettleName);
        boolean keyRemoveSuccess = redisUtil.srem(REDIS_KEY_OF_KETTLE_NAMES, kettleName);
        return keyRemoveSuccess && configRemoveSuccess;
    }

    /**
     * 根据name获取kettle配置
     * @param kettleName
     * @return
     */
    public Map<String,Object> getKettleConfigByName(String kettleName){
        if(null == kettleName || "".equals(kettleName)){
            return null;
        }
        Map<String,Object> configMap = redisUtil.hgetall(kettleName);
        return configMap;
    }

    /**
     * 判断是否存在重名
     * @param kettleName
     * @return
     */
    public boolean checkKettleName(String kettleName){
        Set<Object> kettleSet = redisUtil.smembers(REDIS_KEY_OF_KETTLE_NAMES);
        return kettleSet.contains(kettleName);
    }

    /**
     * 修改kettle并发量数量
     */
    public Map<String, Object> editKettleLockNum(String kettleName, int lockNum)throws Exception {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("kettleName", kettleName);
        paramMap.add("lockNum", lockNum);
        Map<String, Object> lockInfoMap = restTemplate.postForObject(EDIT_KETTLE_LOCK_URL, paramMap, Map.class);
        if(!"S".equals(lockInfoMap.get("message"))) {
            throw new Exception((String)lockInfoMap.get("message"));
        }
        return lockInfoMap;
    }

    /**
     * 获取并发情况
     */
    public Map<String, Object> getConcurrencyInfoByName(String name) {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("kettleName", name);
        Map<String, Object> lockInfoMap = restTemplate.postForObject(GET_KETTLE_LOCK_INFO_URL, paramMap, Map.class);
        return lockInfoMap;
    }
}
