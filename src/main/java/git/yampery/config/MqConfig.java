package git.yampery.config;

import git.yampery.msmq.RedisMQ;
import git.yampery.msmq.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * @decription MqConfig
 * <p>消息队列配置</p>
 * @author Yampery
 * @date 2018/2/9 14:26
 *
 * 根据不同的架构可选择使用XML配置
 * ---------------------------------------------------
 *
    <bean id="redisMQ" class="git.yampery.msmq.RedisMQ">
        <property name="monitorCount" value="15"/>
        <property name="routes">
            <list>
                <bean class="git.yampery.msmq.Route">
                    <property name="queue" value="${mq.queue.first}"/>
                    <property name="list" value="${mq.consumer.first}"/>
                </bean>
                <bean class="git.yampery.msmq.Route">
                    <property name="queue" value="${mq.queue.second}"/>
                    <property name="list" value="${mq.consumer.second}"/>
                </bean>
            </list>
        </property>
    </bean>

 * ----------------------------------------------------
 */
@Configuration
public class MqConfig {

    @Bean(name = "redisMQ")
    @Primary
    public RedisMQ getRedisMq() {
        RedisMQ redisMQ = new RedisMQ();
        // 配置监听队列元素数量
        redisMQ.setMonitorCount(monitorCount);
        // 配置路由表
        redisMQ.setRoutes(routeList());
        return redisMQ;
    }

    /**
     * 返回路由表
     * @return
     */
    public List<Route> routeList() {
        List<Route> routeList = new ArrayList<>();
        Route routeFirst = new Route(queueFirst, listFirst);
        Route routeSecond = new Route(queueSecond, listSecond);
        routeList.add(routeFirst);
        routeList.add(routeSecond);
        return routeList;
    }

    @Value("${mq.monitor.count}")
    private int monitorCount;
    @Value("${mq.queue.first}")
    private String queueFirst;
    @Value("${mq.queue.second}")
    private String queueSecond;
    @Value("${mq.consumer.first}")
    private String listFirst;
    @Value("${mq.consumer.second}")
    private String listSecond;
}
