package com.cg.docService.canal.handlers;

import com.alibaba.fastjson2.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.cg.docService.canal.CanalMessageHandler;
import com.cg.entity.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Component
public class UserTableHandler implements CanalMessageHandler {

    private static final String TABLE_NAME = "sys_user";

    @Override
    public void handle(String tableName, CanalEntry.EventType eventType, List<CanalEntry.RowData> rowDatas) {
        // 只处理用户表的变更
        if (!TABLE_NAME.equals(tableName)) {
            return;
        }

        log.info("开始处理用户表变更，事件类型：{}，变更行数：{}", eventType, rowDatas.size());

        switch (eventType) {
            case INSERT:
                handleInsert(rowDatas);
                break;
            case UPDATE:
                handleUpdate(rowDatas);
                break;
            case DELETE:
                handleDelete(rowDatas);
                break;
            default:
                log.info("不处理的事件类型：{}", eventType);
        }
    }

    /**
     * 处理插入事件
     *
     * @param rowDatas 行数据列表
     */
    private void handleInsert(List<CanalEntry.RowData> rowDatas) {
        List<User> users = Lists.newArrayList();

        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, String> dataMap = rowData.getAfterColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            User user = JSON.parseObject(JSON.toJSONString(dataMap), User.class);
            users.add(user);
        }

        if (!CollectionUtils.isEmpty(users)) {
            log.info("新增用户：{}", JSON.toJSONString(users));
            // 这里可以添加同步到缓存、搜索等业务逻辑
        }
    }

    /**
     * 处理更新事件
     *
     * @param rowDatas 行数据列表
     */
    private void handleUpdate(List<CanalEntry.RowData> rowDatas) {
        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, String> beforeMap = rowData.getBeforeColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            Map<String, String> afterMap = rowData.getAfterColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            User beforeUser = JSON.parseObject(JSON.toJSONString(beforeMap), User.class);
            User afterUser = JSON.parseObject(JSON.toJSONString(afterMap), User.class);

            log.info("更新用户，更新前：{}，更新后：{}",
                    JSON.toJSONString(beforeUser),
                    JSON.toJSONString(afterUser));
            // 这里可以添加同步到缓存、搜索等业务逻辑
        }
    }

    /**
     * 处理删除事件
     *
     * @param rowDatas 行数据列表
     */
    private void handleDelete(List<CanalEntry.RowData> rowDatas) {
        List<User> users = Lists.newArrayList();

        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, String> dataMap = rowData.getBeforeColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            User user = JSON.parseObject(JSON.toJSONString(dataMap), User.class);
            users.add(user);
        }

        if (!CollectionUtils.isEmpty(users)) {
            log.info("删除用户：{}", JSON.toJSONString(users));
            // 这里可以添加从缓存、搜索等删除的业务逻辑
        }
    }
}