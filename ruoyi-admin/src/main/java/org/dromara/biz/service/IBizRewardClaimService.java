package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizRewardClaim;
import org.dromara.biz.domain.vo.BizRewardClaimVo;
import org.dromara.biz.domain.bo.BizRewardClaimBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 理赔申请Service接口
 *
 * @author Lion Li
 * @date 2025-08-18
 */
public interface IBizRewardClaimService {

    /**
     * 批准理赔申请
     *
     * @param id 申请ID
     */
    void approveClaim(Long id);

    /**
     * 拒绝理赔申请
     *
     * @param id 申请ID
     */
    void rejectClaim(Long id);

    /**
     * 查询理赔申请
     *
     * @param id 主键
     * @return 理赔申请
     */
    BizRewardClaimVo queryById(Long id);

    /**
     * 分页查询理赔申请列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 理赔申请分页列表
     */
    TableDataInfo<BizRewardClaimVo> queryPageList(BizRewardClaimBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的理赔申请列表
     *
     * @param bo 查询条件
     * @return 理赔申请列表
     */
    List<BizRewardClaimVo> queryList(BizRewardClaimBo bo);

    /**
     * 新增理赔申请
     *
     * @param bo 理赔申请
     * @return 是否新增成功
     */
    Boolean insertByBo(BizRewardClaimBo bo);

    /**
     * 修改理赔申请
     *
     * @param bo 理赔申请
     * @return 是否修改成功
     */
    Boolean updateByBo(BizRewardClaimBo bo);

    /**
     * 校验并批量删除理赔申请信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizRewardClaimVo> queryList(LambdaQueryWrapper<BizRewardClaim> lqw);

    BizRewardClaimVo queryOne(LambdaQueryWrapper<BizRewardClaim> lqw);
}
