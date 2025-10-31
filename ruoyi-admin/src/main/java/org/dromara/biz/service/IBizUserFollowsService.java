package org.dromara.biz.service;

import org.dromara.biz.domain.bo.BatchFailBo;
import org.dromara.biz.domain.bo.BizUserFollowsBo;
import org.dromara.biz.domain.bo.UserFollowDetailsSaveBo;
import org.dromara.biz.domain.vo.BizUserFollowsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 用户跟投记录Service接口
 *
 * @author Lion Li
 * @date 2025-07-28
 */
public interface IBizUserFollowsService {

    void batchFail(BatchFailBo bo);

    void saveFollowDetails(UserFollowDetailsSaveBo bo);

    void updateBetOddsDesc(List<Long> followIds, String betOddsDesc);

    void batchConfirmFollows(List<Long> followIds);

    BigDecimal sumAmountByPeriodIdAndUserId(Long periodId, Long userId);

    /**
     * 根据跟投ID查询完整的跟投详情（包含方案和比赛信息）
     * @param followId 跟投ID
     * @return 完整的跟投详情
     */
    BizUserFollowsVo queryDetailsById(Long followId);


    /**
     * 查询用户跟投记录
     *
     * @param followId 主键
     * @return 用户跟投记录
     */
    BizUserFollowsVo queryById(Long followId);

    /**
     * 分页查询用户跟投记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户跟投记录分页列表
     */
    TableDataInfo<BizUserFollowsVo> queryPageList(BizUserFollowsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户跟投记录列表
     *
     * @param bo 查询条件
     * @return 用户跟投记录列表
     */
    List<BizUserFollowsVo> queryList(BizUserFollowsBo bo);

    /**
     * 新增用户跟投记录
     *
     * @param bo 用户跟投记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BizUserFollowsBo bo);

    /**
     * 修改用户跟投记录
     *
     * @param bo 用户跟投记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BizUserFollowsBo bo);

    /**
     * 校验并批量删除用户跟投记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 新增或更新
     *
     * @param bo 对象
     * @return 是否成功
     */
    Boolean insertOrUpdate(BizUserFollowsBo bo);

    TableDataInfo<BizUserFollowsVo> queryMyPageList(BizUserFollowsBo bo, PageQuery pageQuery);

    /**
     * 查询用户某一方案最后一笔跟单
     * @param userId
     * @return
     */
    BizUserFollowsVo queryUserLastFollow(Long userId);

    BigDecimal getMinimumBetAmount();

    /**
     * 跟投前的核心业务校验
     * @param userId 用户ID
     * @param betAmount 本次跟投金额
     * @param periodId 本次跟投的期数ID
     * @return 校验是否通过
     */
    boolean followVerify(Long userId, BigDecimal betAmount, Long periodId);

    /**
     * 查询用户最后一次已结算的跟投记录
     * @param userId 用户ID
     * @return 包含期数状态的跟投记录
     */
    BizUserFollowsVo queryUserLastSettledFollow(Long userId);

}
