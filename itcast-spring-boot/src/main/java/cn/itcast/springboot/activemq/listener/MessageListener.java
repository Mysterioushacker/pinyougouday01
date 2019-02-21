package cn.itcast.springboot.activemq.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    @JmsListener(destination = "spring.boot.map.queue")
    public void receiveMsg(Map<String,Object> map){
        System.out.println(map);
    }
}
