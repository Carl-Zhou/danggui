package com.danggui.api.dao;

import org.apache.ibatis.annotations.Param;

import com.danggui.api.entity.CartVo;
import com.danggui.common.dao.BaseDao;

/**
 * @author GongXingSheng
 * 
 * @date 2017-08-11 09:14:25
 */
public interface ApiCartMapper extends BaseDao<CartVo> {
    void updateCheck(@Param("productIds") String[] productIds,
                     @Param("isChecked") Integer isChecked, @Param("userId") Long userId);

    void deleteByProductIds(@Param("productIds") String[] productIds);

    void deleteByUserAndProductIds(@Param("user_id") Long user_id,@Param("productIds") String[] productIds);

    void deleteByCart(@Param("user_id") Long user_id, @Param("session_id") Integer session_id, @Param("checked") Integer checked);
}
