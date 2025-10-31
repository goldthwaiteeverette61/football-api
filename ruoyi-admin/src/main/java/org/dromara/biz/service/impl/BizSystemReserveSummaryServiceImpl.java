package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizSystemReserveSummary;
import org.dromara.biz.domain.bo.BizSystemReserveSummaryBo;
import org.dromara.biz.domain.vo.BizSystemReserveSummaryVo;
import org.dromara.biz.mapper.BizSystemReserveSummaryMapper;
import org.dromara.biz.service.IBizSystemReserveSummaryService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 系统储备金汇总Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@RequiredArgsConstructor
@Service
public class BizSystemReserveSummaryServiceImpl extends BaseImpl<BizSystemReserveSummary,BizSystemReserveSummaryVo> implements IBizSystemReserveSummaryService {

    private final BizSystemReserveSummaryMapper baseMapper;

    @Override
    public void deductReserveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("扣除金额必须为正数");
        }
        // 调用Mapper中的原子更新操作
        int rowsAffected = baseMapper.deductReserveAmount(amount);
        if (rowsAffected == 0) {
            // 如果更新失败，说明余额不足
            throw new ServiceException("系统储备金余额不足，理赔失败");
        }
    }

    @Override
    @Transactional
    public void addReserveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return; // 如果金额无效，则不执行任何操作
        }

        // 假设系统中只存在一条总账记录
        List<BizSystemReserveSummaryVo> list = this.queryList(new BizSystemReserveSummaryBo());

        if (list != null && !list.isEmpty()) {
            // 如果记录已存在，则执行原子更新
            BizSystemReserveSummaryVo summary = list.get(0);
            baseMapper.addTotalReserveAmount(amount, summary.getSummaryId());
        } else {
            // 如果记录不存在，则创建新记录
            BizSystemReserveSummaryBo insertBo = new BizSystemReserveSummaryBo();
            insertBo.setTotalReserveAmount(amount);
            this.insertByBo(insertBo);
        }
    }

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    /**
     * 查询系统储备金汇总
     *
     * @param summaryId 主键
     * @return 系统储备金汇总
     */
    @Override
    public BizSystemReserveSummaryVo queryById(Integer summaryId){
        return baseMapper.selectVoById(summaryId);
    }

    /**
     * 分页查询系统储备金汇总列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 系统储备金汇总分页列表
     */
    @Override
    public TableDataInfo<BizSystemReserveSummaryVo> queryPageList(BizSystemReserveSummaryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizSystemReserveSummary> lqw = buildQueryWrapper(bo);
        Page<BizSystemReserveSummaryVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的系统储备金汇总列表
     *
     * @param bo 查询条件
     * @return 系统储备金汇总列表
     */
    @Override
    public List<BizSystemReserveSummaryVo> queryList(BizSystemReserveSummaryBo bo) {
        LambdaQueryWrapper<BizSystemReserveSummary> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizSystemReserveSummary> buildQueryWrapper(BizSystemReserveSummaryBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizSystemReserveSummary> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizSystemReserveSummary::getSummaryId);
        lqw.eq(bo.getTotalReserveAmount() != null, BizSystemReserveSummary::getTotalReserveAmount, bo.getTotalReserveAmount());
        lqw.eq(bo.getLastCalculationTime() != null, BizSystemReserveSummary::getLastCalculationTime, bo.getLastCalculationTime());
        return lqw;
    }

    /**
     * 新增系统储备金汇总
     *
     * @param bo 系统储备金汇总
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizSystemReserveSummaryBo bo) {
        BizSystemReserveSummary add = MapstructUtils.convert(bo, BizSystemReserveSummary.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setSummaryId(add.getSummaryId());
        }
        return flag;
    }

    /**
     * 修改系统储备金汇总
     *
     * @param bo 系统储备金汇总
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizSystemReserveSummaryBo bo) {
        BizSystemReserveSummary update = MapstructUtils.convert(bo, BizSystemReserveSummary.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizSystemReserveSummary entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除系统储备金汇总信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
    * 新增或更新
    *
    * @param bo 对象
    * @return 是否成功
    */
    @Override
    public Boolean insertOrUpdate(BizSystemReserveSummaryBo bo) {
        if(bo.getSummaryId() != null && bo.getSummaryId() > 0){
            return this.updateByBo(bo);
        }

        LambdaQueryWrapper<BizSystemReserveSummary> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizSystemReserveSummary::getSummaryId, bo.getSummaryId());
        BizSystemReserveSummaryVo vo = baseMapper.selectVoOne(lqw);

        if(vo != null){
            bo.setSummaryId(vo.getSummaryId());
            return this.updateByBo(bo);
        }else {
            return this.insertByBo(bo);
        }
    }

    /**
     * 执行列表查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public List<BizSystemReserveSummaryVo> queryList(LambdaQueryWrapper<BizSystemReserveSummary> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizSystemReserveSummaryVo queryOne(LambdaQueryWrapper<BizSystemReserveSummary> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public BizSystemReserveSummaryVo getDefault() {
        BizSystemReserveSummaryBo bo = new BizSystemReserveSummaryBo();
        List<BizSystemReserveSummaryVo> bizSystemReserveSummaryVos = this.queryList(bo);
        if(bizSystemReserveSummaryVos.size() > 0){
            return bizSystemReserveSummaryVos.get(0);
        }
        return null;
    }

    /**
     * 核心修改：实现新的原子扣减方法
     */
    @Override
    @Transactional
    public void subtractReserveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        List<BizSystemReserveSummaryVo> list = this.queryList(new BizSystemReserveSummaryBo());
        if (list != null && !list.isEmpty()) {
            BizSystemReserveSummaryVo summary = list.get(0);
            baseMapper.subtractTotalReserveAmount(amount, summary.getSummaryId());
        }
    }
}
