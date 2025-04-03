package com.pay.common.db.systemrole;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * 系统角色表
 * SystemRole
 *
 * @author yangpu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@TableName("t_system_role")
public class SystemRoleEntity {

    // TODO: id
    @TableId
    public String id;
    // TODO: 角色名字
    public String roleName;
    // TODO: 角色状态
    public String status;
    // TODO: 角色描述
    public String description;
    // TODO: 产生时间
    public LocalDateTime createTime;
    // TODO: 更新时间
    public LocalDateTime updateTime;

    /**
     * static method
     **/
    public static SystemRoleEntity of() {
        return SystemRoleEntity.builder().build();
    }

}
