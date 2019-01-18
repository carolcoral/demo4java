package site.cnkj.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.List;

/**
 * Kafka监听类
 */
public class Listener {

    /**
     * kafka监听器，批量获取数据并加入到Future队列里解析
     * @param messageList 批量消费的数据集，ConsumerRecord包含数据和topic属性
     */
   @KafkaListener(clientIdPrefix ="#{__listener.topic}" ,topics = "#{__listener.topic}",groupId = "#{__listener.topic}.group")
    public void listen(List<ConsumerRecord<String, Object>> messageList)
    {
        //批量消费数据并解析，加入Futre方便回调操作
        System.out.println("this is listener:"+messageList);
    }

}
