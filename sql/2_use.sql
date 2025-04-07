DROP TABLE IF EXISTS tb_merchant_info;

CREATE TABLE `tb_merchant_info`
(
    `id`            varchar(32) NOT NULL COMMENT '主键ID',
    `merchant_info` varchar(64) NOT NULL COMMENT '商户信息',
    `access_key`    varchar(64) NOT NULL COMMENT 'access_key.filter',
    `secret_key`    varchar(64) NOT NULL COMMENT 'secret_key',
    `status`        varchar(32) NOT NULL COMMENT '状态;ON:启用,OFF:禁用',
    `create_time`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time`   timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_access_key` (`access_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '商户信息';


DROP TABLE IF EXISTS tb_deposite_bank_card_setting;

CREATE TABLE `tb_deposite_bank_card_setting`
(
    `id`             varchar(32)    NOT NULL COMMENT '主键ID',
    `bank_name`      varchar(64)    NOT NULL COMMENT '银行名称',
    `bank_code`      varchar(32)    NOT NULL COMMENT '银行卡开户行编码（例如：KTB等）',
    `name`           varchar(64)    NOT NULL COMMENT '收款人姓名',
    `phone`          varchar(64)    NOT NULL COMMENT '收款人phone',
    `card_no`        varchar(64)    NOT NULL COMMENT '银行卡号',
    `system_user_id` varchar(32)    NOT NULL COMMENT '系统用户ID',
    `quota`          decimal(15, 2) NOT NULL COMMENT '额度',
    `status`         varchar(32)    NOT NULL COMMENT '银行卡状态;CREATED:新产生,IN_USE:使用中,CLOSED:关闭,废弃,不再使用',
    `create_time`    timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time`    timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '收款银行卡信息;system_user_id:t_system_user.id';



DROP TABLE IF EXISTS tb_deposite_merchant;

CREATE TABLE `tb_deposite_merchant`
(
    `id`                         varchar(32)    NOT NULL COMMENT '主键ID',
    `merchant_id`                varchar(32)    NOT NULL COMMENT '商户ID',
    `transaction_id`             varchar(64)    NOT NULL COMMENT '商户订单号',
    `method`                     varchar(64)    NOT NULL COMMENT '支付方式;QR_PAY:二维码扫码,BANK_TRANSFER:网银转账',
    `amount`                     decimal(15, 2) NOT NULL COMMENT '金额',
    `bill_amount`                decimal(15, 2) COMMENT '收款金额',
    `bank_code`                  varchar(32) COMMENT '银行卡开户行编码（例如：KTB等）',
    `card_no`                    varchar(64) COMMENT '银行卡号',

    `deposite_bank_code`         varchar(32) COMMENT '收款卡,银行卡开户行编码（例如：KTB等）',
    `deposite_card_no`           varchar(64) COMMENT '收款卡,银行卡号',
    `deposite_name`              varchar(64) COMMENT '收款卡,银行卡号',
    `deposite_phone`             varchar(64) COMMENT '收款卡, phone',

    `expire_time`                timestamp      NOT NULL COMMENT '失效时间,初次时间',
    `expire_time_real`           timestamp COMMENT '失效时间,以点击链接的时间为准',

    `name`                       varchar(64) COMMENT '名字',
    `phone`                      varchar(64) COMMENT '电话号码',
    `description`                varchar(128) COMMENT '额外字段',
    `qr_code`                    varchar(128) COMMENT '收款二维码,method为qrPay时返回',

    `status`                     varchar(32)    NOT NULL COMMENT '收款状态;CREATED:新产生,IN_USE:使用中,DONE:收款完成,DEPRECATED:废弃',
    `status_callback`            varchar(32) COMMENT '回调状态;',
    `transaction_time`           timestamp COMMENT '交易时间',
    `channel_fee`                decimal(15, 2) COMMENT '渠道费',
    `deposite_bank_statement_id` varchar(32) COMMENT '代收款银行流水ID',

    `account_holder`             varchar(128) COMMENT '入账人',
    `callback_url`               varchar(128) COMMENT 'callback url',

    `create_time`                timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time`                timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_id_transaction_id` (`merchant_id`, `transaction_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '商户提交过来的收款信息;merchant_id:t_merchant_info.id';



DROP TABLE IF EXISTS tb_deposite_bank_statement;

CREATE TABLE `tb_deposite_bank_statement`
(
    `id`                 varchar(32)    NOT NULL COMMENT '主键ID',
    `deposite_bank_code` varchar(32)    NOT NULL COMMENT '收款卡,银行卡开户行编码（例如：KTB等）',
    `deposite_card_no`   varchar(64)    NOT NULL COMMENT '收款卡,银行卡号',

    `bill_amount`        decimal(15, 2) NOT NULL COMMENT '收款金额',
    `transaction_time`   timestamp COMMENT '交易时间',
    `card_no_suffix`     varchar(64) COMMENT '银行卡号,后缀',

    `account_holder`     varchar(128) COMMENT '入账人',
    `info_url`           varchar(128)   NOT NULL COMMENT '图片 url',

    `status`             varchar(32)    NOT NULL COMMENT '状态;CREATED:新产生,DONE:收款完成,DEPRECATED:废弃',

    `create_time`        timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time`        timestamp      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '代收款银行流水;account_holder:t_system_user.id';



DROP TABLE IF EXISTS tb_ocr_info;

CREATE TABLE `tb_ocr_info`
(
    `id`          varchar(32) NOT NULL COMMENT '主键ID',
    `raw_text`    text COMMENT '原文',
    `after_text`  text COMMENT '解析后的文本',

    `create_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = 'ocr info';






