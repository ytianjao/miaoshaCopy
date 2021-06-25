package com.yang.miaosha0514.rabbitmq;

import com.yang.miaosha0514.domain.MiaoShaUser;
import com.yang.miaosha0514.domain.MiaoshaOrder;
import com.yang.miaosha0514.redis.RedisService;
import com.yang.miaosha0514.result.CodeMsg;
import com.yang.miaosha0514.result.Result;
import com.yang.miaosha0514.service.GoodsService;
import com.yang.miaosha0514.service.MiaoshaService;
import com.yang.miaosha0514.service.OrderService;
import com.yang.miaosha0514.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;


    @Autowired
    private MiaoshaService miaoshaService;



    public static Logger logger = LoggerFactory.getLogger(MQReceiver.class);

    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void receive(String message){
        logger.info("receive miaosha message ->" + message);
        MiaoShaMessage msg = RedisService.stringToBean(message, MiaoShaMessage.class);
        long goodsId = msg.getGoodsId();
        MiaoShaUser user = msg.getUser();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int count = goods.getStockCount();
        if (count <= 0) {
            return ;
        }

        MiaoshaOrder order = orderService.getMiaoShaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return ;
        }

        miaoshaService.miaosha(user, goods);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){
        logger.info("receive topic1 ->" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message){
        logger.info("receive topic2 ->" + message);
    }
}
