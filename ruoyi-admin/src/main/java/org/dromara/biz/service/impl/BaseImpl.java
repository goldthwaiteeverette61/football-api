package org.dromara.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * Service层通用数据处理 - 抽象实现类
 * <p>
 * 子类通过继承这个类，可以获得便捷的链式查询能力。
 *
 * @param <T> Entity 实体类
 * @param <Q> Vo 视图对象
 * @author Lion Li & Gemini
 */
public abstract class BaseImpl<T, Q> {

    /**
     * 由子类在初始化时注入具体的Mapper实例。
     */
    public BaseMapperPlus<T, Q> baseMapperPlus;

    /**
     * 线程不安全的查询构造器，每次调用lqw()时都应重新创建。
     */
    private LambdaQueryWrapper<T> wrapper;

    /**
     * 获取一个新的 LambdaQueryWrapper 实例，用于开始链式查询。
     *
     * @return 一个新的 LambdaQueryWrapper<T> 实例
     */
    public LambdaQueryWrapper<T> lqw() {
        return new LambdaQueryWrapper<>();
    }

    /**
     * 执行列表查询。
     * <p>
     * 在调用此方法前，必须先调用 lqw() 并构建查询条件。
     *
     * @return 查询结果列表 List<Q>
     */
    public List<Q> queryList(LambdaQueryWrapper<T> lqw) {
        return baseMapperPlus.selectVoList(lqw);
    }

    /**
     * 执行单条记录查询，返回第一条匹配的记录。
     * <p>
     * 在调用此方法前，必须先调用 lqw() 并构建查询条件。
     *
     * @return 查询到的单个结果 Q，如果不存在则返回 null
     */
    public Q queryOne(LambdaQueryWrapper<T> lqw) {
        // 使用 limit 1 优化查询，确保数据库只返回一条记录
        List<Q> list = baseMapperPlus.selectVoList(lqw.last("limit 1"));
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
