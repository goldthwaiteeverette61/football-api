
// 文件路径: org/dromara/biz/service/IBizTransactionWorkflowService.java
package org.dromara.biz.service;

import org.dromara.biz.domain.bo.TransactionHistoryBo;
import org.dromara.biz.domain.dto.UserTransferDto;
import org.dromara.biz.domain.vo.TransactionHistoryVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

public interface IBizTransactionWorkflowService {

    void initiateTransfer(UserTransferDto dto);

    /**
     * 分页查询我的交易历史记录
     * @param bo 筛选条件
     * @param pageQuery 分页信息
     * @return 分页列表
     */
    TableDataInfo<TransactionHistoryVo> queryMyTransactionHistory(TransactionHistoryBo bo, PageQuery pageQuery);
}
