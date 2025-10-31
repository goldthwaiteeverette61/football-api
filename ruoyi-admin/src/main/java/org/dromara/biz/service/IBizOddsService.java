package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizOdds;
import org.dromara.biz.domain.bo.BizOddsBo;
import org.dromara.biz.domain.vo.BizOddsVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 比赛赔率Service接口
 *
 * @author Lion Li
 * @date 2025-08-06
 */
public interface IBizOddsService {

    Map<Long, String> queryGoalLinesByMatchIds(List<Long> matchIds, String poolCode);

    /**
     * 新增或更新
     *
     * @param bo 对象
     * @return 是否成功
     */
    Boolean saveOrUpdate(BizOddsBo bo);

    /**
     * 查询比赛赔率
     *
     * @param id 主键
     * @return 比赛赔率
     */
    BizOddsVo queryById(Long id);

    /**
     * 分页查询比赛赔率列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比赛赔率分页列表
     */
    TableDataInfo<BizOddsVo> queryPageList(BizOddsBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的比赛赔率列表
     *
     * @param bo 查询条件
     * @return 比赛赔率列表
     */
    List<BizOddsVo> queryList(BizOddsBo bo);

    /**
     * 新增比赛赔率
     *
     * @param bo 比赛赔率
     * @return 是否新增成功
     */
    Boolean insertByBo(BizOddsBo bo);

    /**
     * 修改比赛赔率
     *
     * @param bo 比赛赔率
     * @return 是否修改成功
     */
    Boolean updateByBo(BizOddsBo bo);

    /**
     * 校验并批量删除比赛赔率信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizOddsVo> queryList(LambdaQueryWrapper<BizOdds> lqw);

    BizOddsVo queryOne(LambdaQueryWrapper<BizOdds> lqw);
}
