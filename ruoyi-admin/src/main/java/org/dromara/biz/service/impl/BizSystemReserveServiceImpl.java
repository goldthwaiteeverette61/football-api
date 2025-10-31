package org.dromara.biz.service.impl;


import jakarta.annotation.PostConstruct;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.biz.domain.bo.BizSystemReserveBo;
import org.dromara.biz.domain.vo.BizSystemReserveVo;
import org.dromara.biz.domain.BizSystemReserve;
import org.dromara.biz.mapper.BizSystemReserveMapper;
import org.dromara.biz.service.IBizSystemReserveService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 系统储备金明细Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-09
 */
@RequiredArgsConstructor
@Service
public class BizSystemReserveServiceImpl extends BaseImpl<BizSystemReserve,BizSystemReserveVo> implements IBizSystemReserveService {

    private final BizSystemReserveMapper baseMapper;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    /**
     * 查询系统储备金明细
     *
     * @param reserveId 主键
     * @return 系统储备金明细
     */
    @Override
    public BizSystemReserveVo queryById(Long reserveId){
        return baseMapper.selectVoById(reserveId);
    }

    /**
     * 分页查询系统储备金明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 系统储备金明细分页列表
     */
    @Override
    public TableDataInfo<BizSystemReserveVo> queryPageList(BizSystemReserveBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizSystemReserve> lqw = buildQueryWrapper(bo);
        Page<BizSystemReserveVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的系统储备金明细列表
     *
     * @param bo 查询条件
     * @return 系统储备金明细列表
     */
    @Override
    public List<BizSystemReserveVo> queryList(BizSystemReserveBo bo) {
        LambdaQueryWrapper<BizSystemReserve> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizSystemReserve> buildQueryWrapper(BizSystemReserveBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizSystemReserve> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizSystemReserve::getReserveId);
        lqw.eq(StringUtils.isNotBlank(bo.getSourceType()), BizSystemReserve::getSourceType, bo.getSourceType());
        lqw.eq(StringUtils.isNotBlank(bo.getSourceId()), BizSystemReserve::getSourceId, bo.getSourceId());
        lqw.eq(bo.getUserId() != null, BizSystemReserve::getUserId, bo.getUserId());
        lqw.eq(bo.getAmount() != null, BizSystemReserve::getAmount, bo.getAmount());
        lqw.eq(bo.getCommissionRate() != null, BizSystemReserve::getCommissionRate, bo.getCommissionRate());
        lqw.eq(bo.getOriginalPayout() != null, BizSystemReserve::getOriginalPayout, bo.getOriginalPayout());
        return lqw;
    }

    /**
     * 新增系统储备金明细
     *
     * @param bo 系统储备金明细
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizSystemReserveBo bo) {
        BizSystemReserve add = MapstructUtils.convert(bo, BizSystemReserve.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setReserveId(add.getReserveId());
        }
        return flag;
    }

    /**
     * 修改系统储备金明细
     *
     * @param bo 系统储备金明细
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizSystemReserveBo bo) {
        BizSystemReserve update = MapstructUtils.convert(bo, BizSystemReserve.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizSystemReserve entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除系统储备金明细信息
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
    * 新增或更新
    *
    * @param bo 对象
    * @return 是否成功
    */
    @Override
    public Boolean insertOrUpdate(BizSystemReserveBo bo) {
        if(bo.getReserveId() != null && bo.getReserveId() > 0){
            return this.updateByBo(bo);
        }

        LambdaQueryWrapper<BizSystemReserve> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizSystemReserve::getReserveId, bo.getReserveId());
        BizSystemReserveVo vo = baseMapper.selectVoOne(lqw);

        if(vo != null){
            bo.setReserveId(vo.getReserveId());
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
    public List<BizSystemReserveVo> queryList(LambdaQueryWrapper<BizSystemReserve> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizSystemReserveVo queryOne(LambdaQueryWrapper<BizSystemReserve> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }
}
