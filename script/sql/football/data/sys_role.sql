create table sys_role
(
    role_id             bigint                       not null comment '角色ID'
        primary key,
    tenant_id           varchar(20) default '000000' null comment '租户编号',
    role_name           varchar(30)                  not null comment '角色名称',
    role_key            varchar(100)                 not null comment '角色权限字符串',
    role_sort           int                          not null comment '显示顺序',
    data_scope          char        default '1'      null comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    menu_check_strictly tinyint(1)  default 1        null comment '菜单树选择项是否关联显示',
    dept_check_strictly tinyint(1)  default 1        null comment '部门树选择项是否关联显示',
    status              char                         not null comment '角色状态（0正常 1停用）',
    del_flag            char        default '0'      null comment '删除标志（0代表存在 1代表删除）',
    create_dept         bigint                       null comment '创建部门',
    create_by           bigint                       null comment '创建者',
    create_time         datetime                     null comment '创建时间',
    update_by           bigint                       null comment '更新者',
    update_time         datetime                     null comment '更新时间',
    remark              varchar(500)                 null comment '备注'
)
    comment '角色信息表';

INSERT INTO football.sys_role (role_id, tenant_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1, '000000', '超级管理员', 'superadmin', 1, '1', 1, 1, '0', '0', 103, 1, '2025-07-24 03:25:30', null, null, '超级管理员');
INSERT INTO football.sys_role (role_id, tenant_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (3, '000000', '本部门及以下', 'test1', 3, '4', 1, 1, '0', '0', 103, 1, '2025-07-24 03:25:30', null, null, '');
INSERT INTO football.sys_role (role_id, tenant_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (4, '000000', '仅本人', 'test2', 4, '5', 1, 1, '0', '0', 103, 1, '2025-07-24 03:25:30', null, null, '');
INSERT INTO football.sys_role (role_id, tenant_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_dept, create_by, create_time, update_by, update_time, remark) VALUES (1954653947198283778, '000000', 'APP', 'app', 1, '1', 0, 1, '0', '0', 103, 1, '2025-08-11 05:19:51', 1, '2025-08-11 05:19:51', '前端APP用户');
