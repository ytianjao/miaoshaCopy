package com.yang.miaosha0514.rabbitmq;

import com.yang.miaosha0514.redis.RedisConfig;
import com.yang.miaosha0514.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQSender {

    public static Logger logger = LoggerFactory.getLogger(MQSender.class);
    @Autowired
    AmqpTemplate amqpTemplate;
    public void send(Object message){
        String msg = RedisService.beanToString(message);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);

        logger.info("send ->" + msg);
    }

    public void sendTopic(Object message){
        String msg = RedisService.beanToString(message);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY1, msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY2, msg+"2");

        logger.info("send topic ->" + msg);
    }


    public void sendFanout(Object message){
        String msg = RedisService.beanToString(message);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg+"1");
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", msg+"2");

        logger.info("send fanout ->" + msg);
    }

    public void sendMiaoshaMessage(MiaoShaMessage message) {
        String msg = RedisService.beanToString(message);
        logger.info("send miaosha message ->" + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }
}
