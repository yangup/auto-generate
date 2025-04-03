package com.pay.common.db.systemrole;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 系统角色表
 * SystemRole
 *
 * @author yangpu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SystemRoleData extends SystemRoleEntity {

    // todo : field


    /**
     * static method
     **/
    public static SystemRoleData of() {
        return SystemRoleData.builder().build();
    }

}
