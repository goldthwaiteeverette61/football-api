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
import org.dromara.biz.domain.bo.BizDeviceUpdatesBo;
import org.dromara.biz.domain.vo.BizDeviceUpdatesVo;
import org.dromara.biz.domain.BizDeviceUpdates;
import org.dromara.biz.mapper.BizDeviceUpdatesMapper;
import org.dromara.biz.service.IBizDeviceUpdatesService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备更新记录Service业务层处理
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@RequiredArgsConstructor
@Service
public class BizDeviceUpdatesServiceImpl extends BaseImpl<BizDeviceUpdates,BizDeviceUpdatesVo> implements IBizDeviceUpdatesService {

    private final BizDeviceUpdatesMapper baseMapper;

    /**
     * 查询设备更新记录
     *
     * @param id 主键
     * @return 设备更新记录
     */
    @Override
    public BizDeviceUpdatesVo queryById(Integer id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备更新记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备更新记录分页列表
     */
    @Override
    public TableDataInfo<BizDeviceUpdatesVo> queryPageList(BizDeviceUpdatesBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizDeviceUpdates> lqw = buildQueryWrapper(bo);
        Page<BizDeviceUpdatesVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备更新记录列表
     *
     * @param bo 查询条件
     * @return 设备更新记录列表
     */
    @Override
    public List<BizDeviceUpdatesVo> queryList(BizDeviceUpdatesBo bo) {
        LambdaQueryWrapper<BizDeviceUpdates> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizDeviceUpdates> buildQueryWrapper(BizDeviceUpdatesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizDeviceUpdates> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizDeviceUpdates::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceId()), BizDeviceUpdates::getDeviceId, bo.getDeviceId());
        lqw.eq(StringUtils.isNotBlank(bo.getPlatform()), BizDeviceUpdates::getPlatform, bo.getPlatform());
        lqw.eq(StringUtils.isNotBlank(bo.getFromVersion()), BizDeviceUpdates::getFromVersion, bo.getFromVersion());
        lqw.eq(StringUtils.isNotBlank(bo.getToVersion()), BizDeviceUpdates::getToVersion, bo.getToVersion());
        lqw.eq(StringUtils.isNotBlank(bo.getUpdateType()), BizDeviceUpdates::getUpdateType, bo.getUpdateType());
        lqw.eq(bo.getDownloadStartedAt() != null, BizDeviceUpdates::getDownloadStartedAt, bo.getDownloadStartedAt());
        lqw.eq(bo.getDownloadCompletedAt() != null, BizDeviceUpdates::getDownloadCompletedAt, bo.getDownloadCompletedAt());
        lqw.eq(bo.getInstallStartedAt() != null, BizDeviceUpdates::getInstallStartedAt, bo.getInstallStartedAt());
        lqw.eq(bo.getInstallCompletedAt() != null, BizDeviceUpdates::getInstallCompletedAt, bo.getInstallCompletedAt());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizDeviceUpdates::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getErrorMessage()), BizDeviceUpdates::getErrorMessage, bo.getErrorMessage());
        lqw.eq(bo.getCreatedAt() != null, BizDeviceUpdates::getCreatedAt, bo.getCreatedAt());
        return lqw;
    }

    /**
     * 新增设备更新记录
     *
     * @param bo 设备更新记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizDeviceUpdatesBo bo) {
        BizDeviceUpdates add = MapstructUtils.convert(bo, BizDeviceUpdates.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备更新记录
     *
     * @param bo 设备更新记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizDeviceUpdatesBo bo) {
        BizDeviceUpdates update = MapstructUtils.convert(bo, BizDeviceUpdates.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizDeviceUpdates entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除设备更新记录信息
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
     * 执行列表查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public List<BizDeviceUpdatesVo> queryList(LambdaQueryWrapper<BizDeviceUpdates> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizDeviceUpdatesVo queryOne(LambdaQueryWrapper<BizDeviceUpdates> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizDeviceUpdatesBo bo) {
        BizDeviceUpdates update = MapstructUtils.convert(bo, BizDeviceUpdates.class);
        return baseMapper.saveOrUpdate(update);
    }
}
