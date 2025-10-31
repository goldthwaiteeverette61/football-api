package org.dromara.biz.domain.bo;

import lombok.Data;
import java.util.List;

@Data
public class BatchFailBo {
    private List<Long> followIds;
    private String remark;
}
