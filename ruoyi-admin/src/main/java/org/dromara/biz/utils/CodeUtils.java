package org.dromara.biz.utils;

import java.util.Map;

public class CodeUtils {

    public static final Map<String, String> POOL_CODE_MAP = Map.of(
        "HAD", "胜平负",
        "HHAD", "让球胜平负"
        // 您可以在此添加更多玩法翻译
    );

    public static final Map<String, String> SELECTION_MAP = Map.of(
        "H", "胜",
        "D", "平",
        "A", "负"
    );
}
