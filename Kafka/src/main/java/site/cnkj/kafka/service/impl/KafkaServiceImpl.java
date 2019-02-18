package site.cnkj.kafka.service.impl;

import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import site.cnkj.kafka.config.KafkaConfig;
import site.cnkj.kafka.service.KafkaService;

import java.io.File;
import java.util.List;

/*
 * @version 1.0 created by LXW on 2019/1/16 16:42
 */
@Service
public class KafkaServiceImpl implements KafkaService {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public ListenableFuture producerMsg(String topicName, String data) {
        ListenableFuture res = kafkaTemplate.send(topicName, data);
        return res;
    }

    /**
     * kafka监听器，批量获取数据并加入到Future队列里解析
     * @param messageList 批量消费的数据集，ConsumerRecord包含数据和topic属性
     */
    //@KafkaListener(clientIdPrefix ="#{__listener.topic}" ,topics = "#{__listener.topic}",groupId = "#{__listener.topic}.group")
    @Override
    public void listenerMsg(List<ConsumerRecord<String, Object>> messageList)
    {
        //批量消费数据并解析，加入Futre方便回调操作
        System.out.println("this is listenerMsg:"+messageList);
    }

}
