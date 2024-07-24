drop table if exists t_demo3;
create table t_demo3
(
    id           varchar(32) primary key,
    mobile       varchar(16) not null COMMENT ' 手机号码',
    type         varchar(32) not null COMMENT '请求类型;CREATED:新产生,DONE:完成,CLOSED:关闭,FAILED:失败,FAILED1:失败1,FAILED2:失败2',
    book_name    varchar(128) COMMENT 'book文件名',
    birthday     date COMMENT '出生日期',
    start_time   datetime COMMENT '开始时间',
    end_time     time COMMENT '结束时间',
    number_count smallint unsigned not null default 0 COMMENT '拥有的数量',
    amount       decimal(15, 5) COMMENT '金额',
    des          longtext COMMENT '说明',
    create_time  timestamp   not null default CURRENT_TIMESTAMP,
    update_time  timestamp   not null default CURRENT_TIMESTAMP,
    index        index_t_demo2_mobile(mobile)
) comment='样例1';

CREATE TABLE `tb_user`
(
    `id`          int unsigned NOT NULL AUTO_INCREMENT,
    `status`      varchar(32) NOT NULL COMMENT '状态;ON:启用,OFF:禁用',
    `create_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time` timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息';

CREATE TABLE `tb_user_apple`
(
    `id`              int unsigned NOT NULL AUTO_INCREMENT,
    `user_id`         int unsigned NOT NULL COMMENT 'user_id:tb_user',
    `user_identifier` varchar(128) NOT NULL COMMENT 'user_identifier.filter',
    `email`           varchar(1024) NULL COMMENT 'email',
    `identity_token`  text         NOT NULL COMMENT 'identity_token',
    `create_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time`     timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uni_tb_user_apple_user_id` (`user_id`),
    UNIQUE KEY `uni_tb_user_apple_user_identifier` (`user_identifier`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='apple id 登录信息';


CREATE TABLE `tb_video_episode`
(
    `id`                  int unsigned NOT NULL AUTO_INCREMENT,
    `video_id`            int unsigned NOT NULL COMMENT 'video_id',
    `video_episode_no`    smallint unsigned NOT NULL COMMENT '编号.filter',
    `video_episode_url`   varchar(1024) NOT NULL COMMENT '视频url',
    `video_episode_type`  varchar(32) NOT NULL COMMENT '类型;FREE:免费视频,PAY:付费视频',
    `video_episode_info`  text NULl COMMENT '视频简介',
    `create_time`         timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time`         timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='视频资源信息,每一集的信息;';


CREATE TABLE `tb_video`
(
    `id`                int unsigned NOT NULL AUTO_INCREMENT,
    `video_title`       varchar(1024) NOT NULL COMMENT '视频title.filter',
    `video_cover_url`   varchar(1024) NULL COMMENT '视频封面title',
    `video_tags`        varchar(1024) NULL COMMENT '视频标签;RECOMMEND:推荐',
    `video_info`        text NULl COMMENT '视频简介',
    `create_time`       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据插入时间',
    `update_time`       timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='视频资源信息;id:tb_video_episode.video_id';

