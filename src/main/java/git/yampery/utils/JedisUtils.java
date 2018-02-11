package git.yampery.utils;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @decription JedisUtils
 * <p>redis操作工具</p>
 * @author Yampery
 * @date 2018/2/9 12:53
 */
@Component
public class JedisUtils {

    @Resource private JedisPool jedisPool;

    /**
     * 获取值
     * @param key
     * @param defaultVal
     * @return
     */
    public String get(String key, String defaultVal) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String val = jedis.get(key);
            return val == null ? defaultVal : val;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return defaultVal;
    }

    /**
     * 设置kv
     * @param key
     * @param val
     * @param seconds 有效期（秒）
     * @return
     */
    public boolean setex(String key, String val, int seconds) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(key, seconds, val);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 删除key
     * @param key
     * @return
     */
    public boolean del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 向zset添加元素
     * @param key
     * @param val
     * @param score
     * @return
     */
    public boolean zadd(String key, long score, String val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zadd(key, score, val);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 删除zset元素
     * @param key
     * @param val
     * @return
     */
    public boolean zdel(String key, String... val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrem(key, val) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 获取优先队列元素
     * @param key
     * @param startRange
     * @param endRange
     * @param orderByDesc 是否降序
     * @return
     */
    public Set<String> getSoredSetByRange(String key, int startRange, int endRange, boolean orderByDesc) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (orderByDesc) {
                return jedis.zrevrange(key, startRange, endRange);
            } else {
                return jedis.zrange(key, startRange, endRange);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    /**
     * 获取评分
     * @param key
     * @param member
     * @return
     */
    public Double getScore(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zscore(key, member);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

        /**
         * 获取list长度
         * @param key
         * @return
         */
    public long countList(String key) {
        if (key == null) {
            return 0;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.llen(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return 0L;
    }

    /**
     * 添加元素到list（使用右进）
     * @param key
     * @param val
     * @return
     */
    public boolean insertList(String key, String... val) {
        if (key == null || val == null) {
            return false;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.rpush(key, val);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    /**
     * 获取list元素（采用左出方式）
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> rangeList(String key, long start, long end) {
        if (key == null || key.equals("")) {
            return null;
        }
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return null;
    }

    /**
     * 删除list数据
     * @param key
     * @param count
     * @param value
     * @return
     */
    public boolean removeListValue(String key, long count, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.lrem(key, count, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return false;
    }

    public int removeListValue(String key, long count, List<String> values) {
        int result = 0;
        if (values != null && values.size() > 0) {
            for (String value : values) {
                if (removeListValue(key, count, value)) {
                    result++;
                }
            }
        }
        return result;
    }

    public int removeListValue(String key, List<String> values) {
        return removeListValue(key, 1, values);
    }

}
