package com.xmut.warehouse.module.goods.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.goods.entity.XmutGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface XmutGoodsMapper extends BaseMapper<XmutGoods> {

    /**
     * 根据仓库ID查询商品列表
     * 显式指定字段，并给数据库字段起别名，强制对应实体类属性
     */
    @Select("SELECT " +
            "id, " +
            "name AS goodsName, " +  // 数据库name → 实体类goodsName
            "category_id AS categoryId, " +
            "warehouse_id AS warehouseId, " +
            "price, " +
            "stock, " +
            "pic AS goodsImg, " +   // 数据库pic → 实体类goodsImg
            "status, " +
            "remark, " +
            "create_time AS createTime " +
            "FROM xmut_goods WHERE warehouse_id = #{warehouseId}")
    List<XmutGoods> selectByWarehouseId(String warehouseId);

    /**
     * 根据类别ID查询商品列表
     * 同样显式指定字段+别名
     */
    @Select("SELECT " +
            "id, " +
            "name AS goodsName, " +
            "category_id AS categoryId, " +
            "warehouse_id AS warehouseId, " +
            "price, " +
            "stock, " +
            "pic AS goodsImg, " +
            "status, " +
            "remark, " +
            "create_time AS createTime " +
            "FROM xmut_goods WHERE category_id = #{categoryId}")
    List<XmutGoods> selectByCategoryId(String categoryId);

    /**
     * 根据商品id查询商品列表
     * 显式指定字段+别名
     */
    @Select("select * from xmut_goods where id = #{id}")
    XmutGoods selectById(String id);
}
