package site.cnkj.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;

/*
 * @version 1.0 created by LXW on 2019/1/16 16:42
 */
public interface KafkaService {

    /**
     * 生产者，发送消息
     * @param topicName 索引名
     * @param data 数据
     * @return
     */
    public ListenableFuture producerMsg(String topicName, String data);


    public void listenerMsg(List<ConsumerRecord<String, Object>> messageList);

}
