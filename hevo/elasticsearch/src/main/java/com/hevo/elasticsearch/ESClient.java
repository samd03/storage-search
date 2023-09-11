package com.hevo.elasticsearch;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.BulkByScrollTask;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;

@Slf4j
public class ESClient {

    private final RestHighLevelClient client;
    private final ThreadPoolExecutor writePool;
    private final ThreadPoolExecutor readPool;
    private final ElasticSearchConfiguration esConfig;


    private static Map<String, ESClient> clientMap = new HashMap<>();
    Gson gson = new Gson();

    private ESClient(ElasticSearchConfiguration esConfig) {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(esConfig.getUsername(), esConfig.getPassword()));

        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{TrustAllConfig.TRUST_MANAGER}, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }


        HttpHost host = new HttpHost(esConfig.getUrl(), esConfig.getPort(), esConfig.getScheme());

        SSLContext finalContext = context;
        RestClientBuilder builder = RestClient.builder(host)
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setSSLHostnameVerifier((s, sslSession) -> true)
                                .setDefaultCredentialsProvider(credentialsProvider)
                                .setSSLContext(finalContext));

        this.client = new RestHighLevelClient(builder);
        String name = esConfig.getName();
        this.readPool = get(String.format("esread-%s", name),
                esConfig.getReadThreadPoolSize(), 10000);
        this.writePool = get(String.format("eswrite-%s", name),
                esConfig.getWriteThreadPoolSize(), 10000);
        this.esConfig = esConfig;



    }

    public static ThreadPoolExecutor get(String prefix, int parallelism, int threadPoolQueueSize) {
        ThreadFactory threadFactory = (new ThreadFactoryBuilder()).setDaemon(true).setNameFormat(prefix + "-%d").build();
        return new ThreadPoolExecutor(parallelism, parallelism, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(threadPoolQueueSize), threadFactory);
    }

    public static ESClient getInstance(ElasticSearchConfiguration esConfig) {
        return clientMap.computeIfAbsent(esConfig.getName(), n -> new ESClient(esConfig));
    }

    public SearchResponse search(SearchRequest searchRequest) {
        SearchResponse searchResponse;
        try {
            log.debug("The source query is: {}", searchRequest.source().toString());
            searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to retrieve results for: %s",
                    searchRequest.source().toString()), e);
        }
        RestStatus status = searchResponse.status();
        if (status == RestStatus.OK) {
            return searchResponse;
        }
        throw new RuntimeException(String.format("Unable to retrieve results for: %s",
                searchRequest.source().toString()));
    }

    public String index(EsIndexRequest esIndexRequest) {

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.type("_doc");
        indexRequest.routing(esIndexRequest.getRouting());
        indexRequest.source(gson.toJson(esIndexRequest.getSource()), XContentType.JSON);
        indexRequest.index(esIndexRequest.getIndex());
        indexRequest.id(esIndexRequest.getId());

        IndexResponse indexResponse = null;
        try {
            indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to index: %s",
                    indexRequest.source().toString()), e);
        }
        RestStatus status = indexResponse.status();
        if (status == RestStatus.CREATED || status == RestStatus.OK) {
            return indexResponse.getId();
        }
        throw new RuntimeException(String.format("Unable to index: %s",
                indexRequest.source().toString()));
    }
    public void delete(DeleteByQueryRequest deleteRequest) {
        try {
            client.deleteByQuery(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}