package com.pay.common.db.depositemerchant;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 商户提交过来的收款信息
 * DepositeMerchant
 *
 * @author yangpu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DepositeMerchantData extends DepositeMerchantEntity {

    // todo : field


    /**
     * static method
     **/
    public static DepositeMerchantData of() {
        return DepositeMerchantData.builder().build();
    }

}
