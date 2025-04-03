package com.pay.common.db.systemuser;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.*;
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
@AllArgsConstructor
public class SystemUserDto {

    // todo: id
//    @NotNull(message = "id is empty or incorrect")
//    @JsonProperty("id")
    public String id;

    // todo: 用户登录code
    @NotEmpty(message = "loginName is empty or incorrect")
    @Length(min = 0, max = 64, message = "loginName is empty or incorrect")
//    @JsonProperty("login_name")
    public String loginName;

    // todo: 用户登录全名
    @NotEmpty(message = "fullName is empty or incorrect")
    @Length(min = 0, max = 64, message = "fullName is empty or incorrect")
//    @JsonProperty("full_name")
    public String fullName;

    // todo: 密码
    @NotEmpty(message = "password is empty or incorrect")
    @Length(min = 0, max = 256, message = "password is empty or incorrect")
//    @JsonProperty("password")
    public String password;

    // todo: 类型
    @NotEmpty(message = "type is empty or incorrect")
    @Length(min = 0, max = 16, message = "type is empty or incorrect")
//    @JsonProperty("type")
    public String type;

    // todo: 状态
    @NotEmpty(message = "status is empty or incorrect")
    @Length(min = 0, max = 16, message = "status is empty or incorrect")
//    @JsonProperty("status")
    public String status;

    // todo: 登录失败的次数
    @Min(value = 0L, message = "failedLoginCount is empty or incorrect")
    @Max(value = 99999L, message = "failedLoginCount is empty or incorrect")
//    @JsonProperty("failed_login_count")
    public Integer failedLoginCount;

    // todo: 最后一次的登录失败,或者成功的时间
    @NotNull(message = "lastTime is empty or incorrect")
//    @JsonProperty("last_time")
    public LocalDateTime lastTime;

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
    public static SystemUserDto of() {
        return SystemUserDto.builder().build();
    }
}
