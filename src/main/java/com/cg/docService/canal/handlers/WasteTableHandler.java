package com.cg.docService.canal.handlers;

import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.cg.docService.canal.CanalMessageHandler;
import com.cg.docService.docs.WasteDocument;
import com.cg.docService.elasticsearch.ESClient;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cg.docService.elasticsearch.Utils.moveStr;


@Slf4j
@Component
public class WasteTableHandler implements CanalMessageHandler {

    private static final String TABLE_NAME = "waste";
    @Autowired
    ESClient esClient;

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
    private void handleInsert(List<CanalEntry.RowData> rowDatas) throws IOException {
        List<WasteDocument> wastes = Lists.newArrayList();

        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, String> dataMap = rowData.getAfterColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            WasteDocument waste = JSON.parseObject(JSON.toJSONString(dataMap), WasteDocument.class);
            wastes.add(waste);
        }

        if (!CollectionUtils.isEmpty(wastes)) {
            log.info("插入数据：{}", wastes);
            List<WasteDocument> wasteDocuments = Lists.newArrayList();
            WasteDocument wasteDocument = new WasteDocument();
    wastes.forEach(w->{
        //把waste对象转换为wasteDocument对象
        BeanUtils.copyProperties(w, wasteDocument);
        wasteDocuments.add(wasteDocument);
    });

            BulkResponse result = esClient.insertBunch(wasteDocuments);
            if (result.errors()) {
                System.out.println("批量操作中存在错误：");
                result.items().forEach(item -> {
                    if (item.error() != null) {
                        System.out.println("文档 ID: " + item.id() + " 操作失败: " + item.error().reason());
                    }
                });
            } else {
                System.out.println("所有文档批量索引成功！");
                result.items().forEach(item -> {
                    System.out.println("文档 ID: " + item.id() + " 已成功索引，版本: " + item.version());
                });
            }


        }
    }

    /**
     * 处理更新事件
     *
     * @param rowDatas 行数据列表
     */
    private void handleUpdate(List<CanalEntry.RowData> rowDatas) throws IOException {
        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, Object> beforeMap = rowData.getBeforeColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            Map<String, Object> afterMap = rowData.getAfterColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            WasteDocument beforeWaste = JSON.parseObject(JSON.toJSONString(beforeMap), WasteDocument.class);
            WasteDocument afterWaste = JSON.parseObject(JSON.toJSONString(afterMap), WasteDocument.class);

            log.info("更新前：{}，更新后：{}",
                    JSON.toJSONString(beforeWaste),
                    JSON.toJSONString(afterWaste));
            UpdateResponse<WasteDocument> response = esClient.updateIndex(afterWaste, "waste",afterWaste.getWasteId().toString(), true);
            log.info("更新结果：{}", JSON.toJSON(moveStr(response.toString())));
        }
    }

    /**
     * 处理删除事件
     *
     * @param rowDatas 行数据列表
     */
    private void handleDelete(List<CanalEntry.RowData> rowDatas) throws IOException {
        List<WasteDocument> wastes = Lists.newArrayList();

        for (CanalEntry.RowData rowData : rowDatas) {
            Map<String, String> dataMap = rowData.getBeforeColumnsList().stream()
                    .collect(Collectors.toMap(CanalEntry.Column::getName, CanalEntry.Column::getValue));

            WasteDocument waste = JSON.parseObject(JSON.toJSONString(dataMap), WasteDocument.class);
            wastes.add(waste);
        }

        if (!CollectionUtils.isEmpty(wastes)) {
            log.info("删除：{}", JSON.toJSONString(wastes));
            for (WasteDocument waste : wastes) {
                DeleteResponse res = esClient.deleteById("waste",waste.getWasteId().toString());
                log.info("成功移除：{}", JSON.toJSON(moveStr(res.toString())));
            }
        }
    }
}