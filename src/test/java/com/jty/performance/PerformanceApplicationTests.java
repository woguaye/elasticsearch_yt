package com.jty.performance;

import com.jty.performance.domain.news.News;
import com.jty.performance.repository.NewsRepository;
import com.jty.performance.util.EsUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PerformanceApplicationTests {

    @Autowired
    private NewsRepository newsRepository;

    @Test
    public void contextLoads() {
        TransportClient client = EsUtils.getClient();
        GetResponse getResponse = client.prepareGet("blog", "article", "1").get();
        String result = getResponse.getSourceAsString();
        System.out.println(result);
        System.out.println(getResponse.getSourceAsMap());
        System.out.println(client);
    }

    @Test
    public void testOne() {
        TransportClient clientOne = EsUtils.getClient();
        TransportClient clientTwo = EsUtils.getClient();
        System.out.println(clientOne);
        System.out.println(clientTwo);
    }

    @Test
    public void testTwo() {
        TransportClient singleClientOne = EsUtils.getSingleClient();
        TransportClient singleClientTwo = EsUtils.getSingleClient();
        System.out.println(singleClientOne);
        System.out.println(singleClientTwo);
    }

    /**
     * 创建索引测试
     */
    @Test
    public void createIndex() {
        boolean jfjx = EsUtils.createIndex("jfjx", 3, 0);
        System.out.println(jfjx);
    }

    /**
     * 删除索引
     */
    @Test
    public void deleteIndex() {
        boolean jfjx = EsUtils.deleteIndex("jfjx");
        System.out.println(jfjx);
    }

    /**
     * 设置mapping
     */
    @Test
    public void setingMapping() {
        try {
            XContentBuilder builder = jsonBuilder().startObject()
                    .startObject("properties")
                    .startObject("user")
                    .field("type", "text")
                    .endObject()
                    .startObject("postDate")
                    .field("type", "date")
                    .endObject()
                    .startObject("message")
                    .field("type", "text")
                    .endObject()
                    .endObject()
                    .endObject();

            System.out.println("==============================" + builder.string());
            boolean b = EsUtils.setMapping("jfjx", "jf", builder.string());
            System.out.println(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向索引中添加文本方式一
     */
    @Test
    public void putIndexOne() {
        HashMap<String, String> json = new HashMap<>();
        json.put("user", "tom");
        json.put("postDate", "2014-05-20");
        json.put("message", "Tom trying out Elasticsearch");
        IndexResponse indexResponse = EsUtils.getSingleClient().prepareIndex("jfjx", "jf", "1")
                .setSource(json, XContentType.JSON)
                .get();
        RestStatus status = indexResponse.status();
        System.out.println("=========================" + status.getStatus());
    }

    /**
     * 向索引中添加文本方式二
     */
    @Test
    public void putIndexTwo() {

        try {
            XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field("user", "Tonny")
                    .field("postDate", "2017-05-20")
                    .field("message", "Tonny trying out Elasticsearch")
                    .endObject();
            String json = builder.string();
            IndexResponse indexResponse = EsUtils.getSingleClient().prepareIndex("jfjx", "jf", "2")
                    .setSource(json, XContentType.JSON)
                    .get();
            RestStatus status = indexResponse.status();
            System.out.println("=========================" + status.getStatus());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取对应索引的文档
     */
    @Test
    public void findIndexByWord() {
        GetResponse getResponse = EsUtils.getSingleClient().prepareGet("jfjx", "jf", "1")
                .execute().actionGet();
        System.out.println(getResponse.getSourceAsString());
        System.out.println(getResponse.getSourceAsMap());
    }

    /**
     * 更新索引文档测试
     */
    @Test
    public void updateIndexByDoum() {
        HashMap<String, String> json = new HashMap<>();
        json.put("user", "tom");
        json.put("postDate", "2014-05-20");
        json.put("message", "new message");
        UpdateResponse updateResponse = EsUtils.getSingleClient().prepareUpdate("jfjx", "jf", "1")
                .setDoc(json)
                .get();
        RestStatus status = updateResponse.status();
        System.out.println("======================" + status.getStatus());
    }

    /**
     * 删除文档
     */
    @Test
    public void deleteDoum() {
        DeleteResponse response = EsUtils.getSingleClient().prepareDelete("jfjx", "jf", "1")
                .execute()
                .actionGet();
        System.out.println("========================================" + response.status().getStatus());
    }

    /**
     * 搜索文档测试
     */
    @Test
    public void searchByTrem() {
        TermQueryBuilder termQuery = QueryBuilders.termQuery("user", "tonny");
        SearchResponse searchResponse = EsUtils.getSingleClient().prepareSearch("jfjx")
                .setQuery(termQuery)
                .setTypes("jf")
                .execute()
                .actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("=========================" + hits.totalHits);
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsString());
            System.out.println(hit.getScore());
        }

    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //实现新闻字段查找
    @Test
    public void createIndexNews() {
        EsUtils.createIndex("spnews", 3, 0);
    }

    /**
     * 设置索引mapping
     */
    @Test
    public void setingNewsMapping() {
        try {
            XContentBuilder builder = jsonBuilder().startObject()
                    .startObject("properties")
                    .startObject("id")
                    .field("type", "long")
                    .endObject()
                    .startObject("created_time")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd")
                    .endObject()
                    .startObject("modified_time")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd")
                    .endObject()
                    .startObject("content")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .field("search_analyzer", "ik_max_word")
                    .field("boost", "1")
                    .endObject()
                    .startObject("key_word")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .field("search_analyzer", "ik_max_word")
                    .endObject()
                    .startObject("postdate")
                    .field("type", "date")
                    .field("format", "yyyy-MM-dd HH:mm:ss")
                    .endObject()
                    .startObject("reply")
                    .field("type", "long")
                    .endObject()
                    .startObject("source")
                    .field("type", "keyword")
                    .endObject()
                    .startObject("title")
                    .field("type", "text")
                    .field("analyzer", "ik_max_word")
                    .field("search_analyzer", "ik_max_word")
                    .field("boost", "2")
                    .endObject()
                    .startObject("url")
                    .field("type", "keyword")
                    .endObject()
                    .endObject()
                    .endObject();

            System.out.println("==============================" + builder.string());
            boolean b = EsUtils.setMapping("spnews", "news", builder.string());
            System.out.println(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将mysql同步到es中
     */
    @Test
    public void insertDoumToIndex() {
        TransportClient client = EsUtils.getSingleClient();
        List<News> all = newsRepository.findAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<String, Object> json = new HashMap<>();
        for (News news : all) {
            json.put("id", news.getId());
            json.put("created_time", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(news.getCreatedTime()));
            json.put("modified_time", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(news.getModifiedTime()));
            json.put("content", news.getContent());
            json.put("key_word", news.getKeyWord());
            json.put("postdate", dateFormat.format(news.getPostdate()));
            json.put("reply", news.getReply());
            json.put("source", news.getSource());
            json.put("title", news.getTitle());
            json.put("url", news.getUrl());
            System.out.println("=================================" + json);
            client.prepareIndex("spnews", "news", String.valueOf(news.getId()))
                    .setSource(json)
                    .execute()
                    .actionGet();
        }

    }


    @Test
    public void test10() {
        searchSpnewsByKey("足球", 2);
    }

    /**
     * 测试前端输入关键字直接从es中查询出结果，结果以分页的数据结构返回数据
     *
     * @param query   查询关键字
     * @param pageNum 分页页码
     */
    public void searchSpnewsByKey(String query, int pageNum) {
        long start = System.currentTimeMillis();
        //支持对多个字段查询
        MultiMatchQueryBuilder multiQuery = QueryBuilders.multiMatchQuery(query, "title", "content");

        //设置高亮字段
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .preTags("<span style=\\\"color:red\\\">")
                .postTags("</span>")
                .field("title")
                .field("content");

        SearchResponse searchResponse = EsUtils.getSingleClient().prepareSearch("spnews")
                .setTypes("news")
                .setQuery(multiQuery)
                .highlighter(highlightBuilder)
                .setFrom((pageNum - 1) * 10)//该操作可以实现分页操作
                .setSize(10)
                .execute()
                .actionGet();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> news = hit.getSourceAsMap();
            HighlightField hTitle = hit.getHighlightFields().get("title");
            if (hTitle != null) {
                Text[] fragments = hTitle.fragments();
                String hTitleStr = "";
                for (Text text : fragments) {
                    hTitleStr += text;
                }
                news.put("title", hTitleStr);
            }

            HighlightField hConten = hit.getHighlightFields().get("content");
            if (hConten != null) {
                Text[] fragments1 = hConten.fragments();
                String hContenStr = "";
                for (Text text : fragments1) {
                    hContenStr += text;
                }
                news.put("content", hContenStr);
            }

            System.out.println(news);
        }
        long end = System.currentTimeMillis();
        System.out.println("搜索到的总数：" + hits.getTotalHits());
        System.out.println("搜索耗时：" + (end - start));

    }
}
