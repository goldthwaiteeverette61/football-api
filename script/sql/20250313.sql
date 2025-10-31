-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1900012834676400130, '商品货源', '1894580984357167105', '1', 'productsSource', 'biz/productsSource/index', 1, 0, 'C', '0', '0', 'biz:productsSource:list', '#', 103, 1, sysdate(), null, null, '商品货源菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1900012834676400131, '商品货源查询', 1900012834676400130, '1',  '#', '', 1, 0, 'F', '0', '0', 'biz:productsSource:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1900012834676400132, '商品货源新增', 1900012834676400130, '2',  '#', '', 1, 0, 'F', '0', '0', 'biz:productsSource:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1900012834676400133, '商品货源修改', 1900012834676400130, '3',  '#', '', 1, 0, 'F', '0', '0', 'biz:productsSource:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1900012834676400134, '商品货源删除', 1900012834676400130, '4',  '#', '', 1, 0, 'F', '0', '0', 'biz:productsSource:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1900012834676400135, '商品货源导出', 1900012834676400130, '5',  '#', '', 1, 0, 'F', '0', '0', 'biz:productsSource:export',       '#', 103, 1, sysdate(), null, null, '');
