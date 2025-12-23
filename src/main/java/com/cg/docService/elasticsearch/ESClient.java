package com.cg.docService.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.cg.docService.docs.WasteDocument;
import jakarta.json.JsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.cg.docService.elasticsearch.Utils.applyIf;

/**
 * Author: MIZUGI
 * Date: 2025/11/22
 * Description:
 */
@Component
public class ESClient {
    @Autowired
    ElasticsearchClient client;



    public BulkResponse insertBunch(List<WasteDocument> list) throws IOException {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (WasteDocument doc : list) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("waste")
                            .id(doc.getWasteId().toString())
                            // 设置要索引的文档内容
                            .document(doc)
                    )
            );
        }

        return client.bulk(br.build());
    }

    //更新文档
    public <T> UpdateResponse<T> updateIndex(T doc, String index, String id ,Boolean replaceAll) throws IOException {
        UpdateRequest<T, Object> request = new UpdateRequest.Builder<T, Object>()
                .index(index)
                .id(id)
                .doc(doc)
                .docAsUpsert(replaceAll)// false增量更新ture全量更新
                .build();
        return client.update(request, Object.class);
    }

//添加文档
    public <T> IndexResponse insertOne(T doc, String index,String id) throws IOException {
        IndexRequest<T> request = new IndexRequest.Builder<T>()
                .index(index)
                .id(id)
                .document(doc)
                .build();
        return client.index(request);
    }

    //查询文档通过id
   public <T>  T queryById(String index, String id,Class<T> type) throws IOException {
       GetRequest request = new GetRequest.Builder().index(index).id(id).build();
       GetResponse<T> response = client.get(request, type);
       return response.source();
   }
   //删除文档
    public DeleteResponse deleteById(String index, String id) throws IOException {
        DeleteRequest request = new DeleteRequest.Builder().index(index).id(id).build();
        return client.delete(request);
    }

public  <T> JsonValue search(String index, Integer from , List<Object> cid, Integer size, Double minPrice, String keyword, Double maxPrice, String startDate, String endDate, Class<T> type) throws IOException {
    BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
    //filter不用计算得分，must需要计算得分
    applyIf( minPrice!=null&&maxPrice!=null,boolQueryBuilder,t->t.must(m->m.range(r->r.field("price").gte(JsonData.of(minPrice)).lte(JsonData.of(maxPrice)))));
    applyIf( keyword!=null,boolQueryBuilder,t->t.must(m->m.match(v->v.field("all").query(keyword))));
    applyIf(cid!=null&&!cid.isEmpty(),boolQueryBuilder, t->t.must(m->m.terms(v->v.field("cid").terms(te->te.value(
            cid.stream().map(Object::toString).map(FieldValue::of).toList()
    )))));
    applyIf( startDate!=null,boolQueryBuilder,t->t.filter(m->m.range(r->r.field("create_date").gte(JsonData.of(startDate)))));
    applyIf(endDate != null, boolQueryBuilder, t -> t.filter(m -> m.range(r -> r.field("create_date").lte(JsonData.of(endDate)))));
//    SearchRequest.Builder searchRequest = new SearchRequest.Builder();
//    searchRequest.index(index).from(from).size(size).query(q->q.functionScore(fs->fs.query(qs->qs
//            .bool(boolQueryBuilder.build())).functions(fc->fc.filter(fl->fl.match(m->m.field("name").query("电脑"))).weight(10.0))));
    Highlight highlight = Highlight.of(h -> h
            // 指定要高亮的字段
            .fields("name", f -> f.preTags("<em>").postTags("</em>").requireFieldMatch(false))
            .fields("description", f -> f.preTags("<em>").postTags("</em>").requireFieldMatch(false))
    );
    SearchRequest searchRequest = new SearchRequest.Builder().index(index)
            .from(from).size(size).query(q->q.bool(boolQueryBuilder.build())).highlight(highlight).build();
    JsonpMapper jsonpMapper = client._jsonpMapper();
    JsonData jsonData = JsonData.of(client.search(searchRequest, type));
    return jsonData.toJson(jsonpMapper);

}

//重载
public <T> JsonValue search(String keyword, List<Object> status, String userId,Integer size,Integer from ,Class<T> type) throws IOException {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();
        applyIf(true,boolQueryBuilder,b->b.must(m->m.match(k->k.field("user_id").query(userId))));
        applyIf(keyword!=null,boolQueryBuilder, b->b.must(m->m.match(k->k.field("all").query(keyword))));
        applyIf(status!=null&&!status.isEmpty(),boolQueryBuilder, b->b.must(m->m.terms(v->v.field("status").terms(te->te.value(
                status.stream().map(Object::toString).map(FieldValue::of).toList()
        )))));

    Highlight highlight = Highlight.of(h -> h
            // 指定要高亮的字段
//            requireFieldMatch字段名不需要和keyword字段名一样all字段搜索同样会匹配到address和remark字段
            .fields("address", f -> f.preTags("<em>").postTags("</em>").requireFieldMatch(false))
            .fields("remark", f -> f.preTags("<em>").postTags("</em>").requireFieldMatch(false))
    );
    SearchRequest searchRequest = SearchRequest.of(s->s.size(size).from(from).highlight(highlight).index("request")
            .query(b->b.bool(boolQueryBuilder.build())));

    JsonpMapper jsonpMapper = client._jsonpMapper();
    JsonData jsonData = JsonData.of(client.search(searchRequest, type));
    return jsonData.toJson(jsonpMapper);
}

    public BooleanResponse checkExist(String index, String id) throws IOException {
            ExistsRequest.Builder ex = new ExistsRequest.Builder();
            ex.id(id).index(index);
        return client.exists(ex.build());
    }

}
