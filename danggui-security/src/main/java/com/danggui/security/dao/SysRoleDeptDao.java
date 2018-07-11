package com.danggui.security.dao;

import org.apache.ibatis.annotations.Mapper;

import com.danggui.common.dao.BaseDao;
import com.danggui.security.entity.SysRoleDeptEntity;

import java.util.List;

/**
 * 角色与部门对应关系
 * @author GongXings
 * @date 2018年7月10日 下午10:39:39
 */
@Mapper
public interface SysRoleDeptDao extends BaseDao<SysRoleDeptEntity> {

    /**
     * 根据角色ID，获取部门ID列表
     */
    List<Long> queryDeptIdList(Long roleId);

    /**
     * 根据用户ID获取权限部门列表
     *
     * @param userId
     * @return
     */
    List<Long> queryDeptIdListByUserId(Long userId);
}
