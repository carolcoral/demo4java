package site.cnkj.es.service;

/*
 * @version 1.0 created by LXW on 2019/1/8 16:29
 */
public interface ElasticSearchService {

    /**
     * 查询es数据并发送到kafka
     * @param index 索引
     * @param filterKey 关键字
     * @param filterValue 关键字的值
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 执行状态
     */
    boolean queryDataToKafka(String index, String filterKey, String filterValue, String startTime, String endTime);


    /**
     * 通过scrollId从ES获取数据
     * @param scrollId
     * @return
     */
    boolean queryDataToKafkaById(String scrollId);

}
