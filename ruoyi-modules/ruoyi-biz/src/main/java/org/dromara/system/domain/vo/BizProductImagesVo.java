package org.dromara.system.domain.vo;

import org.dromara.common.translation.annotation.Translation;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.common.translation.constant.TransConstant;
import org.dromara.system.domain.BizProductImages;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 商品图片视图对象 biz_product_images
 *
 * @author Lion Li
 * @date 2025-03-07
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizProductImages.class)
public class BizProductImagesVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 图片ID
     */
    @ExcelProperty(value = "图片ID")
    private Long id;

    /**
     * 商品ID
     */
    @ExcelProperty(value = "商品ID")
    private Long productId;

    /**
     * 图片URL
     */
    @ExcelProperty(value = "图片URL")
    private Long imageUrl;

    /**
     * 图片URLUrl
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "imageUrl")
    private String imageUrlUrl;
    /**
     * 图片排序
     */
    @ExcelProperty(value = "图片排序")
    private Long sortOrder;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createdAt;

    /**
     * 目标语言
     */
    @ExcelProperty(value = "目标语言")
    private String targetLanguage;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String souceLanguage;

    /**
     * 类型
     */
    @ExcelProperty(value = "类型")
    private String type;

    /**
     * 图片地址
     */
    @ExcelProperty(value = "图片地址")
    private String urlPublic;

    /**
     * 文件名称
     */
    @ExcelProperty(value = "文件名称")
    private String fileNam;


}
