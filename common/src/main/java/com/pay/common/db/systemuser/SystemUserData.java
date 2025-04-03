package com.pay.common.db.systemuser;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 系统用户表
 * SystemUser
 *
 * @author yangpu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SystemUserData extends SystemUserEntity {

    // todo : field


    /**
     * static method
     **/
    public static SystemUserData of() {
        return SystemUserData.builder().build();
    }

}
