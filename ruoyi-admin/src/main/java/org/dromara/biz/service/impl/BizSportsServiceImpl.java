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
import org.dromara.biz.domain.bo.BizSportsBo;
import org.dromara.biz.domain.vo.BizSportsVo;
import org.dromara.biz.domain.BizSports;
import org.dromara.biz.mapper.BizSportsMapper;
import org.dromara.biz.service.IBizSportsService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 体育项目Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-25
 */
@RequiredArgsConstructor
@Service
public class BizSportsServiceImpl extends BaseImpl<BizSports,BizSportsVo> implements IBizSportsService {

    private final BizSportsMapper baseMapper;

    @PostConstruct
    public void init() {
        super.baseMapperPlus = this.baseMapper;
    }

    /**
     * 查询体育项目
     *
     * @param id 主键
     * @return 体育项目
     */
    @Override
    public BizSportsVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询体育项目列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 体育项目分页列表
     */
    @Override
    public TableDataInfo<BizSportsVo> queryPageList(BizSportsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizSports> lqw = buildQueryWrapper(bo);
        Page<BizSportsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的体育项目列表
     *
     * @param bo 查询条件
     * @return 体育项目列表
     */
    @Override
    public List<BizSportsVo> queryList(BizSportsBo bo) {
        LambdaQueryWrapper<BizSports> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizSports> buildQueryWrapper(BizSportsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizSports> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizSports::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), BizSports::getName, bo.getName());
        return lqw;
    }

    /**
     * 新增体育项目
     *
     * @param bo 体育项目
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizSportsBo bo) {
        BizSports add = MapstructUtils.convert(bo, BizSports.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改体育项目
     *
     * @param bo 体育项目
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizSportsBo bo) {
        BizSports update = MapstructUtils.convert(bo, BizSports.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizSports entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除体育项目信息
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
    public List<BizSportsVo> queryList(LambdaQueryWrapper<BizSports> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizSportsVo queryOne(LambdaQueryWrapper<BizSports> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizSportsBo bo) {
        BizSports update = MapstructUtils.convert(bo, BizSports.class);
        return baseMapper.saveOrUpdate(update);
    }
}
