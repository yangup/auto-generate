package com.pay.common.db.depositemerchant;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户提交过来的收款信息
 * DepositeMerchant
 *
 * @author yangpu
 */
@Data
@SuperBuilder
@NoArgsConstructor
@TableName("tb_deposite_merchant")
public class DepositeMerchantEntity {

    // TODO: 主键ID
    @TableId
    public String id;
    // TODO: 商户ID
    public String merchantId;
    // TODO: 商户订单号
    public String transactionId;
    // TODO: 支付方式;QR_PAY:二维码扫码,BANK_TRANSFER:网银转账
    public String method;
    // TODO: 金额
    public BigDecimal amount;
    // TODO: 收款金额
    public BigDecimal billAmount;
    // TODO: 银行卡开户行编码（例如：KTB等）
    public String bankCode;
    // TODO: 银行卡号
    public String cardNo;
    // TODO: 收款卡,银行卡开户行编码（例如：KTB等）
    public String depositeBankCode;
    // TODO: 收款卡,银行卡号
    public String depositeCardNo;
    // TODO: 收款卡,银行卡号
    public String depositeName;
    // TODO: 收款卡, phone
    public String depositePhone;
    // TODO: 失效时间,初次时间
    public LocalDateTime expireTime;
    // TODO: 失效时间,以点击链接的时间为准
    public LocalDateTime expireTimeReal;
    // TODO: 名字
    public String name;
    // TODO: 电话号码
    public String phone;
    // TODO: 额外字段
    public String description;
    // TODO: 收款二维码,method为qrPay时返回
    public String qrCode;
    // TODO: 收款状态;CREATED:新产生,IN_USE:使用中,DONE:收款完成,DEPRECATED:废弃
    public String status;
    // TODO: 回调状态;
    public String statusCallback;
    // TODO: 交易时间
    public LocalDateTime transactionTime;
    // TODO: 渠道费
    public BigDecimal channelFee;
    // TODO: 代收款银行流水ID
    public String depositeBankStatementId;
    // TODO: 入账人
    public String accountHolder;
    // TODO: callback url
    public String callbackUrl;
    // TODO: 数据插入时间
    public LocalDateTime createTime;
    // TODO: 最后更新时间
    public LocalDateTime updateTime;

    /**
     * static method
     **/
    public static DepositeMerchantEntity of() {
        return DepositeMerchantEntity.builder().build();
    }

}
