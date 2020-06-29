package indi.butterfly.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis缓存服务
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.16
 * @since 1.0.0
 */
@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void set(String key, String value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        this.redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public boolean hasKey(String key) {
        if (key != null)
            return this.redisTemplate.hasKey(key);
        else return false;
    }

    public void delete(String key) {
        this.redisTemplate.delete(key);
    }

    public void batchDelete(String keyPattern) {
        Set<String> keys = this.redisTemplate.keys(keyPattern);
        if (keys != null && keys.size() > 0)
            this.redisTemplate.delete(keys);
    }

    public StringRedisTemplate getTemplate() {
        return this.redisTemplate;
    }

    public String get(String key) {
        return this.redisTemplate.opsForValue().get(key);
    }
}
