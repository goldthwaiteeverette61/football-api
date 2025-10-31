

package org.dromara.biz.app;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.biz.domain.bo.TransactionHistoryBo;
import org.dromara.biz.domain.dto.UserTransferDto;
import org.dromara.biz.domain.vo.TransactionDetailsVo;
import org.dromara.biz.domain.vo.TransactionHistoryVo;
import org.dromara.biz.service.IBizTransactionWorkflowService;
import org.dromara.biz.service.ITransactionDetailsService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 資金流水+轉賬
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/app/transaction")
@Tag(name = "transaction", description = "交易")
public class AppTransactionController extends BaseController {

    private final IBizTransactionWorkflowService transactionWorkflowService;

    private final ITransactionDetailsService transactionDetailsService;


    /**
     * 發起站內轉賬
     */
    @PostMapping("/transfer")
    public R<Void> transfer(@Validated @RequestBody UserTransferDto dto) {
        transactionWorkflowService.initiateTransfer(dto);
        return R.ok("轉賬成功");
    }

    /**
     * 查詢我的個人交易流水記錄
     * @param bo 包含 status 和 transactionType 的篩選條件
     */
    @GetMapping("/history")
    public TableDataInfo<TransactionHistoryVo> history(TransactionHistoryBo bo, PageQuery pageQuery) {
        return transactionWorkflowService.queryMyTransactionHistory(bo, pageQuery);
    }

    /**
     * 獲取單筆交易的完整詳情
     * @param id 交易流水ID
     */
    @GetMapping("/{id}")
    public R<TransactionDetailsVo> getTransactionDetails(@PathVariable Long id) {
        // 可在此處增加權限校驗，確保用戶只能查詢自己的交易記錄
        return R.ok(transactionDetailsService.getTransactionDetails(id));
    }
}
