package services;

import java.time.Duration;
import java.util.List;

public interface RedisService {
    void save(String key, Object value);
    void save(String key, Object value, Duration ttl);
    <T> T get(String key, Class<T> type);
    void increment(String key);
    Long getCounter(String key);
    boolean exists(String key);
    void delete(String key);
    void setTtl(String key, Duration ttl);
    void saveToList(String key, String value);
    List<Object> getList(String key);
}
