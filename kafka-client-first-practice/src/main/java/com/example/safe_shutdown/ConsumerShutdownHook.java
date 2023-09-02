package com.example.safe_shutdown;

import com.example.GlobalConstant;
import com.example.KafkaTestUtil;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;

/*
이런식으로 shut down hook을 등록해서 종료시 안전하게 컨슈머를 종료시킬 수 있다.
밖에서 wakenup()이 호출되면, poll()시 WakeupException이 발생한다.
 */
public class ConsumerShutdownHook {

    public static void main(String[] args) {
        try (KafkaConsumer<String,String> consumer = KafkaTestUtil.createConsumer(true, true)){
            //shut down hook 등록
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(Collections.singleton(GlobalConstant.TOPIC_NAME));
            //무한 반복 poll
            while (true){
                ConsumerRecords<String, String> poll = consumer.poll(Duration.ofSeconds(1));
                for (var record : poll){
                    System.out.println(record);
                }
            }
        } catch (WakeupException e) {
            System.out.println("wakeup 호출되었습니다.");
        }
        // try - with - resource 문을 사용하면 close()를 호출하지 않아도 된다.


    }
}
