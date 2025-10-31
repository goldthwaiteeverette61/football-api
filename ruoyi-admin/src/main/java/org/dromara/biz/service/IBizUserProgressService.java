package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizUserProgress;
import org.dromara.biz.domain.bo.BizUserProgressBo;
import org.dromara.biz.domain.vo.BizUserFollowsVo;
import org.dromara.biz.domain.vo.BizUserProgressVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 用户跟投进度Service接口
 *
 * @author Lion Li
 * @date 2025-08-12
 */
public interface IBizUserProgressService {

    void createInitialProgress(Long userId,String userName);
    /**
     * 【核心新增】切换用户投注模式
     *
     * @param userId  用户ID
     * @param betType 新的投注模式 (normal 或 double)
     */
    void updateBetType(Long userId, String betType);

    void resetUserLosses(Long userId);

    void incrementUserLosses(Long userId, BizUserFollowsVo followsVo);

    /**
     * 查询用户跟投进度
     *
     * @param progressId 主键
     * @return 用户跟投进度
     */
    BizUserProgressVo queryById(Long progressId);

    /**
     * 分页查询用户跟投进度列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户跟投进度分页列表
     */
    TableDataInfo<BizUserProgressVo> queryPageList(BizUserProgressBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户跟投进度列表
     *
     * @param bo 查询条件
     * @return 用户跟投进度列表
     */
    List<BizUserProgressVo> queryList(BizUserProgressBo bo);

    /**
     * 新增用户跟投进度
     *
     * @param bo 用户跟投进度
     * @return 是否新增成功
     */
    Boolean insertByBo(BizUserProgressBo bo);

    /**
     * 修改用户跟投进度
     *
     * @param bo 用户跟投进度
     * @return 是否修改成功
     */
    Boolean updateByBo(BizUserProgressBo bo);

    /**
     * 校验并批量删除用户跟投进度信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    List<BizUserProgressVo> queryList(LambdaQueryWrapper<BizUserProgress> lqw);

    BizUserProgressVo queryOne(LambdaQueryWrapper<BizUserProgress> lqw);

    /**
     * 根据用户ID查询跟投进度
     *
     * @param userId 用户ID
     * @return 用户跟投进度
     */
    BizUserProgressVo findByUserId(Long userId);

    /**
     * 核心修复：新增方法
     * 根据用户ID删除进度记录
     * @param userId 用户ID
     */
    void deleteByUserId(Long userId);

    /**
     * 处理用户跟投胜利的逻辑
     *
     * @param userId 用户ID
     */
    void handleFollowWin(Long userId);

    /**
     * 处理用户跟投失败的逻辑
     *
     * @param userId 用户ID
     * @param amount 失败的金额
     */
    void handleFollowLoss(Long userId, BigDecimal amount);
}
