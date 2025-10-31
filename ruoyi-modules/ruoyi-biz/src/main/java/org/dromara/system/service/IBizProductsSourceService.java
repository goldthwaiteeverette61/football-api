package org.dromara.system.service;

import org.dromara.system.domain.vo.BizProductsSourceVo;
import org.dromara.system.domain.bo.BizProductsSourceBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 商品货源Service接口
 *
 * @author Lion Li
 * @date 2025-03-13
 */
public interface IBizProductsSourceService {

    /**
     * 查询商品货源
     *
     * @param id 主键
     * @return 商品货源
     */
    BizProductsSourceVo queryById(Long id);

    /**
     * 分页查询商品货源列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品货源分页列表
     */
    TableDataInfo<BizProductsSourceVo> queryPageList(BizProductsSourceBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的商品货源列表
     *
     * @param bo 查询条件
     * @return 商品货源列表
     */
    List<BizProductsSourceVo> queryList(BizProductsSourceBo bo);

    /**
     * 新增商品货源
     *
     * @param bo 商品货源
     * @return 是否新增成功
     */
    Boolean insertByBo(BizProductsSourceBo bo);

    /**
     * 修改商品货源
     *
     * @param bo 商品货源
     * @return 是否修改成功
     */
    Boolean updateByBo(BizProductsSourceBo bo);

    /**
     * 校验并批量删除商品货源信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
