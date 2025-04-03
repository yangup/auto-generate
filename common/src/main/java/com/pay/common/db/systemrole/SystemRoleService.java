package com.pay.common.db.systemrole;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pay.common.common.BaseService;
import com.pay.common.common.QueryMap;
import com.pay.common.common.ReturnData;
import com.pay.common.common.paginator.PageBounds;
import com.pay.common.paginator.PageList;
import com.pay.common.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.beans.BeanUtils.*;
import static com.pay.common.common.ReturnData.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色
 * SystemRole
 *
 * @author yangpu
 */
@Slf4j
@Service
public class SystemRoleService extends BaseService {

    private @Autowired SystemRoleMapper systemRoleMapper;
    private @Autowired RedissonUtil redissonUtil;

    @Transactional
    public String createEntity(SystemRoleEntity entity) {
        entity.id = redissonUtil.generateId(SystemRoleEntity.class, "SR");
        systemRoleMapper.insert(entity);
        return entity.id;
    }

    @Transactional
    public int deleteById(String id) {
        return systemRoleMapper.deleteById(id);
    }

    @Transactional
    public int updateEntity(SystemRoleEntity entity) {
        return systemRoleMapper.updateById(entity);
    }

    // TODO: 以下为查询
    public SystemRoleData findOne(QueryMap queryMap) {
        return find(queryMap, null).first();
    }

    public PageList<SystemRoleData> find(QueryMap queryMap) {
        return find(queryMap, null);
    }

    public PageList<SystemRoleData> find(QueryMap queryMap, PageBounds pageBounds) {
        queryMap = QueryMap.of(queryMap);
        pageBounds = pageBounds == null ? PageBounds.of() : pageBounds;
        LambdaQueryWrapper<SystemRoleEntity> wrapper = new LambdaQueryWrapper<>();
        if (isNotEmpty(queryMap.get("id"))) {
            wrapper.eq(SystemRoleEntity::getId, queryMap.get("id"));
        }
        wrapper.orderByDesc(SystemRoleEntity::getId);
        PageList<SystemRoleData> result = null;
        if (pageBounds.isNotPageFind()) {
            List<SystemRoleData> dataList = systemRoleMapper.selectList(wrapper).stream()
                    .map(this::convertToData)
                    .collect(Collectors.toList());
            result = new PageList<>(dataList);
        } else {
            Page<SystemRoleEntity> pageData = systemRoleMapper.selectPage(
                    Page.of(pageBounds.getPage(), pageBounds.getSize()), wrapper);
            List<SystemRoleData> dataList = pageData.getRecords().stream()
                    .map(this::convertToData)
                    .collect(Collectors.toList());
            result = new PageList<>(dataList, pageData.getTotal());
        }
        if (isEmpty(result) || queryMap.raw()) {
            return result;
        }
        return result;
    }

    private SystemRoleData convertToData(SystemRoleEntity entity) {
        SystemRoleData data = new SystemRoleData();
        copyProperties(entity, data);
        return data;
    }

    // TODO: ReturnData - findQuery
    public ReturnData<Object> findQuery(QueryMap queryMap, PageBounds pageBounds) {
        queryMap = QueryMap.of(queryMap);
        if (queryMap.all()) {
            return ReturnData.ok(this.find(QueryMap.of().rawTrue()));
        }
        if (queryMap.justOne()) {
            return ReturnData.ok(this.findOne(queryMap));
        }
        return ReturnData.ok(this.find(queryMap, PageBounds.of(pageBounds, queryMap)));
    }

}
