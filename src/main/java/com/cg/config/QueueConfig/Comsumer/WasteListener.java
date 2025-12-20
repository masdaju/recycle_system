//package com.cg.config.QueueConfig.Comsumer;
//
//import co.elastic.clients.elasticsearch.core.IndexResponse;
//import co.elastic.clients.elasticsearch.core.UpdateResponse;
//import com.cg.docService.elasticsearch.ESClient;
//import com.cg.entity.WasteRequests;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.List;
//
///**
// * Author: MIZUGI
// * Date: 2025/11/28
// * Description:
// */
//@Slf4j
//@Component
//public class WasteListener {
//    @Autowired
//    ESClient esClient;
//    @RabbitListener(queues = "insertQueue")
//    public void insert(WasteRequests params) throws IOException {
//        List<Long> wid = params.getWid();
//        if (esClient.checkExist("request", params.getRequestId().toString()).value()) {
//            UpdateResponse<WasteRequests> request = esClient.updateIndex(new WasteRequests().setWid(wid), "request", params.getRequestId().toString(), false);
//            log.info("更新成功{}",request);
//        }
//    }
//
//    @RabbitListener(queues = "deleteQueue")
//    public void delete(WasteRequests params){
//
//    }
//}
