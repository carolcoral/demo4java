package site.cnkj.util;

import site.cnkj.util.service.CommonInfo;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rx on 2018/9/30.
 */
public class RestTemplateUtil {

    public static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateUtil.class);

    private RestTemplate restTemplate;

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 获取空闲的服务url
     * 需要配合 actuate 包检查服务健康状态一起使用
     * 设置参数在 site.cnkj.util.service.CommonInfo 中
     *
     * @param urlList url集合
     * @return 空闲的url
     */
    public String getFreeUrl(List<String> urlList) {
        for (String url : urlList) {
            try {
                JSONObject jsonObject = restTemplate.getForObject(url + CommonConstant.API.COMMON_INFO, JSONObject.class);
                if (CommonInfo.VACANCY.equals(jsonObject.get("CURRENT_STATUS"))) {
                    return url;
                } else {
                    return null;
                }
            } catch (Exception e) {
                LOGGER.error("获取COMMON_INFO解析失败", e);
                continue;
            }
        }
        return null;
    }

    /**
     * 获取空闲的服务url 过期时间,循环次数
     *
     * @param urlList url集合
     * @param timeout 单次等待时长
     * @param retry   循环次数
     * @return null?null:url
     */
    public String getFreeUrl(List<String> urlList, Integer timeout, Integer retry) {
        Integer retry_now = 0;
        Integer finalRetry = retry < 3 ? 3 : retry;
        String freeUrl = null;
        while (true) {
            if (retry_now < finalRetry) {
                freeUrl = getFreeUrl(urlList);
                if (freeUrl != null) {
                    return freeUrl;
                } else if (freeUrl == null) {
                    try {
                        Thread.sleep(timeout);
                        retry_now++;
                        continue;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                return freeUrl;
            }
        }
    }


    /**
     * post 请求object对象
     *
     * @param object 参数
     * @param url    地址
     * @param cls    对象
     * @param <T>    泛型
     * @return
     */
    public <T> T postVieObjectReturn(Object object, String url, Class<T> cls) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(object, headers);
        try {
            ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, requestEntity, cls);
            return responseEntity.getBody();
        } catch (Exception e) {
            LOGGER.error("postVieObjectReturn", e);
        }
        return null;
    }

    /**
     * post 请求form对象
     *
     * @param multiValueMap 参数
     * @param url           地址
     * @param cls           对象
     * @param <T>           泛型
     * @return
     */
    public <T> T postVieFormReturn(MultiValueMap<String, Object> multiValueMap, String url, Class<T> cls) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(multiValueMap, headers);
        try {
            ResponseEntity<T> responseEntity = restTemplate.postForEntity(url, requestEntity, cls);
            return responseEntity.getBody();
        } catch (Exception e) {
            LOGGER.error("postVieFormReturn请求失败", e);
        }
        return null;
    }

    /**
     * get 请求，带参数
     *
     * @param url 地址
     * @param map 参数
     * @param cls 对象
     * @param <T> 泛型
     * @return
     */
    public <T> T get(String url, Class<T> cls, Map map) {
        try {
            ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, cls, map);
            return responseEntity.getBody();
        } catch (Exception e) {
            LOGGER.error("get请求失败", e);
        }
        return null;
    }

    /**
     * get 请求，不带参数
     *
     * @param url 地址
     * @param cls 对象
     * @param <T> 泛型
     * @return
     */
    public <T> T get(String url, Class<T> cls) {
        try {
            ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, cls);
            return responseEntity.getBody();
        } catch (Exception e) {
            LOGGER.error("get请求失败", e);
        }
        return null;
    }

    /**
     * get 请求，拼接地址
     *
     * @param url  地址
     * @param args 参数
     * @param cls  对象
     * @param <T>  泛型
     * @return
     */
    public <T> T getWithQ(String url, Class<T> cls, Map<String, Object> args) {
        try {
            String params = new String();
            for (String arg : args.keySet()) {
                params = params + arg + "=" + args.get(arg) + "&";
            }
            params = params.substring(0, params.length() - 1);
            url = url + "?" + params;
            ResponseEntity<T> responseEntity = restTemplate.getForEntity(url, cls);
            return responseEntity.getBody();
        } catch (Exception e) {
            LOGGER.error("get请求失败", e);
        }
        return null;
    }

}
