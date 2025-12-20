package com.cg.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.SQLException;
@Slf4j
public class PerformanceInterceptor implements InnerInterceptor {


    @Override
    // 查询拦截
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        long startTime = System.currentTimeMillis();
        try {
            return true;
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCost = endTime - startTime;
            String sql = boundSql.getSql();
            if (sqlCost > 1000){
                log.warn("Query SQL: {} 执行耗时: {} ms", sql, sqlCost);
            }
            log.info("Query SQL: {} 执行耗时: {} ms", sql, sqlCost);
        }
    }

    @Override
    //增删改拦截
    public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter) {
        long startTime = System.currentTimeMillis();
        BoundSql boundSql = ms.getBoundSql(parameter);
        try {
            return true;
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCost = endTime - startTime;
            String sql = boundSql.getSql();
            // 排除定时任务的sql
            if (!sql.contains("INTERVAL")) {
                if (sqlCost > 1000) {
                    log.warn("Update SQL: {} 执行耗时: {} ms", sql, sqlCost);
                }
                log.info("Update SQL: {} 执行耗时: {} ms", sql, sqlCost);
            }
        }
    }

}    