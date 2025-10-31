package org.dromara.biz.utils;

import org.dromara.biz.domain.BizMatchResults;
import org.dromara.biz.domain.dto.BetOrderDto;
import org.dromara.common.core.exception.ServiceException;

import java.util.Set;

/**
 * 投注訂單數據校驗工具類
 * @author Lion Li
 */
public final class PoolCodeUtils {

    private PoolCodeUtils() {
        // 私有構造函數，防止實例化
    }

    private static final Set<String> CRS_OTHER_SELECTIONS = Set.of("HX", "DX", "AX");

    /**
     * 校驗投注訂單數據的格式是否符合後端要求
     *
     * @param betOrderDto 待校驗的投注訂單
     * @throws ServiceException 如果數據格式不正確
     */
    public static void validate(BetOrderDto betOrderDto) {
        if (betOrderDto == null || betOrderDto.getDetails() == null || betOrderDto.getDetails().isEmpty()) {
            throw new ServiceException("投注內容不能為空");
        }

        for (BetOrderDto.BetDetailDto detail : betOrderDto.getDetails()) {
            if (detail.getPoolCode() == null || detail.getSelection() == null) {
                throw new ServiceException("玩法代碼或投注選項不能為空");
            }

            String poolCode = detail.getPoolCode().toUpperCase();
            String selection = detail.getSelection();

            // 根據玩法校驗選項格式
            if (poolCode.contains(BizMatchResults.POOL_CODE_HAD)) {
                if (!selection.matches("^[HDA]$")) {
                    throw new ServiceException(
                        String.format("玩法 [%s] 的投注選項 [%s] 格式不正確。有效選項為 'H', 'D', 或 'A'。", poolCode, selection)
                    );
                }
            } else if (BizMatchResults.POOL_CODE_HAFU.equals(poolCode)) {
                if (!selection.matches("^[HDA]{2}$")) {
                    throw new ServiceException(
                        String.format("玩法 [HAFU] 的投注選項 [%s] 格式不正確。有效選項應為兩個字母的組合（例如 'HH', 'HD', 'AA'）。", selection)
                    );
                }
            } else if (BizMatchResults.POOL_CODE_TTG.equals(poolCode)) {
                if (!selection.matches("^([0-6]|7\\+)$")) {
                    throw new ServiceException(
                        String.format("玩法 [TTG] 的投注選項 [%s] 格式不正確。有效選項應為 0-6 的數字或 '7+'。", selection)
                    );
                }
            } else if (BizMatchResults.POOL_CODE_CRS.equals(poolCode)) {
                if (!selection.matches("^\\d+[:-]\\d+$") && !CRS_OTHER_SELECTIONS.contains(selection)) {
                    throw new ServiceException(
                        String.format("玩法 [CRS] 的投注選項 [%s] 格式不正確。有效選項應為比分格式（例如 '1:0'）或 '胜其他', '平其他', '负其他'。", selection)
                    );
                }
            }

        }
    }

    /**
     * 数据格式化
     * @param detailDto
     */
    public static void formatDate(BetOrderDto.BetDetailDto detailDto){
        detailDto.setPoolCode(detailDto.getPoolCode().toUpperCase());

        if(detailDto.getPoolCode().contains(BizMatchResults.POOL_CODE_HAD)){
            detailDto.setSelection(PoolCodeUtils.formatHAD(detailDto.getSelection()));
        } else if(detailDto.getPoolCode().equals(BizMatchResults.POOL_CODE_HAFU)){
            detailDto.setSelection(PoolCodeUtils.formatHAFU(detailDto.getSelection()));
        } else if(detailDto.getPoolCode().equals(BizMatchResults.POOL_CODE_TTG)){
            detailDto.setSelection(PoolCodeUtils.formatTTG(detailDto.getSelection()));
        } else if(detailDto.getPoolCode().equals(BizMatchResults.POOL_CODE_CRS)){
            detailDto.setSelection(PoolCodeUtils.formatCRS(detailDto.getSelection()));
        }
    }

    public static String formatHAD(String content){
        return content.toUpperCase().replace("HOME","H").replace("DRAW","D").replace("AWAY","A");
    }

    public static String formatHAFU(String content){
        return content.toUpperCase().replace("S","H").replace("P","D").replace("F","A");
    }

    public static String formatTTG(String content){
        return content.toUpperCase().replace("GOALS","").replace("PLUS","+");
    }

    public static String formatCRS(String content){
        return content.toUpperCase().replace("-",":").replace("平其他","DX").replace("胜其他","HX").replace("负其他","AX");
    }
}
