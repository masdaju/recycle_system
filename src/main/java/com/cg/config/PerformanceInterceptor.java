package com.cg.config;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class PerformanceInterceptor implements InnerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);

    @Override
    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        long startTime = System.currentTimeMillis();
        try {
            return true;
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCost = endTime - startTime;
            String sql = boundSql.getSql();
            logger.info("Query SQL: {} 执行耗时: {} ms", sql, sqlCost);
        }
    }

    @Override
    public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        long startTime = System.currentTimeMillis();
        BoundSql boundSql = ms.getBoundSql(parameter);
        try {
            return true;
        } finally {
            long endTime = System.currentTimeMillis();
            long sqlCost = endTime - startTime;
            String sql = boundSql.getSql();
            logger.info("Update SQL: {} 执行耗时: {} ms", sql, sqlCost);
        }
    }
}    