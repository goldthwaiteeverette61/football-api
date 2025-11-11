package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizOddsHistory;
import org.dromara.biz.domain.vo.BizOddsHistoryVo;
import org.dromara.biz.domain.bo.BizOddsHistoryBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 比赛赔率历史Service接口
 *
 * @author Lion Li
 * @date 2025-11-10
 */
public interface IBizOddsHistoryService {

    /**
     * 【新增】查询指定比赛和玩法的 *最后一条* 赔率历史记录
     * @param matchId 比赛ID
     * @param poolCode 玩法代码
     * @return 最后一条历史记录，如果不存在则返回 null
     */
    BizOddsHistoryVo queryLastHistoryByMatchIdAndPoolCode(Long matchId, String poolCode);

    /**
     * 根据比赛ID查询其所有赔率历史
     * @param matchId 比赛ID
     * @return 赔率历史列表
     */
    List<BizOddsHistoryVo> queryHistoryByMatchId(Long matchId);

    /**
     * 根据比赛ID和玩法代码查询赔率历史
     * @param matchId 比赛ID
     * @param poolCode 玩法代码 (例如 "HAD", "HHAD")
     * @return 赔率历史列表
     */
    BizOddsHistoryVo queryOneByMatchIdAndPoolCode(Long matchId, String poolCode);


    /**
     * 根据比赛ID和玩法代码查询赔率历史
     * @param matchId 比赛ID
     * @param poolCode 玩法代码 (例如 "HAD", "HHAD")
     * @return 赔率历史列表
     */
    List<BizOddsHistoryVo> queryHistoryByMatchIdAndPoolCode(Long matchId, String poolCode);


    LambdaQueryWrapper<BizOddsHistory> getLqw();

    /**
     * 查询比赛赔率历史
     *
     * @param historyId 主键
     * @return 比赛赔率历史
     */
    BizOddsHistoryVo queryById(Long historyId);

    /**
     * 分页查询比赛赔率历史列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 比赛赔率历史分页列表
     */
    TableDataInfo<BizOddsHistoryVo> queryPageList(BizOddsHistoryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的比赛赔率历史列表
     *
     * @param bo 查询条件
     * @return 比赛赔率历史列表
     */
    List<BizOddsHistoryVo> queryList(BizOddsHistoryBo bo);

    /**
     * 新增比赛赔率历史
     *
     * @param bo 比赛赔率历史
     * @return 是否新增成功
     */
    Boolean insertByBo(BizOddsHistoryBo bo);

    /**
     * 修改比赛赔率历史
     *
     * @param bo 比赛赔率历史
     * @return 是否修改成功
     */
    Boolean updateByBo(BizOddsHistoryBo bo);

    /**
     * 校验并批量删除比赛赔率历史信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizOddsHistoryVo> queryList(LambdaQueryWrapper<BizOddsHistory> lqw);

    BizOddsHistoryVo queryOne(LambdaQueryWrapper<BizOddsHistory> lqw);

    /**
     * 新增或修改
     *
     * @param bo 比赛赔率历史
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizOddsHistoryBo bo);
}
