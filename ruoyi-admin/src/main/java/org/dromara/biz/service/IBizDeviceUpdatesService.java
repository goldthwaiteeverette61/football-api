package org.dromara.biz.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.biz.domain.BizDeviceUpdates;
import org.dromara.biz.domain.vo.BizDeviceUpdatesVo;
import org.dromara.biz.domain.bo.BizDeviceUpdatesBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备更新记录Service接口
 *
 * @author Lion Li
 * @date 2025-09-13
 */
public interface IBizDeviceUpdatesService {

    /**
     * 查询设备更新记录
     *
     * @param id 主键
     * @return 设备更新记录
     */
    BizDeviceUpdatesVo queryById(Integer id);

    /**
     * 分页查询设备更新记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备更新记录分页列表
     */
    TableDataInfo<BizDeviceUpdatesVo> queryPageList(BizDeviceUpdatesBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备更新记录列表
     *
     * @param bo 查询条件
     * @return 设备更新记录列表
     */
    List<BizDeviceUpdatesVo> queryList(BizDeviceUpdatesBo bo);

    /**
     * 新增设备更新记录
     *
     * @param bo 设备更新记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BizDeviceUpdatesBo bo);

    /**
     * 修改设备更新记录
     *
     * @param bo 设备更新记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BizDeviceUpdatesBo bo);

    /**
     * 校验并批量删除设备更新记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Integer> ids, Boolean isValid);


    List<BizDeviceUpdatesVo> queryList(LambdaQueryWrapper<BizDeviceUpdates> lqw);

    BizDeviceUpdatesVo queryOne(LambdaQueryWrapper<BizDeviceUpdates> lqw);

    /**
     * 新增或修改
     *
     * @param bo 设备更新记录
     * @return 是否修改成功
     */
    Boolean saveOrUpdate(BizDeviceUpdatesBo bo);
}
