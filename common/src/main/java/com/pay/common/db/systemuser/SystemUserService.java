package com.pay.common.db.systemuser;

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
 * 系统用户
 * SystemUser
 *
 * @author yangpu
 */
@Slf4j
@Service
public class SystemUserService extends BaseService {

    private @Autowired SystemUserMapper systemUserMapper;
    private @Autowired RedissonUtil redissonUtil;

    @Transactional
    public String createEntity(SystemUserEntity entity) {
        entity.id = redissonUtil.generateId(SystemUserEntity.class, "SU");
        systemUserMapper.insert(entity);
        return entity.id;
    }

    @Transactional
    public int deleteById(String id) {
        return systemUserMapper.deleteById(id);
    }

    @Transactional
    public int updateEntity(SystemUserEntity entity) {
        return systemUserMapper.updateById(entity);
    }

    // TODO: 以下为查询
    public SystemUserData findOne(QueryMap queryMap) {
        return find(queryMap, null).first();
    }

    public PageList<SystemUserData> find(QueryMap queryMap) {
        return find(queryMap, null);
    }

    public PageList<SystemUserData> find(QueryMap queryMap, PageBounds pageBounds) {
        queryMap = QueryMap.of(queryMap);
        pageBounds = pageBounds == null ? PageBounds.of() : pageBounds;
        LambdaQueryWrapper<SystemUserEntity> wrapper = new LambdaQueryWrapper<>();
        if (isNotEmpty(queryMap.get("id"))) {
            wrapper.eq(SystemUserEntity::getId, queryMap.get("id"));
        }
        wrapper.orderByDesc(SystemUserEntity::getId);
        PageList<SystemUserData> result = null;
        if (pageBounds.isNotPageFind()) {
            List<SystemUserData> dataList = systemUserMapper.selectList(wrapper).stream()
                    .map(this::convertToData)
                    .collect(Collectors.toList());
            result = new PageList<>(dataList);
        } else {
            Page<SystemUserEntity> pageData = systemUserMapper.selectPage(
                    Page.of(pageBounds.getPage(), pageBounds.getSize()), wrapper);
            List<SystemUserData> dataList = pageData.getRecords().stream()
                    .map(this::convertToData)
                    .collect(Collectors.toList());
            result = new PageList<>(dataList, pageData.getTotal());
        }
        if (isEmpty(result) || queryMap.raw()) {
            return result;
        }
        return result;
    }

    private SystemUserData convertToData(SystemUserEntity entity) {
        SystemUserData data = new SystemUserData();
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
