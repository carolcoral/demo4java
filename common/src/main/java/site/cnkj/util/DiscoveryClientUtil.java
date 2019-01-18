package site.cnkj.util;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * SpringCloud 常用的一些方法，后续会不断增加
 */
public class DiscoveryClientUtil {

    /**
     * 获取本地ip
     *
     * @return ip
     */
    public static String getLocalIP() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String ip = addr.getHostAddress().toString();
        return ip;
    }

    /**
     * 获取本地机器名
     *
     * @return hostName
     */
    public static String getLocalHostName() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String hostName = addr.getHostName().toString();
        return hostName;
    }

    /**
     * 组装请求地址
     *
     * @param ip
     * @param port
     * @return "http://"+ip+":"+port
     */
    public static String genHomeUrl(String ip, int port) {
        return "http://" + ip + ":" + port;
    }


    /**
     * 获取所有指定版本的指定服务的ip列表(所有机器)
     *
     * @param discoveryClient
     * @param serviceName     服务名
     * @param metaDataName    元数据key
     * @param metaDataValue   元数据值
     * @return url集合
     */
    public static List<String> getUrlList(DiscoveryClient discoveryClient, String serviceName, String metaDataName, String metaDataValue) {
        return getUrlList(discoveryClient, serviceName, metaDataName, metaDataValue, false);
    }

    /**
     * 获取所有指定元数据的指定服务的ip列表(是否同机器)
     *
     * @param discoveryClient
     * @param serviceName     服务名
     * @param metaDataName    元数据key
     * @param metaDataValue   元数据值
     * @param same            是否在相同机器上
     * @return url集合
     */
    public static List<String> getUrlList(DiscoveryClient discoveryClient, String serviceName, String metaDataName, String metaDataValue, boolean same) {
        Assert.notNull(metaDataValue, "non null version required");
        Assert.notNull(serviceName, "non null serviceName required");
        List<String> urlList = new ArrayList();
        List<ServiceInstance> serviceInstancesList = discoveryClient.getInstances(serviceName);
        for (ServiceInstance serviceInstance : serviceInstancesList) {
            boolean sameHost = same ? getLocalHostName().equals(serviceInstance.getHost()) : true;
            if (metaDataValue.equals(serviceInstance.getMetadata().get(metaDataName)) && sameHost) {
                EurekaDiscoveryClient.EurekaServiceInstance esi = (EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance;
                String homePageUrl = genHomeUrl(esi.getInstanceInfo().getIPAddr(), esi.getInstanceInfo().getPort());
                urlList.add(homePageUrl);
            }
        }
        return urlList;
    }

    /**
     * 获取当前存活版本的所有服务(所有机器)
     *
     * @param discoveryClient
     * @param serviceName     服务名
     * @return url集合
     */
    public static List<String> getUrlList(DiscoveryClient discoveryClient, String serviceName) {
        return getUrlList(discoveryClient, serviceName, false);
    }


    /**
     * 获取当前存活版本的所有服务(是否同机器)
     *
     * @param discoveryClient
     * @param serviceName     服务名
     * @param same            是否在相同机器上
     * @return url集合
     */
    public static List<String> getUrlList(DiscoveryClient discoveryClient, String serviceName, boolean same) {
        Assert.notNull(serviceName, "non null serviceName required");
        List<String> urlList = new ArrayList();
        List<ServiceInstance> serviceInstancesList = discoveryClient.getInstances(serviceName);
        for (ServiceInstance serviceInstance : serviceInstancesList) {
            boolean sameHost = same ? getLocalHostName().equals(serviceInstance.getHost()) : true;
            if (sameHost) {
                EurekaDiscoveryClient.EurekaServiceInstance esi = (EurekaDiscoveryClient.EurekaServiceInstance) serviceInstance;
                String homePageUrl = genHomeUrl(esi.getInstanceInfo().getIPAddr(), esi.getInstanceInfo().getPort());
                urlList.add(homePageUrl);
            }
        }
        return urlList;
    }
}
