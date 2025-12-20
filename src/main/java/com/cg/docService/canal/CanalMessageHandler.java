package com.cg.docService.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;

import java.io.IOException;
import java.util.List;

/**
 * Canal消息处理器接口
 *
 * @author ken
 */
public interface CanalMessageHandler {

    /**
     * 处理Canal消息
     *
     * @param tableName 表名
     * @param eventType 事件类型
     * @param rowDatas 行数据列表
     */
    void handle(String tableName, CanalEntry.EventType eventType, List<CanalEntry.RowData> rowDatas) throws IOException;
}