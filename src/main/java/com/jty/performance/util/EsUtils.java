package com.jty.performance.util;


import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author: yeting
 * @Date: 2019/8/13 9:16
 */
public class EsUtils {

    private static volatile TransportClient client;

    private static final String CLUSTER_NAME = "elasticsearch";

    private static final String HOST_IP = "192.168.52.128";

    private static final int TCP_PORT = 9300;

    static Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();

    //获取非单例模式得TransportClient对象
    public static TransportClient getClient() {
        try {
            client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return client;
    }

    //双重检查枷锁的TransportClient单例模式实现
    public static TransportClient getSingleClient() {
        if (client == null) {
            synchronized (TransportClient.class) {

            }
            if (client == null) {
                try {
                    client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }
        return client;
    }

    /**
     * 获取索引管理得IndicesAdminClient
     *
     * @return
     */
    public static IndicesAdminClient getAdminClient() {
        return getSingleClient().admin().indices();
    }

    /**
     * 创建索引
     *
     * @param indexName 索引名称
     * @param shards    分片数
     * @param replicas  副本数
     * @return
     */
    public static boolean createIndex(String indexName, int shards, int replicas) {
        Settings settings = Settings.builder()
                .put("index.number_of_shards", shards)
                .put("index.number_of_replicas", replicas)
                .build();
        CreateIndexResponse createIndexResponse = getAdminClient()
                .prepareCreate(indexName.toLowerCase())
                .setSettings(settings)
                .execute().actionGet();
        boolean isIndexCreated = createIndexResponse.isAcknowledged();
        if (isIndexCreated) {
            System.out.println("索引：" + indexName + " 创建成功");
        } else {
            System.out.println("索引：" + indexName + " 创建失败");
        }
        return isIndexCreated;
    }

    /**
     * 删除索引
     *
     * @param indexName 索引名称
     * @return
     */
    public static boolean deleteIndex(String indexName) {
        DeleteIndexResponse deleteResponse = getAdminClient()
                .prepareDelete(indexName.toLowerCase())
                .execute().actionGet();
        boolean isIndexDelete = deleteResponse.isAcknowledged();
        if (isIndexDelete) {
            System.out.println("索引：" + indexName + " 删除成功");
        } else {
            System.out.println("索引：" + indexName + " 删除失败");
        }
        return isIndexDelete;
    }

    /**
     * 创建索引mapping
     *
     * @param indexName 索引名称
     * @param typeName  索引类型
     * @param mapping   mapping设置
     * @return
     */
    public static boolean setMapping(String indexName, String typeName, String mapping) {
        PutMappingResponse putMappingResponse = getAdminClient()
                .preparePutMapping(indexName)
                .setType(typeName)
                .setSource(mapping, XContentType.JSON).get();
        boolean isMappinged = putMappingResponse.isAcknowledged();
        if (isMappinged) {
            System.out.println("索引：" + indexName + " 创建mapping成功");
        } else {
            System.out.println("索引：" + indexName + " 创建mapping失败");
        }
        return isMappinged;
    }

}
