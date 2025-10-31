package org.dromara.biz.service;

import org.dromara.biz.domain.vo.SchemeDashboardVo;

public interface IBizDashboardService {

    /**
     * 获取方案看板数据
     * @return 组装好的看板数据视图对象
     */
    SchemeDashboardVo getSchemeDashboardData();
}
