package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizUserInvitations;
import org.dromara.biz.domain.bo.BizUserInvitationsBo;
import org.dromara.biz.domain.vo.BizUserInvitationsVo;
import org.dromara.biz.mapper.BizUserInvitationsMapper;
import org.dromara.biz.service.IBizUserInvitationsService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户邀请记录Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@RequiredArgsConstructor
@Service
public class BizUserInvitationsServiceImpl extends BaseImpl<BizUserInvitations,BizUserInvitationsVo> implements IBizUserInvitationsService {

    private final BizUserInvitationsMapper baseMapper;

    /**
     * 【核心新增】獲取用戶的邀請統計數據
     *
     * @param inviterId 邀請人的用戶ID
     * @return 包含 totalInvites 和 monthlyInvites 的 Map
     */
    @Override
    public Map<String, Long> getInvitationSummary(Long inviterId) {
        // 1. 統計總邀請數量
        long totalInvites = baseMapper.selectCount(
            new LambdaQueryWrapper<BizUserInvitations>()
                .eq(BizUserInvitations::getInviterId, inviterId)
        );

        // 2. 統計本月邀請數量
        // 獲取當前月份的第一天
        LocalDate startOfMonth = YearMonth.now().atDay(1);
        // 獲取下個月的第一天
        LocalDate startOfNextMonth = startOfMonth.plusMonths(1);

        long monthlyInvites = baseMapper.selectCount(
            new LambdaQueryWrapper<BizUserInvitations>()
                .eq(BizUserInvitations::getInviterId, inviterId)
                .ge(BizUserInvitations::getCreateTime, startOfMonth)
                .lt(BizUserInvitations::getCreateTime, startOfNextMonth)
        );

        // 3. 組裝返回結果
        Map<String, Long> summary = new HashMap<>();
        summary.put("totalInvites", totalInvites);
        summary.put("monthlyInvites", monthlyInvites);
        return summary;
    }

    /**
     * 查询用户邀请记录
     *
     * @param invitationId 主键
     * @return 用户邀请记录
     */
    @Override
    public BizUserInvitationsVo queryById(Long invitationId){
        return baseMapper.selectVoById(invitationId);
    }

    /**
     * 分页查询用户邀请记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户邀请记录分页列表
     */
    @Override
    public TableDataInfo<BizUserInvitationsVo> queryPageList(BizUserInvitationsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizUserInvitations> lqw = buildQueryWrapper(bo);
        Page<BizUserInvitationsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户邀请记录列表
     *
     * @param bo 查询条件
     * @return 用户邀请记录列表
     */
    @Override
    public List<BizUserInvitationsVo> queryList(BizUserInvitationsBo bo) {
        LambdaQueryWrapper<BizUserInvitations> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizUserInvitations> buildQueryWrapper(BizUserInvitationsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizUserInvitations> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizUserInvitations::getInvitationId);
        lqw.eq(bo.getInviterId() != null, BizUserInvitations::getInviterId, bo.getInviterId());
        lqw.eq(bo.getInviteeId() != null, BizUserInvitations::getInviteeId, bo.getInviteeId());
        lqw.eq(StringUtils.isNotBlank(bo.getInvitationCodeUsed()), BizUserInvitations::getInvitationCodeUsed, bo.getInvitationCodeUsed());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizUserInvitations::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增用户邀请记录
     *
     * @param bo 用户邀请记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizUserInvitationsBo bo) {
        BizUserInvitations add = MapstructUtils.convert(bo, BizUserInvitations.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setInvitationId(add.getInvitationId());
        }
        return flag;
    }

    /**
     * 修改用户邀请记录
     *
     * @param bo 用户邀请记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizUserInvitationsBo bo) {
        BizUserInvitations update = MapstructUtils.convert(bo, BizUserInvitations.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizUserInvitations entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户邀请记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 执行列表查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public List<BizUserInvitationsVo> queryList(LambdaQueryWrapper<BizUserInvitations> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizUserInvitationsVo queryOne(LambdaQueryWrapper<BizUserInvitations> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizUserInvitationsBo bo) {
        BizUserInvitations update = MapstructUtils.convert(bo, BizUserInvitations.class);
        return baseMapper.saveOrUpdate(update);
    }
}
