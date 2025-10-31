package org.dromara.system.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 商品信息对象 biz_products
 *
 * @author Lion Li
 * @date 2025-03-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_products")
public class BizProducts extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 最低价格
     */
    private Long priceMin;

    /**
     * 最高价格
     */
    private Long priceMax;

    /**
     * 折扣价（如果有）
     */
    private Long discountPrice;

    /**
     * 库存数量
     */
    private Long stock;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 卖家名称
     */
    private String seller;

    /**
     * 是否为源头
     */
    private Long origin;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 版本,货源、亚马逊美国、亚马逊日本
     */
    @Version
    private String version;

    /**
     *
     */
    private String sourceLanguage;

    /**
     * zip压缩包位置
     */
    private Long zip;

    /**
     * 目标语言
     */
    private String targetLanguage;

    /**
     * 父id
     */
    private Long parentId;


}
