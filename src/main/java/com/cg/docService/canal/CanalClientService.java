package com.cg.docService.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Canal客户端服务
 *
 * @author ken
 */
@Slf4j
@Service
public class CanalClientService {

    private final CanalConnector canalConnector;
    private final List<CanalMessageHandler> messageHandlers;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private volatile boolean running = false;

    @Autowired
    public CanalClientService(CanalConnector canalConnector, List<CanalMessageHandler> messageHandlers) {
        this.canalConnector = canalConnector;
        this.messageHandlers = messageHandlers;
    }

    /**
     * 启动Canal客户端
     */
    public void start() {
        if (running) {
            log.warn("Canal客户端已经在运行中");
            return;
        }

        running = true;
        executorService.execute(this::process);
        log.info("Canal客户端启动成功");
    }

    /**
     * 停止Canal客户端
     */
    public void stop() {
        if (!running) {
            log.warn("Canal客户端已经停止");
            return;
        }

        running = false;
        executorService.shutdown();
        canalConnector.disconnect();
        log.info("Canal客户端停止成功");
    }

    /**
     * 处理Canal消息
     */
    private void process() {
        int batchSize = 1000;
        try {
            // 连接Canal服务端
            canalConnector.connect();
            // 订阅所有消息
            canalConnector.subscribe();
            // 回滚到上次处理成功的位置
            canalConnector.rollback();

            while (running) {
                // 获取消息
                Message message = canalConnector.getWithoutAck(batchSize);
//                log.info("{}",message);
                long batchId = message.getId();
                int size = message.getEntries().size();

                if (batchId == -1 || size == 0) {
                    // 没有消息，休眠一段时间
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // 忽略中断异常
                    }
                } else {
                    // 处理消息
                    processMessage(message.getEntries());
                    // 确认消息已处理
                    canalConnector.ack(batchId);
                }
            }
        } catch (Exception e) {
            log.error("Canal客户端处理消息异常", e);
            try {
                // 发生异常时回滚
                canalConnector.rollback();
            } catch (Exception ex) {
                log.error("Canal客户端回滚异常", ex);
            }
        } finally {
            canalConnector.disconnect();
        }
    }

    /**
     * 处理消息条目
     *
     * @param entries 消息条目列表
     */
    private void processMessage(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            // 忽略事务开始和结束的消息
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN ||
                entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange;
            try {
                // 解析消息内容
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                log.error("解析Canal消息失败，entry: {}", entry, e);
                continue;
            }

            // 获取数据库名和表名
            String databaseName = entry.getHeader().getSchemaName();
            String tableName = entry.getHeader().getTableName();
            CanalEntry.EventType eventType = rowChange.getEventType();

            log.info("接收到数据变更，数据库：{}，表名：{}，事件类型：{}",
                databaseName, tableName, eventType);

            // 处理行数据变更
            List<CanalEntry.RowData> rowDatas = rowChange.getRowDatasList();
            if (!CollectionUtils.isEmpty(rowDatas) && !CollectionUtils.isEmpty(messageHandlers)) {
                for (CanalMessageHandler handler : messageHandlers) {
                    try {
                        handler.handle(tableName, eventType, rowDatas);
                    } catch (Exception e) {
                        log.error("处理Canal消息异常，handler: {}", handler.getClass().getName(), e);
                    }
                }
            }
        }
    }
}