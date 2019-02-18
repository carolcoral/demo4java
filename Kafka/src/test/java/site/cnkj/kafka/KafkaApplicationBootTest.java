package site.cnkj.kafka;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import site.cnkj.kafka.service.KafkaBatchMessageListenerService;
import site.cnkj.kafka.service.KafkaService;
import site.cnkj.util.FileOperationUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
public class KafkaApplicationBootTest {

    @Autowired
    private KafkaBatchMessageListenerService kafkaBatchMessageListenerService;

    @Autowired
    private KafkaService kafkaService;

    @Test
    public void test(){
    }

    //@Test
    //public void sendMessage(){
    //    for (int i = 0; i < 1000; i++) {
    //        String message = "a"+String.valueOf(i);
    //        kafkaService.producerMsg("test_topic", message);
    //    }
    //}

}