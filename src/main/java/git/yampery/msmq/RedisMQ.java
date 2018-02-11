package git.yampery.msmq;

import git.yampery.utils.JedisUtils;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @decription RedisMQ
 * <p>基于redis的消息队列</p>
 * <p>将整个redis作为消息池存储消息体，以ZSET为消息队列，LIST作为待消费列表<br>
 * 用Spring定时器作为监听器，每次监听ZSET中指定数量的消息<br>
 * 根据SCORE确定是否达到发送要求，如果达到，利用消息路由{@link Route}将消息路由到待消费list</p>
 * @author Yampery
 * @date 2017/11/2 15:49
 */
public class RedisMQ {

    /**
     * 消息池前缀，以此前缀加上传递的消息id作为key，以消息{@link Message}
     * 的消息体body作为值存储
     */
    private static final String MSG_POOL = "Message:Pool:";
    /**
     * 默认监听数量，对应监听zset队列前多少个元素
     */
    private static final int DEFAUT_MONITOR = 10;
    @Resource private JedisUtils jedisUtils;


    /**
     * 每次监听queue中元素的数量，可配置
     */
    private int monitorCount = DEFAUT_MONITOR;


    /**
     * 消息路由
     */
    private List<Route> routes;

    /**
     * 存入消息池
     * @param message
     * @return
     */
    public boolean addMsgPool(Message message) {

        if (null != message) {
            return jedisUtils.setex(MSG_POOL + message.getId(), message.getBody(), message.getTtl());
        }
        return false;
    }

    /**
     * 从消息池中删除消息
     * @param id
     * @return
     */
    public boolean deMsgPool(String id) {

        return jedisUtils.del(MSG_POOL + id);
    }

    /**
     * 像队列中添加消息
     * @param key
     * @param score 优先级
     * @param val
     * @return 返回消息id
     */
    public String enMessage(String key, long score, String val) {

        if (jedisUtils.zadd(key, score, val)) {
            return val;
        }
        return "";
    }

    /**
     * 从队列删除消息
     * @param id
     * @return
     */
    public boolean deMessage(String key, String id) {

        return jedisUtils.zdel(key, id);
    }

    /**
     * 消费
     * @return
     */
    public List<String> consume(String key) {

        long count = jedisUtils.countList(key);
        if (0 < count) {
            // 可根据需求做限制
            List<String> ids = jedisUtils.rangeList(key, 0, count - 1);
            if (ids != null) {
                List<String> result = new ArrayList<>();
                ids.forEach(l -> result.add(jedisUtils.get(MSG_POOL + l, "")));
                jedisUtils.removeListValue(key, ids);
                return result;
            } /// if end~
        }

        return null;
    }

    /**
     * 消息队列监听器<br>
     * 监听所有路由器，将消息队列中的消息路由到待消费列表
     */
    @Scheduled(cron="*/5 * * * * *")
    public void monitor() {
        // 获取消息路由
        int route_size;
        if (null == routes || 1 > (route_size = routes.size())) return;
        String queue, list;
        Set<String> set;
        for (int i = 0; i < route_size; i++) {
            queue = routes.get(i).getQueue();
            list = routes.get(i).getList();
            set = jedisUtils.getSoredSetByRange(queue, 0, monitorCount, true);
            if (null != set) {
                long current = System.currentTimeMillis();
                long score;
                for (String id : set) {
                     score = jedisUtils.getScore(queue, id).longValue();
                    if (current >= score) {
                        // 添加到list
                        if (jedisUtils.insertList(list, id)) {
                            // 删除queue中的元素
                            deMessage(queue, id);
                        } /// if end~
                    } /// if end~
                } /// for end~
            } /// if end~
        } /// for end~
    }

    public int getMonitorCount() {
        return monitorCount;
    }

    public void setMonitorCount(int monitorCount) {
        this.monitorCount = monitorCount;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
