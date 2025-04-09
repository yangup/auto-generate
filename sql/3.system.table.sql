drop table if exists t_system_user;
create table t_system_user (
  id 			        varchar(32)          primary key,
  login_name  	        varchar(64)          not null COMMENT '用户登录code.filter',
  full_name  	        varchar(64)          not null COMMENT '用户登录全名',
  password  	        varchar(256)         not null COMMENT '密码',
  type			        varchar(16)          not null COMMENT '类型;ADMIN:系统管理员,NORMAL:正常用户',
  status    	        varchar(16)          not null COMMENT '状态;ON:可用,OFF:禁用',
  failed_login_count    smallint unsigned    not null default 0 COMMENT '登录失败的次数',
  last_time             timestamp            not null default CURRENT_TIMESTAMP COMMENT '最后一次的登录失败,或者成功的时间',
  create_time           timestamp            not null default CURRENT_TIMESTAMP,
  update_time           timestamp            not null default CURRENT_TIMESTAMP,
  unique key unique_t_system_user_login_name(login_name)
) comment='系统用户表;id:t_system_user_setting.user_id,just_one';


drop table if exists t_system_user_setting;
create table t_system_user_setting (
  id 			        varchar(32)          primary key,
  user_id  	            varchar(32)          not null COMMENT '用户id',
  set_data      	    text                 COMMENT '配置数据',
  create_time           timestamp            not null default CURRENT_TIMESTAMP,
  update_time           timestamp            not null default CURRENT_TIMESTAMP,
  unique key unique_t_system_user_setting_user_id(user_id)
) comment='系统用户配置表';


-- admin系统的角色表
drop table if exists t_system_role;
create table t_system_role (
  id 			        varchar(32)          primary key,
  role_name  	        varchar(64)          not null COMMENT '角色名字',
  status    	        varchar(16)          not null COMMENT '角色状态;ON:可用,OFF:禁用',
  description	        varchar(64)                   COMMENT '角色描述',
  create_time           timestamp            not null default CURRENT_TIMESTAMP,
  update_time           timestamp            not null default CURRENT_TIMESTAMP,
  unique key unique_t_system_role_role_name(role_name)
) comment='系统角色表';

-- admin系统的 前端权限表
drop table if exists t_system_privilege;
create table t_system_privilege (
  id 			        varchar(32)          primary key,
  icon  	            varchar(64)          not null COMMENT 'icon',
  name  	            varchar(64)          not null COMMENT '显示的名字',
  url  	                varchar(128)         COMMENT '前端url',
  status    	        varchar(16)          not null COMMENT '状态;ON:可用,OFF:禁用',
  type    	            varchar(16)          not null COMMENT '类型;PARENT:父节点,CHILD:子节点',
  parent_id             varchar(32)          COMMENT '父id',
  sort                  bigint unsigned      not null default 0 COMMENT '排序字段',
  create_time           timestamp            not null default CURRENT_TIMESTAMP,
  update_time           timestamp            not null default CURRENT_TIMESTAMP,
  unique key unique_t_system_privilege_url(url)
) comment='系统权限表';

-- admin系统的 后端权限表
drop table if exists t_system_privilege_server;
create table t_system_privilege_server (
  id 			        varchar(32)          primary key,
  privilege_id          varchar(32)          not null COMMENT '权限',
  type    	            varchar(16)          not null COMMENT '后端权限匹配类型,例如,正则表达式,带*匹配,完全匹配',
  url  	                varchar(128)         not null COMMENT '后端url',
  create_time           timestamp            not null default CURRENT_TIMESTAMP,
  update_time           timestamp            not null default CURRENT_TIMESTAMP,
  unique key unique_t_system_privilege_server_url(url)
) comment='系统服务端权限表';

drop table if exists t_system_user_role;
create table t_system_user_role (
  id 			        varchar(32)          primary key,
  user_id   	        varchar(32)          not null COMMENT '用户id',
  role_id   	        varchar(32)          not null COMMENT '角色id',
  create_time           timestamp            not null default CURRENT_TIMESTAMP,
  update_time           timestamp            not null default CURRENT_TIMESTAMP
) comment='系统用户角色关联表';


drop table if exists t_system_role_privilege;
create table t_system_role_privilege (
  id 			        varchar(32)          primary key,
  role_id   	        varchar(32)          not null COMMENT '角色id',
  privilege_id          varchar(32)          not null COMMENT '权限id',
  create_time           timestamp            not null default CURRENT_TIMESTAMP,
  update_time           timestamp            not null default CURRENT_TIMESTAMP
) comment='系统角色权限关联表';



INSERT INTO `t_system_privilege` VALUES ('2025031018432400000019', 'el-icon-setting', '系统管理', null, 'ON', 'PARENT', '0', '1000000000', now(), now());
INSERT INTO `t_system_privilege` VALUES ('2025031019422100000020', 'el-icon-remove', '权限管理', '/system/privilege', 'ON', 'CHILD', '2025031018432400000019', '1000000000', now(), now());
INSERT INTO `t_system_privilege` VALUES ('2025031019425800000021', 'el-icon-zoom-out', '角色管理', '/system/role', 'ON', 'CHILD', '2025031018432400000019', '2000000000', now(), now());
INSERT INTO `t_system_privilege` VALUES ('2025031019431900000022', 'el-icon-mic', '用户管理', '/system/user', 'ON', 'CHILD', '2025031018432400000019', '3000000000', now(), now());
INSERT INTO `t_system_privilege` VALUES ('2025031019440200000023', 'el-icon-info', '业务管理', null, 'ON', 'PARENT', '0', '2000000000', now(), now());
INSERT INTO `t_system_privilege` VALUES ('2025031019444300000024', 'fullscreen', '放款管理', '/table/issue', 'ON', 'CHILD', '2025031019440200000023', '2000000000', now(), now());
INSERT INTO `t_system_privilege` VALUES ('2025031019450800000025', 'el-icon-back', '订单管理', '/table/app', 'ON', 'CHILD', '2025031019440200000023', '1000000000', now(), now());
INSERT INTO `t_system_privilege` VALUES ('2021030411530700000001', 'el-icon-star-off', '权限调整页面', '/system/privilegeAdjust', 'ON', 'CHILD', '2025031018432400000019', '90000000000', now(), now());




INSERT INTO `t_system_role` VALUES ('2025031019400300000001', 'admin', 'ON', 'admin', now(), now());
INSERT INTO `t_system_role` VALUES ('2025031019404800000002', 'adfsd', 'ON', 'adsfdsfa', now(), now());
INSERT INTO `t_system_role` VALUES ('2025031019405500000003', 'roleNameafdds', 'ON', 'roleNameafdds', now(), now());


INSERT INTO `t_system_role_privilege` VALUES ('2025031019532600000001', '2025031019400300000001', '2025031018432400000019', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031019532600000002', '2025031019400300000001', '2025031019422100000020', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031019532600000003', '2025031019400300000001', '2025031019425800000021', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031019532600000004', '2025031019400300000001', '2025031019431900000022', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031019532600000005', '2025031019400300000001', '2025031019444300000024', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031019532600000006', '2025031019400300000001', '2025031019440200000023', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031111295200000007', '2025031019404800000002', '2025031018432400000019', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031111295200000008', '2025031019404800000002', '2025031019422100000020', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031111295200000009', '2025031019404800000002', '2025031019425800000021', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031111295200000010', '2025031019404800000002', '2025031019431900000022', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031111295200000011', '2025031019404800000002', '2025031019440200000023', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031111295200000012', '2025031019404800000002', '2025031019450800000025', now(), now());
INSERT INTO `t_system_role_privilege` VALUES ('2025031111295200000013', '2025031019404800000002', '2025031019444300000024', now(), now());



INSERT INTO `t_system_user` VALUES ('2025031020151000000005', 'admin', '杨普创建的, 用于测试，展示用的', 'kzWUoS01I0EzGYjvGABq9dwuPIEV6yx8', 'ADMIN', 'ON', '0', now(), now(), now());
INSERT INTO `t_system_user` VALUES ('2025031020173200000007', 'admin12', 'admin12', 'kzWUoS01I0EzGYjvGABq9dwuPIEV6yx8', 'ADMIN', 'ON', '0', now(), now(), now());
INSERT INTO `t_system_user` VALUES ('2025031620124900000010', 'yangpu', 'yangpu', 'Yfi8dI9Z1BIruLuf8gOU4yS1e7FQNcFu', 'ADMIN', 'ON', '1', now(), now(), now());
INSERT INTO `t_system_user` VALUES ('2021030316535100000001', 'asfdd', 'asdfasf', 'qYoMXqY5/aBSzAlUZLoW2qtNUipHwf9+', 'ADMIN', 'ON', '0', now(), now(), now());


INSERT INTO `t_system_user_role` VALUES ('2025031110415600000003', '2025031110415500000008', '2025031019405500000003', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031110415600000004', '2025031110415500000008', '2025031019404800000002', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031110415600000005', '2025031110415500000008', '2025031019400300000001', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031110424100000006', '2025031110424100000009', '2025031019405500000003', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031110424100000007', '2025031110424100000009', '2025031019404800000002', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031110424100000008', '2025031110424100000009', '2025031019400300000001', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031111050900000018', '2025031020173200000007', '2025031019400300000001', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031111050900000019', '2025031020173200000007', '2025031019404800000002', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031620124900000026', '2025031620124900000010', '2025031019405500000003', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031620124900000027', '2025031620124900000010', '2025031019404800000002', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2025031620124900000028', '2025031620124900000010', '2025031019400300000001', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2021021818421500000001', '2025031020151000000005', '2025031019400300000001', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2021021818421500000002', '2025031020151000000005', '2025031019405500000003', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2021021818421500000003', '2025031020151000000005', '2025031019404800000002', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2021030316535100000004', '2021030316535100000001', '2025031019405500000003', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2021030316535100000005', '2021030316535100000001', '2025031019404800000002', now(), now());
INSERT INTO `t_system_user_role` VALUES ('2021030316535100000006', '2021030316535100000001', '2025031019400300000001', now(), now());



