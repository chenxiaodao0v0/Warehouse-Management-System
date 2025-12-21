package com.xmut.warehouse.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xmut.warehouse.module.user.entity.XmutUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层，继承MyBatis-Plus BaseMapper，自带基础CRUD方法
 */
@Mapper // 标记为MyBatis Mapper，让Spring扫描
public interface XmutUserMapper extends BaseMapper<XmutUser> {
    // 无需额外编写基础CRUD方法，BaseMapper已提供（selectById、insert、updateById、deleteById等）
    // 自定义查询：按账号查询用户（登录时使用），MyBatis-Plus可通过Wrapper实现，无需手写SQL
}
