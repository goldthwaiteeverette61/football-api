package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizAppVersions;
import org.dromara.biz.domain.bo.BizAppVersionsBo;
import org.dromara.biz.domain.dto.VersionCheckRequestDto;
import org.dromara.biz.domain.dto.VersionCheckResponseDto;
import org.dromara.biz.domain.vo.BizAppVersionsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 应用版本信息Service接口
 *
 * @author Lion Li
 * @date 2025-09-13
 */
public interface IBizAppVersionsService {

    VersionCheckResponseDto checkVersion(VersionCheckRequestDto requestDto);

    /**
     * 查询应用版本信息
     *
     * @param id 主键
     * @return 应用版本信息
     */
    BizAppVersionsVo queryById(Long id);

    /**
     * 分页查询应用版本信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 应用版本信息分页列表
     */
    TableDataInfo<BizAppVersionsVo> queryPageList(BizAppVersionsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的应用版本信息列表
     *
     * @param bo 查询条件
     * @return 应用版本信息列表
     */
    List<BizAppVersionsVo> queryList(BizAppVersionsBo bo);

    /**
     * 新增应用版本信息
     *
     * @param bo 应用版本信息
     * @return 是否新增成功
     */
    Boolean insertByBo(BizAppVersionsBo bo);

    /**
     * 修改应用版本信息
     *
     * @param bo 应用版本信息
     * @return 是否修改成功
     */
    Boolean updateByBo(BizAppVersionsBo bo);

    /**
     * 校验并批量删除应用版本信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizAppVersionsVo> queryList(LambdaQueryWrapper<BizAppVersions> lqw);

    BizAppVersionsVo queryOne(LambdaQueryWrapper<BizAppVersions> lqw);

    /**
     * 新增或修改
     *
     * @param bo 应用版本信息
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizAppVersionsBo bo);
}
