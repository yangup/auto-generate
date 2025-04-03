package com.pay.common.db.systemrole;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.*;
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
@AllArgsConstructor
public class SystemRoleDto {

    // todo: id
//    @NotNull(message = "id is empty or incorrect")
//    @JsonProperty("id")
    public String id;

    // todo: 角色名字
    @NotEmpty(message = "roleName is empty or incorrect")
    @Length(min = 0, max = 64, message = "roleName is empty or incorrect")
//    @JsonProperty("role_name")
    public String roleName;

    // todo: 角色状态
    @NotEmpty(message = "status is empty or incorrect")
    @Length(min = 0, max = 16, message = "status is empty or incorrect")
//    @JsonProperty("status")
    public String status;

    // todo: 角色描述
//    @NotEmpty(message = "description is empty or incorrect")
//    @Length(min = 0, max = 64, message = "description is empty or incorrect")
//    @JsonProperty("description")
    public String description;

    // todo: 产生时间
//    @NotNull(message = "createTime is empty or incorrect")
//    @JsonProperty("create_time")
    public LocalDateTime createTime;

    // todo: 更新时间
//    @NotNull(message = "updateTime is empty or incorrect")
//    @JsonProperty("update_time")
    public LocalDateTime updateTime;

    /**
     * static method
     **/
    public static SystemRoleDto of() {
        return SystemRoleDto.builder().build();
    }
}
