package org.dromara.biz.domain.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class TransferDetailsVo implements Serializable {
    /**
     * 交易另一方的用户ID
     */
    private Long otherPartyUserId;

    /**
     * 交易另一方的用户昵称
     */
    private String otherPartyUsername;
}
