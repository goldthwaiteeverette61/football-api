package org.dromara.biz.domain.bo;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

// 用於接收 a.png 保存時前端傳來的數據
@Data
public class UserFollowDetailsSaveBo {

    private List<Long> followIds;
    // 核心修改：數據結構變爲組合列表，每個內層List代表一個獨立的投注組合
    private List<DetailItem> combinations;

    @Data
    public static class DetailItem {
        private Long periodId;
        private Long periodDetailsId;
        private Long matchId;
        private String poolCode;
        private String selection;
        private BigDecimal odds;
        private String goalLine;
        private String matchName;
    }
}
