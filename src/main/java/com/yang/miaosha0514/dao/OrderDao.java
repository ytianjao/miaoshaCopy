package com.yang.miaosha0514.dao;


import com.yang.miaosha0514.domain.MiaoshaOrder;
import com.yang.miaosha0514.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDao {

    @Select("select * from miaosha_order where user_id = #{userId} and goods_id = #{goodsId}")
    MiaoshaOrder getMiaoShaOrderByUserIdGoodsId(@Param("userId") Long userId, @Param("goodsId") long goodsId);


    @Insert("insert into order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=long.class, before=false, statement={" select last_insert_id()"})
    long insert(OrderInfo orderInfo);


    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    int insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    @Select("select * from order_info where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);


    @Delete("delete from order_info")
    public void deleteOrders();

    @Delete("delete from miaosha_order")
    public void deleteMiaoshaOrders();
}
