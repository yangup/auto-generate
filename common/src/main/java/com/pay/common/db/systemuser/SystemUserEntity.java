package com.pay.common.db.systemuser;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 系统用户表
 * SystemUser
 *
 * @author yangpu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@TableName("t_system_user")
public class SystemUserEntity {

    // TODO: id
    @TableId
    public String id;
    // TODO: 用户登录code
    public String loginName;
    // TODO: 用户登录全名
    public String fullName;
    // TODO: 密码
    public String password;
    // TODO: 类型
    public String type;
    // TODO: 状态
    public String status;
    // TODO: 登录失败的次数
    public Integer failedLoginCount;
    // TODO: 最后一次的登录失败,或者成功的时间
    public LocalDateTime lastTime;
    // TODO: 产生时间
    public LocalDateTime createTime;
    // TODO: 更新时间
    public LocalDateTime updateTime;

    /**
     * static method
     **/
    public static SystemUserEntity of() {
        return SystemUserEntity.builder().build();
    }

}
