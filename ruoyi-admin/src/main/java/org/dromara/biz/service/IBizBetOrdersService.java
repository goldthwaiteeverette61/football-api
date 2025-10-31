package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizBetOrders;
import org.dromara.biz.domain.bo.BizBetOrdersBo;
import org.dromara.biz.domain.dto.BatchUpdateOddsDto;
import org.dromara.biz.domain.vo.BizBetOrdersVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 使用者投注訂單Service接口
 *
 * @author Lion Li
 * @date 2025-10-11
 */
public interface IBizBetOrdersService {

    /**
     * 【新增】處理超時的草稿訂單的業務入口
     */
    void processExpiredDraftOrders();

    /**
     * 【新增】以事務方式取消單個訂單並退款
     * @param order 待處理的訂單
     */
    void cancelOrderAndRefund(BizBetOrdersVo order);

    void settlePendingOrdersJob();

    /**
     * 【新增】批量更新赔率
     * @param dto 包含待更新详情的DTO
     * @return 是否成功
     */
    Boolean batchUpdateOdds(BatchUpdateOddsDto dto);

    LambdaQueryWrapper<BizBetOrders> getLqw();

    /**
     * 查询使用者投注訂單
     *
     * @param orderId 主键
     * @return 使用者投注訂單
     */
    BizBetOrdersVo queryById(Long orderId);

    /**
     * 分页查询使用者投注訂單列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 使用者投注訂單分页列表
     */
    TableDataInfo<BizBetOrdersVo> queryPageList(BizBetOrdersBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的使用者投注訂單列表
     *
     * @param bo 查询条件
     * @return 使用者投注訂單列表
     */
    List<BizBetOrdersVo> queryList(BizBetOrdersBo bo);

    /**
     * 新增使用者投注訂單
     *
     * @param bo 使用者投注訂單
     * @return 是否新增成功
     */
    Boolean insertByBo(BizBetOrdersBo bo);

    /**
     * 修改使用者投注訂單
     *
     * @param bo 使用者投注訂單
     * @return 是否修改成功
     */
    Boolean updateByBo(BizBetOrdersBo bo);

    /**
     * 校验并批量删除使用者投注訂單信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizBetOrdersVo> queryList(LambdaQueryWrapper<BizBetOrders> lqw);

    BizBetOrdersVo queryOne(LambdaQueryWrapper<BizBetOrders> lqw);

    /**
     * 新增或修改
     *
     * @param bo 使用者投注訂單
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizBetOrdersBo bo);
}
