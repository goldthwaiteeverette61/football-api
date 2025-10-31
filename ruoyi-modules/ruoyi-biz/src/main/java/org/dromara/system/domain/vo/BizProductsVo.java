package org.dromara.system.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.system.domain.BizProducts;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 商品信息视图对象 biz_products
 *
 * @author Lion Li
 * @date 2025-03-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BizProducts.class)
public class BizProductsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @ExcelProperty(value = "商品ID")
    private Long id;

    /**
     * 商品标题
     */
    @ExcelProperty(value = "商品标题")
    private String title;

    /**
     * 商品描述
     */
    @ExcelProperty(value = "商品描述")
    private String description;

    /**
     * 最低价格
     */
    @ExcelProperty(value = "最低价格")
    private Long priceMin;

    /**
     * 最高价格
     */
    @ExcelProperty(value = "最高价格")
    private Long priceMax;

    /**
     * 折扣价（如果有）
     */
    @ExcelProperty(value = "折扣价", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=果有")
    private Long discountPrice;

    /**
     * 库存数量
     */
    @ExcelProperty(value = "库存数量")
    private Long stock;

    /**
     * 商品分类
     */
    @ExcelProperty(value = "商品分类")
    private String category;

    /**
     * 卖家名称
     */
    @ExcelProperty(value = "卖家名称")
    private String seller;

    /**
     * 是否为源头
     */
    @ExcelProperty(value = "是否为源头")
    private Long origin;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createdAt;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updatedAt;

    /**
     *
     */
    @ExcelProperty(value = "")
    private String sourceLanguage;

    /**
     * zip压缩包位置
     */
    @ExcelProperty(value = "zip压缩包位置")
    private Long zip;

    /**
     * 目标语言
     */
    @ExcelProperty(value = "目标语言")
    private String targetLanguage;

    /**
     * 父id
     */
    @ExcelProperty(value = "父id")
    private Long parentId;

    /**
     * 已经生成完成的语言
     */
    private List<String> genedLang;

}
