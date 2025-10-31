package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizBetOrderDetails;
import org.dromara.biz.domain.bo.BizBetOrderDetailsBo;
import org.dromara.biz.domain.vo.BizBetOrderDetailsVo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.biz.mapper.BizBetOrderDetailsMapper;
import org.dromara.biz.service.IBizBetOrderDetailsService;
import org.dromara.biz.service.IBizMatchesService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 投注訂單詳情Service业务层处理
 *
 * @author Lion Li
 * @date 2025-10-11
 */
@RequiredArgsConstructor
@Service
public class BizBetOrderDetailsServiceImpl extends BaseImpl<BizBetOrderDetails,BizBetOrderDetailsVo> implements IBizBetOrderDetailsService {

    private final BizBetOrderDetailsMapper baseMapper;
    private final IBizMatchesService iBizMatchesService;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    @Override
    public LambdaQueryWrapper<BizBetOrderDetails> getLqw() {
        return super.lqw();
    }

    /**
     * 查询投注訂單詳情
     *
     * @param detailId 主键
     * @return 投注訂單詳情
     */
    @Override
    public BizBetOrderDetailsVo queryById(Long detailId){
        return baseMapper.selectVoById(detailId);
    }

    /**
     * 分页查询投注訂單詳情列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 投注訂單詳情分页列表
     */
    @Override
    public TableDataInfo<BizBetOrderDetailsVo> queryPageList(BizBetOrderDetailsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizBetOrderDetails> lqw = buildQueryWrapper(bo);
        Page<BizBetOrderDetailsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的投注訂單詳情列表
     *
     * @param bo 查询条件
     * @return 投注訂單詳情列表
     */
    @Override
    public List<BizBetOrderDetailsVo> queryListF1(BizBetOrderDetailsBo bo) {
        LambdaQueryWrapper<BizBetOrderDetails> lqw = buildQueryWrapper(bo);
        List<BizBetOrderDetailsVo> list = baseMapper.selectVoList(lqw);
        for (BizBetOrderDetailsVo vo : list) {
            BizMatchesVo bizMatchesVo = iBizMatchesService.queryById(vo.getMatchId());
            if(bizMatchesVo != null) {
                vo.setMatchName(bizMatchesVo.getMatchName());
            }
        }
        return list;
    }

    /**
     * 查询符合条件的投注訂單詳情列表
     *
     * @param bo 查询条件
     * @return 投注訂單詳情列表
     */
    @Override
    public List<BizBetOrderDetailsVo> queryList(BizBetOrderDetailsBo bo) {
        LambdaQueryWrapper<BizBetOrderDetails> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizBetOrderDetails> buildQueryWrapper(BizBetOrderDetailsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizBetOrderDetails> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizBetOrderDetails::getDetailId);
        lqw.eq(bo.getOrderId() != null, BizBetOrderDetails::getOrderId, bo.getOrderId());
        lqw.eq(bo.getMatchId() != null, BizBetOrderDetails::getMatchId, bo.getMatchId());
        lqw.eq(StringUtils.isNotBlank(bo.getPoolCode()), BizBetOrderDetails::getPoolCode, bo.getPoolCode());
        lqw.eq(StringUtils.isNotBlank(bo.getSelection()), BizBetOrderDetails::getSelection, bo.getSelection());
        lqw.eq(bo.getOdds() != null, BizBetOrderDetails::getOdds, bo.getOdds());
        lqw.eq(bo.getIsWinning() != null, BizBetOrderDetails::getIsWinning, bo.getIsWinning());

        // 优先使用 orderIds 列表进行 IN 查询
        if (!CollectionUtils.isEmpty(bo.getOrderIds())) {
            lqw.in(BizBetOrderDetails::getOrderId, bo.getOrderIds());
        }
        return lqw;
    }

    /**
     * 新增投注訂單詳情
     *
     * @param bo 投注訂單詳情
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizBetOrderDetailsBo bo) {
        BizBetOrderDetails add = MapstructUtils.convert(bo, BizBetOrderDetails.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setDetailId(add.getDetailId());
        }
        return flag;
    }

    /**
     * 修改投注訂單詳情
     *
     * @param bo 投注訂單詳情
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizBetOrderDetailsBo bo) {
        BizBetOrderDetails update = MapstructUtils.convert(bo, BizBetOrderDetails.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizBetOrderDetails entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除投注訂單詳情信息
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
    public List<BizBetOrderDetailsVo> queryList(LambdaQueryWrapper<BizBetOrderDetails> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizBetOrderDetailsVo queryOne(LambdaQueryWrapper<BizBetOrderDetails> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizBetOrderDetailsBo bo) {
        BizBetOrderDetails update = MapstructUtils.convert(bo, BizBetOrderDetails.class);
        return baseMapper.saveOrUpdate(update);
    }
}
