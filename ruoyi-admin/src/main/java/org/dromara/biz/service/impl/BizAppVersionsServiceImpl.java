package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizAppVersions;
import org.dromara.biz.domain.bo.BizAppVersionsBo;
import org.dromara.biz.domain.bo.BizDeviceUpdatesBo;
import org.dromara.biz.domain.dto.VersionCheckRequestDto;
import org.dromara.biz.domain.dto.VersionCheckResponseDto;
import org.dromara.biz.domain.vo.BizAppVersionsVo;
import org.dromara.biz.mapper.BizAppVersionsMapper;
import org.dromara.biz.service.IBizAppVersionsService;
import org.dromara.biz.service.IBizDeviceUpdatesService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 应用版本信息Service业务层处理
 *
 * @author Lion Li
 * @date 2025-09-13
 */
@RequiredArgsConstructor
@Service
public class BizAppVersionsServiceImpl extends BaseImpl<BizAppVersions,BizAppVersionsVo> implements IBizAppVersionsService {

    private final BizAppVersionsMapper baseMapper;
    private final IBizDeviceUpdatesService deviceUpdatesService;


    /**
     * 检查应用版本更新
     *
     * @param requestDto 请求参数
     * @return 版本更新信息
     */
    @Override
    public VersionCheckResponseDto checkVersion(VersionCheckRequestDto requestDto) {
        // 1. 查询指定平台最新的、已激活的版本信息
        LambdaQueryWrapper<BizAppVersions> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizAppVersions::getPlatform, requestDto.getPlatform());
        lqw.eq(BizAppVersions::getIsActive, 1);
        lqw.orderByDesc(BizAppVersions::getId); // 通常用ID或创建时间降序来获取最新版本
        lqw.last("LIMIT 1");
        BizAppVersionsVo latestVersion = baseMapper.selectVoOne(lqw);

        // 如果找不到任何版本，或当前版本已是最新，则返回无更新
        if (latestVersion == null) {
            return new VersionCheckResponseDto(false, requestDto.getCurrentVersion());
        }

        // 2. 比较版本号
        int comparisonResult = compareVersions(latestVersion.getVersion(), requestDto.getCurrentVersion());
        if (comparisonResult <= 0) {
            return new VersionCheckResponseDto(false, requestDto.getCurrentVersion());
        }

        // 3. 发现新版本，开始构建响应体
        VersionCheckResponseDto response = new VersionCheckResponseDto();
        response.setHasUpdate(true);
        response.setLatestVersion(latestVersion .getVersion());
        response.setCurrentVersion(requestDto.getCurrentVersion());
        response.setReleaseNotes(latestVersion.getReleaseNotes());
        response.setDownloadUrl(latestVersion.getDownloadUrl());
        response.setMinSupportedVersion(latestVersion.getMinSupportedVersion());
        response.setForceUpdate(latestVersion.getForceUpdate() == 1);
        response.setUpdateDeadline(latestVersion.getUpdateDeadline());
        response.setChecksum(latestVersion.getChecksum());
        response.setUpdateSize(formatFileSize(latestVersion.getFileSize()));

        // 4. 确定更新类型 (force > required > optional)
        String updateType = "optional"; // 默认为可选更新
        if (Boolean.TRUE.equals(response.getForceUpdate())) {
            updateType = "force";
        } else if (latestVersion.getMinSupportedVersion() != null
            && compareVersions(latestVersion.getMinSupportedVersion(), requestDto.getCurrentVersion()) > 0) {
            // 如果当前版本低于最低支持版本
            updateType = "required";
        }
        response.setUpdateType(updateType);

        // 5. 异步记录设备更新检查事件 (可选，但推荐)
        // 注意：为避免影响主流程响应速度，可以将此操作放入异步线程池执行
        recordDeviceUpdateCheck(requestDto, latestVersion, updateType);

        return response;
    }

    /**
     * 记录设备更新检查
     */
    private void recordDeviceUpdateCheck(VersionCheckRequestDto requestDto, BizAppVersionsVo latestVersion, String updateType) {
        try {
            BizDeviceUpdatesBo deviceUpdateBo = new BizDeviceUpdatesBo();
            deviceUpdateBo.setDeviceId(requestDto.getDeviceId());
            deviceUpdateBo.setPlatform(requestDto.getPlatform());
            deviceUpdateBo.setFromVersion(requestDto.getCurrentVersion());
            deviceUpdateBo.setToVersion(latestVersion.getVersion());
            deviceUpdateBo.setUpdateType(updateType);
            deviceUpdateBo.setStatus("pending"); // 初始状态为待定
            deviceUpdatesService.insertByBo(deviceUpdateBo);
        } catch (Exception e) {
            // 记录日志，但不影响主流程
            // log.error("记录设备更新检查失败", e);
            System.err.println("记录设备更新检查失败: " + e.getMessage());
        }
    }


    /**
     * 比较版本号大小.
     * @param version1 版本1
     * @param version2 版本2
     * @return 如果 version1 > version2, 返回 1; 如果 version1 < version2, 返回 -1; 如果相等, 返回 0.
     */
    private int compareVersions(String version1, String version2) {
        if (version1 == null || version2 == null) {
            return 0;
        }
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        int length = Math.max(v1Parts.length, v2Parts.length);

        for (int i = 0; i < length; i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;
            if (v1Part > v2Part) {
                return 1;
            }
            if (v1Part < v2Part) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * 格式化文件大小
     * @param bytes 文件字节数
     * @return 格式化后的字符串 (例如 "15.2MB")
     */
    private String formatFileSize(Long bytes) {
        if (bytes == null || bytes <= 0) {
            return "0B";
        }
        if (bytes < 1024 * 1024) {
            return new DecimalFormat("#.0").format(bytes / 1024.0) + "KB";
        }
        return new DecimalFormat("#.0").format(bytes / (1024.0 * 1024.0)) + "MB";
    }

    /**
     * 查询应用版本信息
     *
     * @param id 主键
     * @return 应用版本信息
     */
    @Override
    public BizAppVersionsVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询应用版本信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 应用版本信息分页列表
     */
    @Override
    public TableDataInfo<BizAppVersionsVo> queryPageList(BizAppVersionsBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizAppVersions> lqw = buildQueryWrapper(bo);
        Page<BizAppVersionsVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的应用版本信息列表
     *
     * @param bo 查询条件
     * @return 应用版本信息列表
     */
    @Override
    public List<BizAppVersionsVo> queryList(BizAppVersionsBo bo) {
        LambdaQueryWrapper<BizAppVersions> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizAppVersions> buildQueryWrapper(BizAppVersionsBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizAppVersions> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizAppVersions::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getPlatform()), BizAppVersions::getPlatform, bo.getPlatform());
        lqw.eq(bo.getBuildNumber() != null, BizAppVersions::getBuildNumber, bo.getBuildNumber());
        lqw.eq(bo.getFileSize() != null, BizAppVersions::getFileSize, bo.getFileSize());
        lqw.eq(StringUtils.isNotBlank(bo.getDownloadUrl()), BizAppVersions::getDownloadUrl, bo.getDownloadUrl());
        lqw.eq(StringUtils.isNotBlank(bo.getChecksum()), BizAppVersions::getChecksum, bo.getChecksum());
        lqw.eq(StringUtils.isNotBlank(bo.getReleaseNotes()), BizAppVersions::getReleaseNotes, bo.getReleaseNotes());
        lqw.eq(StringUtils.isNotBlank(bo.getMinSupportedVersion()), BizAppVersions::getMinSupportedVersion, bo.getMinSupportedVersion());
        lqw.eq(bo.getForceUpdate() != null, BizAppVersions::getForceUpdate, bo.getForceUpdate());
        lqw.eq(bo.getUpdateDeadline() != null, BizAppVersions::getUpdateDeadline, bo.getUpdateDeadline());
        lqw.eq(bo.getIsActive() != null, BizAppVersions::getIsActive, bo.getIsActive());
        lqw.eq(bo.getCreatedAt() != null, BizAppVersions::getCreatedAt, bo.getCreatedAt());
        lqw.eq(bo.getUpdatedAt() != null, BizAppVersions::getUpdatedAt, bo.getUpdatedAt());
        return lqw;
    }

    /**
     * 新增应用版本信息
     *
     * @param bo 应用版本信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizAppVersionsBo bo) {
        BizAppVersions add = MapstructUtils.convert(bo, BizAppVersions.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改应用版本信息
     *
     * @param bo 应用版本信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizAppVersionsBo bo) {
        BizAppVersions update = MapstructUtils.convert(bo, BizAppVersions.class);
        validEntityBeforeSave(update);
        Boolean a = baseMapper.updateById(update) > 0;
        return a;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizAppVersions entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除应用版本信息信息
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
    public List<BizAppVersionsVo> queryList(LambdaQueryWrapper<BizAppVersions> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizAppVersionsVo queryOne(LambdaQueryWrapper<BizAppVersions> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizAppVersionsBo bo) {
        BizAppVersions update = MapstructUtils.convert(bo, BizAppVersions.class);
        return baseMapper.saveOrUpdate(update);
    }
}
