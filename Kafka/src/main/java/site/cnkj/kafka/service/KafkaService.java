package site.cnkj.kafka.service;

/*
 * @version 1.0 created by LXW on 2019/1/16 16:42
 */
public interface KafkaService {

    Object producerMsg(String ...args);


    Object listenerMsg(String ...args);

}
