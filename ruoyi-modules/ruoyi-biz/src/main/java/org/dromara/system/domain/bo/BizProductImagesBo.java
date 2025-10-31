package org.dromara.system.domain.bo;

import org.dromara.system.domain.BizProductImages;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.dromara.common.translation.annotation.Translation;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.common.translation.constant.TransConstant;

/**
 * 商品图片业务对象 biz_product_images
 *
 * @author Lion Li
 * @date 2025-03-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BizProductImages.class, reverseConvertGenerate = false)
public class BizProductImagesBo extends BaseEntity {

    /**
     * 图片ID
     */
    @NotNull(message = "图片ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long productId;

    /**
     * 图片URL
     */
    @NotBlank(message = "图片URL不能为空", groups = { AddGroup.class, EditGroup.class })
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
