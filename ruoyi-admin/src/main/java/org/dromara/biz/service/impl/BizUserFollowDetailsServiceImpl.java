package org.dromara.biz.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizUserFollowDetails;
import org.dromara.biz.domain.bo.BizUserFollowDetailsBo;
import org.dromara.biz.domain.vo.BizUserFollowDetailsVo;
import org.dromara.biz.mapper.BizUserFollowDetailsMapper;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.biz.service.IBizUserFollowDetailsService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 用户跟投详情Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-25
 */
@RequiredArgsConstructor
@Service
public class BizUserFollowDetailsServiceImpl extends BaseImpl<BizUserFollowDetails,BizUserFollowDetailsVo> implements IBizUserFollowDetailsService {

    private final BizUserFollowDetailsMapper baseMapper;

    private final IBizMatchesService bizMatchesService;
//    private final IBizUserFollowsService bizUserFollowsService;

    @Override
    public void insertBatch(List<BizUserFollowDetails> allNewDetailsToInsert) {
        baseMapper.insertBatch(allNewDetailsToInsert);
    }

    @Override
    public void delete(List<Long> followIds) {
        baseMapper.delete(Wrappers.<BizUserFollowDetails>lambdaQuery().in(BizUserFollowDetails::getFollowId, followIds));
    }

    /**
     * 【核心补充】根据跟投ID查询详情列表
     */
    @Override
    public List<BizUserFollowDetailsVo> queryByFollowId(Long followId) {
        if (followId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<BizUserFollowDetails> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizUserFollowDetails::getFollowId, followId);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 【核心补充】根据多个跟投ID查询所有详情
     */
    @Override
    public List<BizUserFollowDetailsVo> queryByFollowIds(List<Long> followIds) {
        if (CollUtil.isEmpty(followIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<BizUserFollowDetails> lqw = Wrappers.lambdaQuery();
        lqw.in(BizUserFollowDetails::getFollowId, followIds);
        return baseMapper.selectVoList(lqw);
    }



    /**
     * 查询用户跟投详情
     *
     * @param followDetailId 主键
     * @return 用户跟投详情
     */
    @Override
    public BizUserFollowDetailsVo queryById(Long followDetailId){
        return baseMapper.selectVoById(followDetailId);
    }

    /**
     * 分页查询用户跟投详情列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户跟投详情分页列表
     */
    @Override
    public TableDataInfo<BizUserFollowDetailsVo> queryPageList(BizUserFollowDetailsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizUserFollowDetails> lqw = buildQueryWrapper(bo);
        Page<BizUserFollowDetailsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户跟投详情列表
     *
     * @param bo 查询条件
     * @return 用户跟投详情列表
     */
    @Override
    public List<BizUserFollowDetailsVo> queryList(BizUserFollowDetailsBo bo) {
        LambdaQueryWrapper<BizUserFollowDetails> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizUserFollowDetails> buildQueryWrapper(BizUserFollowDetailsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizUserFollowDetails> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizUserFollowDetails::getFollowDetailId);
        lqw.eq(bo.getFollowId() != null, BizUserFollowDetails::getFollowId, bo.getFollowId());
        lqw.eq(bo.getPeriodId() != null, BizUserFollowDetails::getPeriodId, bo.getPeriodId());
        lqw.eq(bo.getPeriodDetailsId() != null, BizUserFollowDetails::getPeriodDetailsId, bo.getPeriodDetailsId());
        lqw.eq(bo.getOdds() != null, BizUserFollowDetails::getOdds, bo.getOdds());
        return lqw;
    }

    /**
     * 新增用户跟投详情
     *
     * @param bo 用户跟投详情
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizUserFollowDetailsBo bo) {
        BizUserFollowDetails add = MapstructUtils.convert(bo, BizUserFollowDetails.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setFollowDetailId(add.getFollowDetailId());
        }
        return flag;
    }

    /**
     * 修改用户跟投详情
     *
     * @param bo 用户跟投详情
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizUserFollowDetailsBo bo) {
        BizUserFollowDetails update = MapstructUtils.convert(bo, BizUserFollowDetails.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizUserFollowDetails entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户跟投详情信息
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
    public List<BizUserFollowDetailsVo> queryList(LambdaQueryWrapper<BizUserFollowDetails> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizUserFollowDetailsVo queryOne(LambdaQueryWrapper<BizUserFollowDetails> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizUserFollowDetailsBo bo) {
        BizUserFollowDetails update = MapstructUtils.convert(bo, BizUserFollowDetails.class);
        return baseMapper.saveOrUpdate(update);
    }
}
