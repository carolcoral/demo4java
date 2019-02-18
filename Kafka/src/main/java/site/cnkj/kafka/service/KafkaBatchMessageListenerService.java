package site.cnkj.kafka.service;

import org.springframework.kafka.listener.BatchMessageListener;

import java.util.List;

/*
 * @version 1.0 created by LXW on 2019/2/15 11:25
 */
public interface KafkaBatchMessageListenerService<K,V> extends BatchMessageListener<K,V> {
}
