package com.pay.api.controller;

import com.pay.common.common.QueryMap;
import com.pay.common.common.ReturnData;
import com.pay.common.common.paginator.PageBounds;
import com.pay.common.db.depositemerchant.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.beans.BeanUtils.*;
import static com.pay.common.constant.Constant.*;
import static com.pay.common.common.ReturnData.*;
import static org.apache.commons.lang3.ObjectUtils.*;

/**
 * 商户提交过来的收款信息
 * DepositeMerchant
 *
 * @author yangpu
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/backend/depositeMerchant/")
public class DepositeMerchantController {

    private @Autowired DepositeMerchantService depositeMerchantService;

    @PostMapping("delete")
    @Operation(summary = "delete 商户提交过来的收款信息")
    public ReturnData<Object> delete(@RequestBody DepositeMerchantEntity entity) throws Exception {
        if (isEmpty(entity.id)) {
            return failNeedParameter("id");
        }
        depositeMerchantService.deleteById(entity.id);
        return ok();
    }

    @PostMapping("addUpdate")
    @Operation(summary = "addUpdate 商户提交过来的收款信息")
    public ReturnData<Object> addUpdate(@RequestBody @Valid DepositeMerchantDto dto) throws Exception {
        DepositeMerchantEntity entity = DepositeMerchantEntity.of();
        copyProperties(dto, entity);
        if (isEmpty(entity.id)) {
            depositeMerchantService.createEntity(entity);
        } else {
            depositeMerchantService.updateEntity(entity);
        }
        return ok();
    }

    // TODO: 以下为查询
    @GetMapping("find")
    @Operation(summary = "find 查询 商户提交过来的收款信息")
    public ReturnData<Object> find(QueryMap queryMap, PageBounds pageBounds) throws Exception {
        return depositeMerchantService.findQuery(queryMap, pageBounds);
    }

}
