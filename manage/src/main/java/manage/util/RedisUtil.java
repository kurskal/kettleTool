package manage.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Component
public class RedisUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key){
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * hgetall
     * @param key
     * @return
     */
    public Map<String,Object> hgetall(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * hmset 设置多个键值对
     * @param key
     * @param map
     * @return
     */
    public boolean hmset(String key, Map<String,Object> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * hset 放置值进hash中，如不存在则创建
     * @param key
     * @param item
     * @param value
     * @return
     */
    public boolean hset(String key,String item,Object value){
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        }catch(Exception e){
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取所有set值 smembers
     * @param key
     * @return
     */
    public Set<Object> smembers (String key){
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * sadd 添加值
     * @param key
     * @param value
     * @return
     */
    public boolean sadd(String key,String value){
        try {
            redisTemplate.opsForSet().add(key, value);
            return true;
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    /**
     * srem 删除set中的元素
     * @param key
     * @param member
     * @return
     */
    public boolean srem(String key, String member){
        try {
            redisTemplate.opsForSet().remove(key, member);
            return true;
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    /**
     * del 删除元素
     * @param key
     * @return
     */
    public boolean del(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * rename 修改 key 名字
     * @param oldName
     * @param newName
     * @return
     */
    public boolean rename(String oldName, String newName) {
        try {
            redisTemplate.rename(oldName, newName);
            return true;
        }catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    /**
     * 判断是否存在key
     * @return
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
}
