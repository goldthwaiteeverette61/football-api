package org.dromara.system.domain.bo;

import org.dromara.system.domain.BizProductsSource;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 商品货源业务对象 biz_products_source
 *
 * @author Lion Li
 * @date 2025-03-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizProductsSource.class, reverseConvertGenerate = false)
public class BizProductsSourceBo extends BaseEntity {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 商品标题
     */
    @NotBlank(message = "商品标题不能为空", groups = { AddGroup.class, EditGroup.class })
    private String title;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 最低价格
     */
    @NotNull(message = "最低价格不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long priceMin;

    /**
     * 最高价格
     */
    @NotNull(message = "最高价格不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long priceMax;

    /**
     * 折扣价（如果有）
     */
    private Long discountPrice;

    /**
     * 库存数量
     */
    @NotNull(message = "库存数量不能为空", groups = { AddGroup.class, EditGroup.class })
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
