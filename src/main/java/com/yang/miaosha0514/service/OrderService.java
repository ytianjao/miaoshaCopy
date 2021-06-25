package com.yang.miaosha0514.service;

import com.yang.miaosha0514.dao.OrderDao;
import com.yang.miaosha0514.domain.MiaoShaUser;
import com.yang.miaosha0514.domain.MiaoshaOrder;
import com.yang.miaosha0514.domain.OrderInfo;
import com.yang.miaosha0514.redis.OrderKey;
import com.yang.miaosha0514.redis.RedisService;
import com.yang.miaosha0514.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    RedisService redisService;

    public MiaoshaOrder getMiaoShaOrderByUserIdGoodsId(Long userId, long goodsId) {
        MiaoshaOrder order = redisService.get(OrderKey.getMiaoshaOrderByUidGid, "" + userId + "_" + goodsId, MiaoshaOrder.class);
        if (order != null){
            return order;
        }
        return orderDao.getMiaoShaOrderByUserIdGoodsId(userId, goodsId);
    }


    @Transactional
    public OrderInfo createOrder(MiaoShaUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());

        orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());

        orderDao.insertMiaoshaOrder(miaoshaOrder);

        redisService.set(OrderKey.getMiaoshaOrderByUidGid, "" + user.getId() + "_" + goods.getId(), miaoshaOrder);

        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {

        return orderDao.getOrderById(orderId);
    }

    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteMiaoshaOrders();
    }
}
