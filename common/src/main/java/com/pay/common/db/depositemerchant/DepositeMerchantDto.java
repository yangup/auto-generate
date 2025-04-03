package com.pay.common.db.depositemerchant;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.*;
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
@AllArgsConstructor
public class DepositeMerchantDto {

    // todo: 主键ID
//    @NotNull(message = "id is empty or incorrect")
//    @JsonProperty("id")
    public String id;

    // todo: 商户ID
//    @NotNull(message = "merchantId is empty or incorrect")
//    @JsonProperty("merchant_id")
    public String merchantId;

    // todo: 商户订单号
//    @NotNull(message = "transactionId is empty or incorrect")
//    @JsonProperty("transaction_id")
    public String transactionId;

    // todo: 支付方式;QR_PAY:二维码扫码,BANK_TRANSFER:网银转账
    @NotEmpty(message = "method is empty or incorrect")
    @Length(min = 0, max = 64, message = "method is empty or incorrect")
//    @JsonProperty("method")
    public String method;

    // todo: 金额
//    @DecimalMin(value = "0", inclusive = false, message = "amount is empty or incorrect")
//    @DecimalMax(value = ""9999999999999"."99"", inclusive = false, message = "amount is empty or incorrect")
//    @Digits(integer = 13, fraction = 2, message = "amount is empty or incorrect")
    @NotNull(message = "amount is empty or incorrect")
//    @JsonProperty("amount")
    public BigDecimal amount;

    // todo: 收款金额
//    @DecimalMin(value = "0", inclusive = false, message = "billAmount is empty or incorrect")
//    @DecimalMax(value = ""9999999999999"."99"", inclusive = false, message = "billAmount is empty or incorrect")
//    @Digits(integer = 13, fraction = 2, message = "billAmount is empty or incorrect")
//    @NotNull(message = "billAmount is empty or incorrect")
//    @JsonProperty("bill_amount")
    public BigDecimal billAmount;

    // todo: 银行卡开户行编码（例如：KTB等）
//    @NotEmpty(message = "bankCode is empty or incorrect")
//    @Length(min = 0, max = 32, message = "bankCode is empty or incorrect")
//    @JsonProperty("bank_code")
    public String bankCode;

    // todo: 银行卡号
//    @NotEmpty(message = "cardNo is empty or incorrect")
//    @Length(min = 0, max = 64, message = "cardNo is empty or incorrect")
//    @JsonProperty("card_no")
    public String cardNo;

    // todo: 收款卡,银行卡开户行编码（例如：KTB等）
//    @NotEmpty(message = "depositeBankCode is empty or incorrect")
//    @Length(min = 0, max = 32, message = "depositeBankCode is empty or incorrect")
//    @JsonProperty("deposite_bank_code")
    public String depositeBankCode;

    // todo: 收款卡,银行卡号
//    @NotEmpty(message = "depositeCardNo is empty or incorrect")
//    @Length(min = 0, max = 64, message = "depositeCardNo is empty or incorrect")
//    @JsonProperty("deposite_card_no")
    public String depositeCardNo;

    // todo: 收款卡,银行卡号
//    @NotEmpty(message = "depositeName is empty or incorrect")
//    @Length(min = 0, max = 64, message = "depositeName is empty or incorrect")
//    @JsonProperty("deposite_name")
    public String depositeName;

    // todo: 收款卡, phone
//    @NotEmpty(message = "depositePhone is empty or incorrect")
//    @Length(min = 0, max = 64, message = "depositePhone is empty or incorrect")
//    @JsonProperty("deposite_phone")
    public String depositePhone;

    // todo: 失效时间,初次时间
    @NotNull(message = "expireTime is empty or incorrect")
//    @JsonProperty("expire_time")
    public LocalDateTime expireTime;

    // todo: 失效时间,以点击链接的时间为准
//    @NotNull(message = "expireTimeReal is empty or incorrect")
//    @JsonProperty("expire_time_real")
    public LocalDateTime expireTimeReal;

    // todo: 名字
//    @NotEmpty(message = "name is empty or incorrect")
//    @Length(min = 0, max = 64, message = "name is empty or incorrect")
//    @JsonProperty("name")
    public String name;

    // todo: 电话号码
//    @NotEmpty(message = "phone is empty or incorrect")
//    @Length(min = 0, max = 64, message = "phone is empty or incorrect")
//    @JsonProperty("phone")
    public String phone;

    // todo: 额外字段
//    @NotEmpty(message = "description is empty or incorrect")
//    @Length(min = 0, max = 128, message = "description is empty or incorrect")
//    @JsonProperty("description")
    public String description;

    // todo: 收款二维码,method为qrPay时返回
//    @NotEmpty(message = "qrCode is empty or incorrect")
//    @Length(min = 0, max = 128, message = "qrCode is empty or incorrect")
//    @JsonProperty("qr_code")
    public String qrCode;

    // todo: 收款状态;CREATED:新产生,IN_USE:使用中,DONE:收款完成,DEPRECATED:废弃
    @NotEmpty(message = "status is empty or incorrect")
    @Length(min = 0, max = 32, message = "status is empty or incorrect")
//    @JsonProperty("status")
    public String status;

    // todo: 回调状态;
//    @NotEmpty(message = "statusCallback is empty or incorrect")
//    @Length(min = 0, max = 32, message = "statusCallback is empty or incorrect")
//    @JsonProperty("status_callback")
    public String statusCallback;

    // todo: 交易时间
//    @NotNull(message = "transactionTime is empty or incorrect")
//    @JsonProperty("transaction_time")
    public LocalDateTime transactionTime;

    // todo: 渠道费
//    @DecimalMin(value = "0", inclusive = false, message = "channelFee is empty or incorrect")
//    @DecimalMax(value = ""9999999999999"."99"", inclusive = false, message = "channelFee is empty or incorrect")
//    @Digits(integer = 13, fraction = 2, message = "channelFee is empty or incorrect")
//    @NotNull(message = "channelFee is empty or incorrect")
//    @JsonProperty("channel_fee")
    public BigDecimal channelFee;

    // todo: 代收款银行流水ID
//    @NotNull(message = "depositeBankStatementId is empty or incorrect")
//    @JsonProperty("deposite_bank_statement_id")
    public String depositeBankStatementId;

    // todo: 入账人
//    @NotEmpty(message = "accountHolder is empty or incorrect")
//    @Length(min = 0, max = 128, message = "accountHolder is empty or incorrect")
//    @JsonProperty("account_holder")
    public String accountHolder;

    // todo: callback url
//    @NotEmpty(message = "callbackUrl is empty or incorrect")
//    @Length(min = 0, max = 128, message = "callbackUrl is empty or incorrect")
//    @JsonProperty("callback_url")
    public String callbackUrl;

    // todo: 数据插入时间
//    @NotNull(message = "createTime is empty or incorrect")
//    @JsonProperty("create_time")
    public LocalDateTime createTime;

    // todo: 最后更新时间
//    @NotNull(message = "updateTime is empty or incorrect")
//    @JsonProperty("update_time")
    public LocalDateTime updateTime;

    /**
     * static method
     **/
    public static DepositeMerchantDto of() {
        return DepositeMerchantDto.builder().build();
    }
}
