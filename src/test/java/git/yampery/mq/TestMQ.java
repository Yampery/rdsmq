package git.yampery.mq;

import com.alibaba.fastjson.JSONObject;
import git.yampery.msmq.Message;
import git.yampery.msmq.RedisMQ;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @decription TestMQ
 * <p>测试</p>
 * @author Yampery
 * @date 2018/2/9 18:43
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMQ {

    @Resource
    private RedisMQ redisMQ;
    @Value("${mq.queue.first}")
    private String MQ_QUEUE_FIRST;

    @Test
    public void testMq() {

        JSONObject jObj = new JSONObject();
        jObj.put("msg", "这是一条短信");

        String seqId = UUID.randomUUID().toString();

        // 将有效信息放入消息队列和消息池中
        Message message = new Message();
        message.setBody(jObj.toJSONString());
        // 可以添加延迟配置
        message.setDelay(20);
        message.setTopic("SMS");
        message.setCreateTime(System.currentTimeMillis());
        message.setId(seqId);
        message.setTtl(20 * 60);
        message.setStatus(0);
        message.setPriority(0);
        redisMQ.addMsgPool(message);
        redisMQ.enMessage(MQ_QUEUE_FIRST,
                message.getCreateTime() + message.getDelay() + message.getPriority(), message.getId());
    }
}
