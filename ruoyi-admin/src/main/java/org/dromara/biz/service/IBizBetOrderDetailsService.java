package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizBetOrderDetails;
import org.dromara.biz.domain.bo.BizBetOrderDetailsBo;
import org.dromara.biz.domain.vo.BizBetOrderDetailsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 投注訂單詳情Service接口
 *
 * @author Lion Li
 * @date 2025-10-11
 */
public interface IBizBetOrderDetailsService {




    LambdaQueryWrapper<BizBetOrderDetails> getLqw();

    /**
     * 查询投注訂單詳情
     *
     * @param detailId 主键
     * @return 投注訂單詳情
     */
    BizBetOrderDetailsVo queryById(Long detailId);

    /**
     * 分页查询投注訂單詳情列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 投注訂單詳情分页列表
     */
    TableDataInfo<BizBetOrderDetailsVo> queryPageList(BizBetOrderDetailsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的投注訂單詳情列表
     *
     * @param bo 查询条件
     * @return 投注訂單詳情列表
     */
    List<BizBetOrderDetailsVo> queryListF1(BizBetOrderDetailsBo bo);

    /**
     * 查询符合条件的投注訂單詳情列表
     *
     * @param bo 查询条件
     * @return 投注訂單詳情列表
     */
    List<BizBetOrderDetailsVo> queryList(BizBetOrderDetailsBo bo);

    /**
     * 新增投注訂單詳情
     *
     * @param bo 投注訂單詳情
     * @return 是否新增成功
     */
    Boolean insertByBo(BizBetOrderDetailsBo bo);

    /**
     * 修改投注訂單詳情
     *
     * @param bo 投注訂單詳情
     * @return 是否修改成功
     */
    Boolean updateByBo(BizBetOrderDetailsBo bo);

    /**
     * 校验并批量删除投注訂單詳情信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizBetOrderDetailsVo> queryList(LambdaQueryWrapper<BizBetOrderDetails> lqw);

    BizBetOrderDetailsVo queryOne(LambdaQueryWrapper<BizBetOrderDetails> lqw);

    /**
     * 新增或修改
     *
     * @param bo 投注訂單詳情
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizBetOrderDetailsBo bo);
}
