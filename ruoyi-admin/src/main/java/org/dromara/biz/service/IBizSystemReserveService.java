package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizSystemReserve;
import org.dromara.biz.domain.vo.BizSystemReserveVo;
import org.dromara.biz.domain.bo.BizSystemReserveBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 系统储备金明细Service接口
 *
 * @author Lion Li
 * @date 2025-08-09
 */
public interface IBizSystemReserveService {

    /**
     * 查询系统储备金明细
     *
     * @param reserveId 主键
     * @return 系统储备金明细
     */
    BizSystemReserveVo queryById(Long reserveId);

    /**
     * 分页查询系统储备金明细列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 系统储备金明细分页列表
     */
    TableDataInfo<BizSystemReserveVo> queryPageList(BizSystemReserveBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的系统储备金明细列表
     *
     * @param bo 查询条件
     * @return 系统储备金明细列表
     */
    List<BizSystemReserveVo> queryList(BizSystemReserveBo bo);

    /**
     * 新增系统储备金明细
     *
     * @param bo 系统储备金明细
     * @return 是否新增成功
     */
    Boolean insertByBo(BizSystemReserveBo bo);

    /**
     * 修改系统储备金明细
     *
     * @param bo 系统储备金明细
     * @return 是否修改成功
     */
    Boolean updateByBo(BizSystemReserveBo bo);

    /**
     * 校验并批量删除系统储备金明细信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);


    /**
     * 新增或更新
     *
     * @param bo 对象
     * @return 是否成功
     */
    Boolean insertOrUpdate(BizSystemReserveBo bo);

    List<BizSystemReserveVo> queryList(LambdaQueryWrapper<BizSystemReserve> lqw);

    BizSystemReserveVo queryOne(LambdaQueryWrapper<BizSystemReserve> lqw);
}
