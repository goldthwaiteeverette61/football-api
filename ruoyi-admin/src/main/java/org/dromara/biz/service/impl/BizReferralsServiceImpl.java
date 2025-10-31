package org.dromara.biz.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.biz.domain.bo.BizReferralsBo;
import org.dromara.biz.domain.vo.BizReferralsVo;
import org.dromara.biz.domain.BizReferrals;
import org.dromara.biz.mapper.BizReferralsMapper;
import org.dromara.biz.service.IBizReferralsService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 好友推荐关系Service业务层处理
 *
 * @author Lion Li
 * @date 2025-07-24
 */
@RequiredArgsConstructor
@Service
public class BizReferralsServiceImpl implements IBizReferralsService {

    private final BizReferralsMapper baseMapper;

    /**
     * 查询好友推荐关系
     *
     * @param id 主键
     * @return 好友推荐关系
     */
    @Override
    public BizReferralsVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询好友推荐关系列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 好友推荐关系分页列表
     */
    @Override
    public TableDataInfo<BizReferralsVo> queryPageList(BizReferralsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizReferrals> lqw = buildQueryWrapper(bo);
        Page<BizReferralsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的好友推荐关系列表
     *
     * @param bo 查询条件
     * @return 好友推荐关系列表
     */
    @Override
    public List<BizReferralsVo> queryList(BizReferralsBo bo) {
        LambdaQueryWrapper<BizReferrals> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizReferrals> buildQueryWrapper(BizReferralsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizReferrals> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizReferrals::getId);
        lqw.eq(bo.getReferrerUserId() != null, BizReferrals::getReferrerUserId, bo.getReferrerUserId());
        lqw.eq(bo.getReferredUserId() != null, BizReferrals::getReferredUserId, bo.getReferredUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizReferrals::getStatus, bo.getStatus());
        lqw.eq(bo.getBonusAwarded() != null, BizReferrals::getBonusAwarded, bo.getBonusAwarded());
        lqw.eq(bo.getCreatedAt() != null, BizReferrals::getCreatedAt, bo.getCreatedAt());
        return lqw;
    }

    /**
     * 新增好友推荐关系
     *
     * @param bo 好友推荐关系
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizReferralsBo bo) {
        BizReferrals add = MapstructUtils.convert(bo, BizReferrals.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改好友推荐关系
     *
     * @param bo 好友推荐关系
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizReferralsBo bo) {
        BizReferrals update = MapstructUtils.convert(bo, BizReferrals.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizReferrals entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除好友推荐关系信息
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
}
