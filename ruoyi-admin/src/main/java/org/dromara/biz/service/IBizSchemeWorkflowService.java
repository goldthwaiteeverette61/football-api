package org.dromara.biz.service;

import org.dromara.biz.domain.dto.FollowSchemeDto;

public interface IBizSchemeWorkflowService {

    /**
     * 用户跟投方案的某一期
     * @param dto 跟投数据
     */
    void followScheme(FollowSchemeDto dto);

}
