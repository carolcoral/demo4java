package site.cnkj.kafka.service.impl;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import site.cnkj.kafka.service.KafkaBatchMessageListenerService;

import java.util.List;

/*
 * @version 1.0 created by LXW on 2019/2/15 13:49
 */
@Service
public class KafkaBatchMessageListenerServiceImpl<K,V> implements KafkaBatchMessageListenerService<K, V> {

    @Override
    public void onMessage(List<ConsumerRecord<K, V>> data) {

    }

    /**
     * Invoked with data from kafka. The default implementation throws
     * {@link UnsupportedOperationException}.
     *
     * @param data           the data to be processed.
     * @param acknowledgment the acknowledgment.
     */
    @Override
    public void onMessage(List<ConsumerRecord<K, V>> data, Acknowledgment acknowledgment) {

    }

    @Override
    public void onMessage(List<ConsumerRecord<K, V>> data, Consumer<?, ?> consumer) {
    }

    /**
     * Invoked with data from kafka and provides access to the {@link Consumer}. The
     * default implementation throws {@link UnsupportedOperationException}.
     *
     * @param data           the data to be processed.
     * @param acknowledgment the acknowledgment.
     * @param consumer       the consumer.
     * @since 2.0
     */
    @Override
    public void onMessage(List<ConsumerRecord<K, V>> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {

    }
}
