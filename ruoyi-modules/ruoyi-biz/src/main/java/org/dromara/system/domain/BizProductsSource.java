package org.dromara.system.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 商品货源对象 biz_products_source
 *
 * @author Lion Li
 * @date 2025-03-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_products_source")
public class BizProductsSource extends TenantEntity {

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
     * 销量
     */
    private Long sales;

    /**
     * 商品评分
     */
    private Long rating;

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
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 运费
     */
    private Long shippingFee;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;

    /**
     * 
     */
    private String sourceLanguage;

    /**
     * zip压缩包位置
     */
    private String zip;

    /**
     * 目标语言
     */
    private String targetLanguage;

    /**
     * 翻译完成：0，未完成；1，已完成。
     */
    private Long translate;


}
