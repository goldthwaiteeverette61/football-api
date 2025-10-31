package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizSports;
import org.dromara.biz.domain.vo.BizSportsVo;
import org.dromara.biz.domain.bo.BizSportsBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 体育项目Service接口
 *
 * @author Lion Li
 * @date 2025-08-25
 */
public interface IBizSportsService {

    /**
     * 查询体育项目
     *
     * @param id 主键
     * @return 体育项目
     */
    BizSportsVo queryById(Long id);

    /**
     * 分页查询体育项目列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 体育项目分页列表
     */
    TableDataInfo<BizSportsVo> queryPageList(BizSportsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的体育项目列表
     *
     * @param bo 查询条件
     * @return 体育项目列表
     */
    List<BizSportsVo> queryList(BizSportsBo bo);

    /**
     * 新增体育项目
     *
     * @param bo 体育项目
     * @return 是否新增成功
     */
    Boolean insertByBo(BizSportsBo bo);

    /**
     * 修改体育项目
     *
     * @param bo 体育项目
     * @return 是否修改成功
     */
    Boolean updateByBo(BizSportsBo bo);

    /**
     * 校验并批量删除体育项目信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizSportsVo> queryList(LambdaQueryWrapper<BizSports> lqw);

    BizSportsVo queryOne(LambdaQueryWrapper<BizSports> lqw);

    /**
     * 新增或修改
     *
     * @param bo 体育项目
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizSportsBo bo);
}
