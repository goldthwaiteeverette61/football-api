package org.dromara.biz.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.BizInvitationCodes;
import org.dromara.biz.domain.bo.BizInvitationCodesBo;
import org.dromara.biz.domain.vo.BizInvitationCodesVo;
import org.dromara.biz.mapper.BizInvitationCodesMapper;
import org.dromara.biz.service.IBizInvitationCodesService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预生成邀请码池Service业务层处理
 *
 * @author Lion Li
 * @date 2025-08-28
 */
@RequiredArgsConstructor
@Service
public class BizInvitationCodesServiceImpl extends BaseImpl<BizInvitationCodes,BizInvitationCodesVo> implements IBizInvitationCodesService {

    private final BizInvitationCodesMapper baseMapper;

    // 【核心实现】定义一个不包含易混淆字符的字符池
    // 排除了 0, o, O, 1, l, I
    private static final String CHAR_POOL = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz";
    private static final int CODE_LENGTH = 5;
    private static final SecureRandom random = new SecureRandom();

    @Override
    public BizInvitationCodesVo findOneAvailableCode() {
        LambdaQueryWrapper<BizInvitationCodes> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizInvitationCodes::getStatus, "available")
            .last("LIMIT 1");
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 【核心重构】批量生成唯一邀请码的简化实现
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateCodes(int count) {
        if (count <= 0) {
            return;
        }

        HashMap<String,String> a = new HashMap<>();
        for (int i = 0; i < count; i++) {
            try{
                String g = generateRandomCode();
                a.put(g,"");
                BizInvitationCodes bizInvitationCodes = new BizInvitationCodes();
                bizInvitationCodes.setInvitationCode(g);
                baseMapper.insert(bizInvitationCodes);
            }catch (Exception e){
            }
        }
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 查询预生成邀请码池
     *
     * @param codeId 主键
     * @return 预生成邀请码池
     */
    @Override
    public BizInvitationCodesVo queryById(Long codeId){
        return baseMapper.selectVoById(codeId);
    }

    /**
     * 分页查询预生成邀请码池列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 预生成邀请码池分页列表
     */
    @Override
    public TableDataInfo<BizInvitationCodesVo> queryPageList(BizInvitationCodesBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BizInvitationCodes> lqw = buildQueryWrapper(bo);
        Page<BizInvitationCodesVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的预生成邀请码池列表
     *
     * @param bo 查询条件
     * @return 预生成邀请码池列表
     */
    @Override
    public List<BizInvitationCodesVo> queryList(BizInvitationCodesBo bo) {
        LambdaQueryWrapper<BizInvitationCodes> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BizInvitationCodes> buildQueryWrapper(BizInvitationCodesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<BizInvitationCodes> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BizInvitationCodes::getCodeId);
        lqw.eq(StringUtils.isNotBlank(bo.getInvitationCode()), BizInvitationCodes::getInvitationCode, bo.getInvitationCode());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BizInvitationCodes::getStatus, bo.getStatus());
        lqw.eq(bo.getAssigneeUserId() != null, BizInvitationCodes::getAssigneeUserId, bo.getAssigneeUserId());
        lqw.eq(bo.getAssignTime() != null, BizInvitationCodes::getAssignTime, bo.getAssignTime());
        return lqw;
    }

    /**
     * 新增预生成邀请码池
     *
     * @param bo 预生成邀请码池
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BizInvitationCodesBo bo) {
        BizInvitationCodes add = MapstructUtils.convert(bo, BizInvitationCodes.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setCodeId(add.getCodeId());
        }
        return flag;
    }

    /**
     * 修改预生成邀请码池
     *
     * @param bo 预生成邀请码池
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BizInvitationCodesBo bo) {
        BizInvitationCodes update = MapstructUtils.convert(bo, BizInvitationCodes.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BizInvitationCodes entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除预生成邀请码池信息
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
    public List<BizInvitationCodesVo> queryList(LambdaQueryWrapper<BizInvitationCodes> lqw) {
        // 直接调用父类 BaseImpl 中的 queryList() 方法
        return super.queryList(lqw);
    }

    /**
     * 执行单条记录查询
     * (这个方法会使用 lqw() 构建的查询条件)
     */
    @Override
    public BizInvitationCodesVo queryOne(LambdaQueryWrapper<BizInvitationCodes> lqw) {
        // 直接调用父类 BaseImpl 中的 queryOne() 方法
        return super.queryOne(lqw);
    }

    @Override
    public Boolean saveOrUpdate(BizInvitationCodesBo bo) {
        BizInvitationCodes update = MapstructUtils.convert(bo, BizInvitationCodes.class);
        return baseMapper.saveOrUpdate(update);
    }
}
