package com.pay.common.db.depositemerchant;

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
 * 商户提交过来的收款信息
 * DepositeMerchant
 *
 * @author yangpu
 */
@Slf4j
@Service
public class DepositeMerchantService extends BaseService {

    private @Autowired DepositeMerchantMapper depositeMerchantMapper;
    private @Autowired RedissonUtil redissonUtil;

    @Transactional
    public String createEntity(DepositeMerchantEntity entity) {
        entity.id = redissonUtil.generateId(DepositeMerchantEntity.class, "TDM");
        depositeMerchantMapper.insert(entity);
        return entity.id;
    }

    @Transactional
    public int deleteById(String id) {
        return depositeMerchantMapper.deleteById(id);
    }

    @Transactional
    public int updateEntity(DepositeMerchantEntity entity) {
        return depositeMerchantMapper.updateById(entity);
    }

    // TODO: 以下为查询
    public DepositeMerchantData findOne(QueryMap queryMap) {
        return find(queryMap, null).first();
    }

    public PageList<DepositeMerchantData> find(QueryMap queryMap) {
        return find(queryMap, null);
    }

    public PageList<DepositeMerchantData> find(QueryMap queryMap, PageBounds pageBounds) {
        queryMap = QueryMap.of(queryMap);
        pageBounds = pageBounds == null ? PageBounds.of() : pageBounds;
        LambdaQueryWrapper<DepositeMerchantEntity> wrapper = new LambdaQueryWrapper<>();
        if (isNotEmpty(queryMap.get("id"))) {
            wrapper.eq(DepositeMerchantEntity::getId, queryMap.get("id"));
        }
        wrapper.orderByDesc(DepositeMerchantEntity::getId);
        PageList<DepositeMerchantData> result = null;
        if (pageBounds.isNotPageFind()) {
            List<DepositeMerchantData> dataList = depositeMerchantMapper.selectList(wrapper).stream()
                    .map(this::convertToData)
                    .collect(Collectors.toList());
            result = new PageList<>(dataList);
        } else {
            Page<DepositeMerchantEntity> pageData = depositeMerchantMapper.selectPage(
                    Page.of(pageBounds.getPage(), pageBounds.getSize()), wrapper);
            List<DepositeMerchantData> dataList = pageData.getRecords().stream()
                    .map(this::convertToData)
                    .collect(Collectors.toList());
            result = new PageList<>(dataList, pageData.getTotal());
        }
        if (isEmpty(result) || queryMap.raw()) {
            return result;
        }
        return result;
    }

    private DepositeMerchantData convertToData(DepositeMerchantEntity entity) {
        DepositeMerchantData data = new DepositeMerchantData();
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
