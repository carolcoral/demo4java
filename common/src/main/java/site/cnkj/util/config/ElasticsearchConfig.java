package site.cnkj.util.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.client.sniff.SnifferBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Objects;


/**
 * ES操作的客户端，5.6版本
 * 直接在需要的地方使用下面的方式即可使用
 *
 * @Resource(name = "HighLevelESClient")
 * private RestHighLevelClient client;
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "common.elasticsearch")
@ConditionalOnProperty(prefix = "common.elasticsearch", name = "clusterNodes")
public class ElasticsearchConfig {

    private String[] clusterNodes;
    private String username;
    private String password;
    private Integer snifferinterval = 60000;
    private Integer maxretrytimeout = 30000; //带超时时间式(毫秒级)
    private Integer failuredelay = 3000;
    private Integer connectTimeout = 5000;
    private Integer socketTimeout = 60000;
    private Integer maxRetryTimeoutMillis = 60000;

    private static final int ADDRESS_LENGTH = 2;

    private static final String HTTP_SCHEME = "http";

    private HttpHost makeHttpHost(String s) {
        assert StringUtils.isNotEmpty(s);
        String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }

    @Bean(name = "HighESClient")
    public RestClient restTomcatClient() {
        HttpHost[] hosts = Arrays.stream(clusterNodes)
                .map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        //自动扫描网段
        //监听同网段服务
        //Low Level Client init
        RestClientBuilder builder = RestClient.builder(hosts).setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        ).setRequestConfigCallback((RequestConfig.Builder build) -> {
            build.setConnectTimeout(connectTimeout);
            build.setSocketTimeout(socketTimeout);
            return build;
        });
        builder.setMaxRetryTimeoutMillis(maxRetryTimeoutMillis);
        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
        builder.setFailureListener(sniffOnFailureListener);
        RestClient lowLevelRestClient = builder.build();
        SnifferBuilder snifferBuilder = Sniffer.builder(lowLevelRestClient).setSniffIntervalMillis(snifferinterval);
        if (failuredelay > 0) {
            snifferBuilder.setSniffAfterFailureDelayMillis(failuredelay);
        }
        sniffOnFailureListener.setSniffer(snifferBuilder.build());
        return lowLevelRestClient;
    }

    /**
     * 具备自动扫描以及多节点配置功能的客户端
     * @param restClient
     * @return
     */
    @Bean(name = "HighLevelESClient")
    public RestHighLevelClient restHighLevelClient(@Qualifier("HighESClient") RestClient restClient) {
        return new RestHighLevelClient(restClient);
    }

    /**
     * 只扫描配置节点的客户端
     * @return
     */
    @Bean(name = "HighLevelClient")
    public RestHighLevelClient restClientNoSniff(){
        String address = clusterNodes[0];
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                //es账号密码
                new UsernamePasswordCredentials(username, password));
        //Low Level Client init
        RestClient lowLevelRestClient = RestClient.builder(
                new HttpHost(address.split(":")[0], Integer.valueOf(address.split(":")[1]), HTTP_SCHEME)
        ).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        }).build();
        //High Level Client init
        RestHighLevelClient client = new RestHighLevelClient(lowLevelRestClient);
        return client;
    }

}
