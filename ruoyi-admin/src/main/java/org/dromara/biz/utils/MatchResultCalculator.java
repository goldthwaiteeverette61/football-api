package org.dromara.biz.utils;

import lombok.extern.slf4j.Slf4j;
import org.dromara.biz.domain.bo.BizMatchResultsBo;
import org.dromara.biz.domain.vo.BizMatchesVo;
import org.dromara.common.core.utils.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 比赛结果计算工具类
 *
 * @author Lion Li
 */
@Slf4j
public class MatchResultCalculator {

    private static final Set<String> CRS_WIN_SCORES = new HashSet<>(Arrays.asList("1:0", "2:0", "2:1", "3:0", "3:1", "3:2", "4:0", "4:1", "4:2", "5:0", "5:1", "5:2"));
    private static final Set<String> CRS_DRAW_SCORES = new HashSet<>(Arrays.asList("0:0", "1:1", "2:2", "3:3"));
    private static final Set<String> CRS_LOSE_SCORES = new HashSet<>(Arrays.asList("0:1", "0:2", "1:2", "0:3", "1:3", "2:3", "0:4", "1:4", "2:4", "0:5", "1:5", "2:5"));

    /**
     * 计算一场比赛的所有玩法结果
     *
     * @param match         比赛信息
     * @param goalLineStr   让球盘口字符串
     * @return 赛果列表
     */
    public static List<BizMatchResultsBo> calculateAllResults(BizMatchesVo match, String goalLineStr) {
        if (match == null || StringUtils.isBlank(match.getFullScore())) {
            return null;
        }

        String[] fullScores = match.getFullScore().split(":");

        if (fullScores.length != 2) {
            log.warn("比赛ID {} 的比分格式不正确 (full={})", match.getMatchId(), match.getFullScore());
            return null;
        }

        try {
            int fullHome = Integer.parseInt(fullScores[0]);
            int fullAway = Integer.parseInt(fullScores[1]);

            List<BizMatchResultsBo> results = new ArrayList<>();

            // 1. 胜平负 (HAD)
            results.add(createResult(match.getMatchId(), "HAD", getHadResult(fullHome, fullAway), "0"));

            // 2. 让球胜平负 (HHAD)
            if (StringUtils.isNotBlank(goalLineStr)) {
                BigDecimal goalLine = new BigDecimal(goalLineStr);
                results.add(createResult(match.getMatchId(), "HHAD", getHhadResult(fullHome, fullAway, goalLine), goalLineStr));
            }

            // 3. 比分 (CRS)
            results.add(createResult(match.getMatchId(), "CRS", getCrsResult(fullHome, fullAway), "0"));

            // 4. 总进球 (TTG)
            results.add(createResult(match.getMatchId(), "TTG", getTtgResult(fullHome, fullAway), "0"));

            // 5. 半全场 (HAFU)
            if (StringUtils.isNotBlank(match.getHalfScore()) && match.getHalfScore().contains(":")) {
                String[] halfScores = match.getHalfScore().split(":");
                if (halfScores.length == 2) {
                    try {
                        int halfHome = Integer.parseInt(halfScores[0]);
                        int halfAway = Integer.parseInt(halfScores[1]);
                        results.add(createResult(match.getMatchId(), "HAFU", getHafuResult(halfHome, halfAway, fullHome, fullAway), "0"));
                    } catch (NumberFormatException e) {
                        log.warn("解析比赛ID {} 的半场比分时出错 (half={})", match.getMatchId(), match.getHalfScore());
                    }
                }
            }

            return results;
        } catch (NumberFormatException e) {
            log.error("解析比赛ID {} 的比分时出错", match.getMatchId(), e);
            return null;
        }
    }

    public static String calculateHadResult(String fullScore, String goalLineStr) {
        if (StringUtils.isAnyBlank(fullScore, goalLineStr) || !fullScore.contains(":")) {
            return null;
        }
        String[] scores = fullScore.split(":");
        try {
            int home = Integer.parseInt(scores[0]);
            int away = Integer.parseInt(scores[1]);
            BigDecimal goalLine = new BigDecimal(goalLineStr);
            if (goalLine.compareTo(BigDecimal.ZERO) == 0) {
                return getHadResult(home, away);
            } else {
                return getHhadResult(home, away, goalLine);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }


    private static BizMatchResultsBo createResult(Long matchId, String poolCode, String combination, String goalLine) {
        BizMatchResultsBo result = new BizMatchResultsBo();
        result.setMatchId(matchId);
        result.setPoolCode(poolCode);
        result.setCombination(combination);
        result.setGoalLine(goalLine);
        // 其他字段如 combinationDesc, odds 等可以留空，因为这是内部生成的赛果
        return result;
    }

    private static String getHadResult(int home, int away) {
        if (home > away) return "H";
        if (home < away) return "A";
        return "D";
    }

    private static String getHhadResult(int home, int away, BigDecimal goalLine) {
        BigDecimal adjustedHome = BigDecimal.valueOf(home).add(goalLine);
        int comparison = adjustedHome.compareTo(BigDecimal.valueOf(away));
        if (comparison > 0) return "H";
        if (comparison < 0) return "A";
        return "D";
    }

    private static String getCrsResult(int home, int away) {
        String score = home + ":" + away;
        if (home > away && !CRS_WIN_SCORES.contains(score)) return "HX";
        if (home < away && !CRS_LOSE_SCORES.contains(score)) return "AX";
        if (home == away && !CRS_DRAW_SCORES.contains(score)) return "DX";
        return score;
    }

    private static String getTtgResult(int home, int away) {
        int total = home + away;
        return (total >= 7) ? "7+" : String.valueOf(total);
    }

    // 【修改】此方法保留，但不再被主流程调用
    private static String getHafuResult(int halfHome, int halfAway, int fullHome, int fullAway) {
        String halfResult = getHadResult(halfHome, halfAway);
        String fullResult = getHadResult(fullHome, fullAway);
        return halfResult + "" + fullResult;
    }
}

