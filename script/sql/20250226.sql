CREATE TABLE biz_products (
                          id INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
                          title VARCHAR(255) NOT NULL COMMENT '商品标题',
                          description TEXT COMMENT '商品描述',
                          price_min DECIMAL(10,2) NOT NULL COMMENT '最低价格',
                          price_max DECIMAL(10,2) NOT NULL COMMENT '最高价格',
                          discount_price DECIMAL(10,2) DEFAULT NULL COMMENT '折扣价（如果有）',
                          stock INT NOT NULL COMMENT '库存数量',
                          sales INT DEFAULT 0 COMMENT '销量',
                          rating DECIMAL(3,2) DEFAULT 0.00 COMMENT '商品评分',
                          category VARCHAR(100) COMMENT '商品分类',
                          seller VARCHAR(255) COMMENT '卖家名称',
                          seller_id INT COMMENT '卖家ID',
                          shipping_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品信息表';

INSERT INTO biz_products (title, description, price_min, price_max, discount_price, stock, sales, rating, category, seller, seller_id, shipping_fee)
VALUES (
           '男士外套春秋夹克宽松潮流立领黑马年2025新款夹克男装潮粉绒',
           '时尚潮流夹克，适合春秋季，立领设计，柔软舒适，适合多种场合穿着。',
           44.10,
           53.90,
           NULL,
           13326,
           1,
           NULL,
           '男士外套',
           '迪格尔服饰',
           1001,
           0.00
       );


CREATE TABLE biz_product_images (
                                id INT AUTO_INCREMENT PRIMARY KEY COMMENT '图片ID',
                                product_id INT NOT NULL COMMENT '商品ID',
                                image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
                                sort_order INT DEFAULT 0 COMMENT '图片排序',
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

INSERT INTO biz_product_images (product_id, image_url, sort_order)
VALUES
    (1, 'https://example.com/image1.jpg', 1),
    (1, 'https://example.com/image2.jpg', 2),
    (1, 'https://example.com/image3.jpg',3);



-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894585730792951810, '商品图片', '1894580984357167105', '1', 'productImages', 'biz/productImages/index', 1, 0, 'C', '0', '0', 'biz:productImages:list', '#', 103, 1, sysdate(), null, null, '商品图片菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894585730792951811, '商品图片查询', 1894585730792951810, '1',  '#', '', 1, 0, 'F', '0', '0', 'biz:productImages:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894585730792951812, '商品图片新增', 1894585730792951810, '2',  '#', '', 1, 0, 'F', '0', '0', 'biz:productImages:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894585730792951813, '商品图片修改', 1894585730792951810, '3',  '#', '', 1, 0, 'F', '0', '0', 'biz:productImages:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894585730792951814, '商品图片删除', 1894585730792951810, '4',  '#', '', 1, 0, 'F', '0', '0', 'biz:productImages:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894585730792951815, '商品图片导出', 1894585730792951810, '5',  '#', '', 1, 0, 'F', '0', '0', 'biz:productImages:export',       '#', 103, 1, sysdate(), null, null, '');


-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894586107688914945, '商品信息', '1894580984357167105', '1', 'products', 'biz/products/index', 1, 0, 'C', '0', '0', 'biz:products:list', '#', 103, 1, sysdate(), null, null, '商品信息菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894586107688914946, '商品信息查询', 1894586107688914945, '1',  '#', '', 1, 0, 'F', '0', '0', 'biz:products:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894586107688914947, '商品信息新增', 1894586107688914945, '2',  '#', '', 1, 0, 'F', '0', '0', 'biz:products:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894586107688914948, '商品信息修改', 1894586107688914945, '3',  '#', '', 1, 0, 'F', '0', '0', 'biz:products:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894586107688914949, '商品信息删除', 1894586107688914945, '4',  '#', '', 1, 0, 'F', '0', '0', 'biz:products:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1894586107688914950, '商品信息导出', 1894586107688914945, '5',  '#', '', 1, 0, 'F', '0', '0', 'biz:products:export',       '#', 103, 1, sysdate(), null, null, '');



alter table biz_product_images
    add tenant_id varchar(20) default '000000' null comment '租户编号';

alter table biz_products
    add tenant_id varchar(20) default '000000' null comment '租户编号';



alter table biz_product_images
    add create_dept bigint null comment '创建部门';

alter table biz_products
    add create_dept bigint null comment '创建部门';


alter table biz_product_images
    add create_by varchar(20) default '000000' null comment '租户编号';

alter table biz_products
    add create_by varchar(20) default '000000' null comment '租户编号';


alter table biz_product_images
    add create_time datetime null comment '创建时间';
alter table biz_products
    add create_time datetime null comment '创建时间';

alter table biz_product_images
    add update_by bigint null comment '更新者';

alter table biz_product_images
    add update_time datetime null comment '更新时间';

alter table biz_products
    add update_by bigint null comment '更新者';

alter table biz_products
    add update_time datetime null comment '更新时间';

