package com.hevo.dao;

import com.google.gson.Gson;
import com.hevo.Utility;
import com.hevo.elasticsearch.ESClient;
import com.hevo.elasticsearch.EsIndexRequest;
import com.hevo.entity.EsFileEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.management.Query;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.join.query.JoinQueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EsFileDao {
    @Autowired
    ESClient esClient;

    private String INDEX = "files";
    public String save(EsFileEntity entity) {
        EsIndexRequest request = buildIndexRequest(entity, entity.getJoinField().getParent());
        return esClient.index(request);
    }

    public void delete(String parentId) {
        QueryBuilder q = QueryBuilders.boolQuery()
                .should( QueryBuilders.termQuery("_id", parentId))
                .should(JoinQueryBuilders.hasParentQuery("file",  QueryBuilders.termQuery("_id", parentId), false))
                .minimumShouldMatch(1);
//        DeleteByQueryRequest request = new DeleteByQueryRequest(INDEX);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(new SearchSourceBuilder().query(q));
        searchRequest.indices(INDEX);
        searchRequest.routing(parentId);
        DeleteByQueryRequest deleteRequest = new DeleteByQueryRequest(searchRequest);


        esClient.delete(deleteRequest);
    }

    public List<EsFileEntity> searchByQuery(String q) {
        QueryBuilder fileQ = QueryBuilders.matchQuery("absolutePath", q);
        QueryBuilder recordQ = JoinQueryBuilders.hasChildQuery("record",
                QueryBuilders.matchQuery("record.content", q), ScoreMode.Total);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(QueryBuilders.boolQuery()
                .should(fileQ)
                .should(recordQ));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        searchRequest.indices(INDEX);
        SearchResponse searchResponse = null;
        try {
            searchResponse = esClient.search(searchRequest);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        return getEntity(searchResponse);
    }

    public Optional<EsFileEntity> search(String absoluteFilePath) {
        QueryBuilder query = QueryBuilders.matchQuery("absolutePath", absoluteFilePath);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder().query(query);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(sourceBuilder);
        searchRequest.indices(INDEX);
        SearchResponse searchResponse = null;
        try {
            searchResponse = esClient.search(searchRequest);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        List<EsFileEntity> list = getEntity(searchResponse);
        return list.size() > 0 ? Optional.of(list.get(0)) : Optional.empty();
    }

    private List<EsFileEntity> getEntity(SearchResponse searchResponse) {
        List<EsFileEntity> list = new ArrayList<>();
        if (searchResponse != null && searchResponse.getHits() != null && searchResponse.getHits().getHits() != null) {
            for (SearchHit searchHit : searchResponse.getHits().getHits()) {
                String hitStr = searchHit.getSourceAsString();
                EsFileEntity entity = Utility.fromJson(EsFileEntity.class, hitStr);
                entity.setId(searchHit.getId());
                list.add(entity);
            }
        }
        return list;
    }

    private EsIndexRequest buildIndexRequest(EsFileEntity entity, String routing) {
        return EsIndexRequest.builder()
                .index(INDEX)
                .source(entity)
                .routing(routing)
                .id(UUID.randomUUID().toString())
                .build();
    }
}
