package org.dromara.system.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.system.domain.bo.BizProductsSourceBo;
import org.dromara.system.domain.vo.BizProductsSourceVo;
import org.dromara.system.domain.BizProductsSource;
import org.dromara.system.mapper.BizProductsSourceMapper;
import org.dromara.system.service.IBizProductsSourceService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 商品货源Service业务层处理
 *
 * @author Lion Li
 * @date 2025-03-13
 */
@RequiredArgsConstructor
@Service
public class BizProductsSourceServiceImpl implements IBizProductsSourceService {

    private final BizProductsSourceMapper baseMapper;

    /**
     * 查询商品货源
     *
     * @param id 主键
     * @return 商品货源
     */
    @Override
    public BizProductsSourceVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询商品货源列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商品货源分页列表
     */
    @Override
    public TableDataInfo<BizProductsSourceVo> queryPageList(BizProductsSourceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizProductsSource> lqw = buildQueryWrapper(bo);
        Page<BizProductsSourceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的商品货源列表
     *
     * @param bo 查询条件
     * @return 商品货源列表
     */
    @Override
    public List<BizProductsSourceVo> queryList(BizProductsSourceBo bo) {
        LambdaQueryWrapper<BizProductsSource> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizProductsSource> buildQueryWrapper(BizProductsSourceBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizProductsSource> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizProductsSource::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getTitle()), BizProductsSource::getTitle, bo.getTitle());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), BizProductsSource::getDescription, bo.getDescription());
        lqw.eq(bo.getPriceMin() != null, BizProductsSource::getPriceMin, bo.getPriceMin());
        lqw.eq(bo.getPriceMax() != null, BizProductsSource::getPriceMax, bo.getPriceMax());
        lqw.eq(bo.getDiscountPrice() != null, BizProductsSource::getDiscountPrice, bo.getDiscountPrice());
        lqw.eq(bo.getStock() != null, BizProductsSource::getStock, bo.getStock());
        lqw.eq(bo.getSales() != null, BizProductsSource::getSales, bo.getSales());
        lqw.eq(bo.getRating() != null, BizProductsSource::getRating, bo.getRating());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), BizProductsSource::getCategory, bo.getCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getSeller()), BizProductsSource::getSeller, bo.getSeller());
        lqw.eq(bo.getOrigin() != null, BizProductsSource::getOrigin, bo.getOrigin());
        lqw.eq(bo.getSellerId() != null, BizProductsSource::getSellerId, bo.getSellerId());
        lqw.eq(bo.getShippingFee() != null, BizProductsSource::getShippingFee, bo.getShippingFee());
        lqw.eq(bo.getCreatedAt() != null, BizProductsSource::getCreatedAt, bo.getCreatedAt());
        lqw.eq(bo.getUpdatedAt() != null, BizProductsSource::getUpdatedAt, bo.getUpdatedAt());
        lqw.eq(StringUtils.isNotBlank(bo.getSourceLanguage()), BizProductsSource::getSourceLanguage, bo.getSourceLanguage());
        lqw.eq(StringUtils.isNotBlank(bo.getZip()), BizProductsSource::getZip, bo.getZip());
        lqw.eq(StringUtils.isNotBlank(bo.getTargetLanguage()), BizProductsSource::getTargetLanguage, bo.getTargetLanguage());
        lqw.eq(bo.getTranslate() != null, BizProductsSource::getTranslate, bo.getTranslate());
        return lqw;
    }

    /**
     * 新增商品货源
     *
     * @param bo 商品货源
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizProductsSourceBo bo) {
        BizProductsSource add = MapstructUtils.convert(bo, BizProductsSource.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商品货源
     *
     * @param bo 商品货源
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizProductsSourceBo bo) {
        BizProductsSource update = MapstructUtils.convert(bo, BizProductsSource.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizProductsSource entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除商品货源信息
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
