package org.dromara.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.biz.domain.BizMatchResults;
import org.dromara.biz.domain.vo.BizMatchResultsVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;


/**
 * 比赛赛果Mapper接口
 *
 * @author Lion Li
 * @date 2025-08-08
 */
public interface BizMatchResultsMapper extends BaseMapperPlus<BizMatchResults, BizMatchResultsVo> {
    /**
     * 分页查询比赛赛果列表 (包含详情)
     */
    Page<BizMatchResultsVo> selectVoPageWithDetails(@Param("page") Page<BizMatchResultsVo> page, @Param("ew") Wrapper<BizMatchResults> queryWrapper);

}
