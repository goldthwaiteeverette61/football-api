package org.dromara.system.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.translation.annotation.Translation;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;

/**
 * 商品图片对象 biz_product_images
 *
 * @author Lion Li
 * @date 2025-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("biz_product_images")
public class BizProductImages extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 图片URL
     */
    private Long imageUrl;

    /**
     * 图片排序
     */
    private Long sortOrder;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 目标语言
     */
    private String targetLanguage;

    /**
     *
     */
    private String souceLanguage;

    /**
     * 类型
     */
    private String type;

    /**
     * 图片地址
     */
    private String urlPublic;

    /**
     * 文件名称
     */
    private String fileNam;


}
