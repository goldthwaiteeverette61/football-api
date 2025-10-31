package org.dromara.biz.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.List;

/**
 * Service层通用数据处理
 *
 * @param <T> Entity
 * @param <Q> Vo
 */
public interface IBaseService<T, Q> {

    /**
     * 获取一个 LambdaQueryWrapper
     * @return LambdaQueryWrapper<T>
     */
    LambdaQueryWrapper<T> lqw();

    /**
     * 执行列表查询
     * @return List<Q>
     */
    List<Q> queryList();

    /**
     * 执行单条记录查询
     * @return Q
     */
    Q queryOne();

}
