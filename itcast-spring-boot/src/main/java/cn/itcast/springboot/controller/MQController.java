package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/mq")
@RestController
public class MQController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 发送一个map消息到MQ的队列
     * @return
     */
    @GetMapping("/send")
    public String sendMapMsg(){
        Map<String,Object> map = new HashMap<>();
        map.put("id",123L);
        map.put("name","传智播客");
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue",map);
        return "发送完成.";
    }

    /**
     * 发送一个手机短信消息到MQ的队列
     * @return
     */
    @GetMapping("/sendSmsMsg")
    public String sendSmsMsg(){
        Map<String,String> map = new HashMap<>();
        map.put("mobile","18127881683");
        map.put("signName","黑马");
        map.put("templateCode","SMS_125018593");
        map.put("templateParam","{\"code\":\"834721\"}");
        jmsMessagingTemplate.convertAndSend("itcast_sms_queue",map);

        return "发送sms消息完成";
    }
}
