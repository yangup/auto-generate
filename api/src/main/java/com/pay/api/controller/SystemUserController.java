package com.pay.api.controller;

import com.pay.common.common.QueryMap;
import com.pay.common.common.ReturnData;
import com.pay.common.common.paginator.PageBounds;
import com.pay.common.db.systemuser.*;
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
 * 系统用户
 * SystemUser
 *
 * @author yangpu
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/backend/systemUser/")
public class SystemUserController {

    private @Autowired SystemUserService systemUserService;

    @PostMapping("delete")
    @Operation(summary = "delete 系统用户")
    public ReturnData<Object> delete(@RequestBody SystemUserEntity entity) throws Exception {
        if (isEmpty(entity.id)) {
            return failNeedParameter("id");
        }
        systemUserService.deleteById(entity.id);
        return ok();
    }

    @PostMapping("addUpdate")
    @Operation(summary = "addUpdate 系统用户")
    public ReturnData<Object> addUpdate(@RequestBody @Valid SystemUserDto dto) throws Exception {
        SystemUserEntity entity = SystemUserEntity.of();
        copyProperties(dto, entity);
        if (isEmpty(entity.id)) {
            systemUserService.createEntity(entity);
        } else {
            systemUserService.updateEntity(entity);
        }
        return ok();
    }

    // TODO: 以下为查询
    @GetMapping("find")
    @Operation(summary = "find 查询 系统用户")
    public ReturnData<Object> find(QueryMap queryMap, PageBounds pageBounds) throws Exception {
        return systemUserService.findQuery(queryMap, pageBounds);
    }

}
