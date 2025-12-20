package com.cg.docService.canal.handlers;

import co.elastic.clients.elasticsearch.core.DeleteResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.cg.docService.canal.CanalMessageHandler;
import com.cg.docService.docs.RequestDocument;
import com.cg.docService.elasticsearch.ESClient;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cg.docService.elasticsearch.Utils.moveStr;

/**
 * 用户表数据变更处理器
 *
 * @author ken
 */
@Slf4j
@Component
public class RequestTableHandler implements CanalMessageHandler {
    @Autowired
    ESClient esClient;
    private static final String TABLE_NAME = "waste_requests";
    @Override
    public void handle(String tableName, CanalEntry.EventType eventType, List<CanalEntry.RowData> rowDatas) throws IOException {
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
        List<RequestDocument> requestDocuments = Lists.newArrayList();

        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, String> dataMap = rowData.getAfterColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            RequestDocument requestDocument = JSON.parseObject(JSON.toJSONString(dataMap), RequestDocument.class);
            requestDocuments.add(requestDocument);
        }

        if (!CollectionUtils.isEmpty(requestDocuments)) {
            log.info("新增用户：{}", JSON.toJSONString(requestDocuments));
            // 这里可以添加同步到缓存、搜索等业务逻辑
            requestDocuments.forEach(item->{
                try {
                    esClient.updateIndex(item,"request", item.getRequestId().toString(),true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * 处理更新事件
     *
     * @param rowDatas 行数据列表
     */
    private void handleUpdate(List<CanalEntry.RowData> rowDatas) throws IOException {
        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, String> beforeMap = rowData.getBeforeColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
            Map<String, String> afterMap = rowData.getAfterColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));
            RequestDocument beforeRequest = JSON.parseObject(JSON.toJSONString(beforeMap), RequestDocument.class);
            RequestDocument afterRequest = JSON.parseObject(JSON.toJSONString(afterMap), RequestDocument.class);

            log.info("更新用户，更新前：{}，更新后：{}",
                    JSON.toJSONString(beforeRequest),
                    JSON.toJSONString(afterRequest));
            if (esClient.checkExist("request", afterRequest.getRequestId().toString()).value()) {
                esClient.updateIndex(afterRequest,"request", afterRequest.getRequestId().toString(),false);
            }
            esClient.updateIndex(afterRequest,"request", afterRequest.getRequestId().toString(),true);
        }
    }

    /**
     * 处理删除事件
     *
     * @param rowDatas 行数据列表
     */
    private void handleDelete(List<CanalEntry.RowData> rowDatas) throws IOException {
        List<RequestDocument> documentArrayList = Lists.newArrayList();

        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, String> dataMap = rowData.getBeforeColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            RequestDocument requestDocument = JSON.parseObject(JSON.toJSONString(dataMap), RequestDocument.class);
            documentArrayList.add(requestDocument);
        }

        if (!CollectionUtils.isEmpty(documentArrayList)) {
            log.info("删除：{}", JSON.toJSONString(documentArrayList));
            for (RequestDocument requestDocument : documentArrayList) {
                DeleteResponse res = esClient.deleteById("waste",requestDocument.getRequestId().toString());
                log.info("成功移除：{}", JSON.toJSON(moveStr(res.toString())));
            }
        }
    }
}