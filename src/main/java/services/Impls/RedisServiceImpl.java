package services.Impls;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import services.RedisService;
import java.time.Duration;
import java.util.List;

@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void save(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        return type.cast(value);
    }

    @Override
    public void increment(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    @Override
    public Long getCounter(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return 0L;
        }
        return Long.parseLong(value.toString());
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void setTtl(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
    }

    @Override
    public void saveToList(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
        redisTemplate.opsForList().trim(key, 0, 2);
    }

    @Override
    public List<Object> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }
}
