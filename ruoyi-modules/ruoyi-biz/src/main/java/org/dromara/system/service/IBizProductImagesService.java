package org.dromara.system.service;

import org.dromara.system.domain.BizProducts;
import org.dromara.system.domain.vo.BizProductImagesVo;
import org.dromara.system.domain.bo.BizProductImagesBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 商品图片Service接口
 *
 * @author Lion Li
 * @date 2025-02-26
 */
public interface IBizProductImagesService {

    /**
     * 查询商品图片
     *
     * @param id 主键
     * @return 商品图片
     */
    BizProductImagesVo queryById(Long id);

    /**
     * 分页查询商品图片列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品图片分页列表
     */
    TableDataInfo<BizProductImagesVo> queryPageList(BizProductImagesBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的商品图片列表
     *
     * @param bo 查询条件
     * @return 商品图片列表
     */
    List<BizProductImagesVo> queryList(BizProductImagesBo bo);

    /**
     * 新增商品图片
     *
     * @param bo 商品图片
     * @return 是否新增成功
     */
    Boolean insertByBo(BizProductImagesBo bo);

    /**
     * 修改商品图片
     *
     * @param bo 商品图片
     * @return 是否修改成功
     */
    Boolean updateByBo(BizProductImagesBo bo);

    /**
     * 校验并批量删除商品图片信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    Boolean copyImagesByProductId(Long sourceProductId, String imageType,BizProducts targetProducts);

}
