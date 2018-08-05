package com.github.eureka.example.eureka;

import com.alibaba.fastjson.JSON;
import com.github.eureka.example.model.EurekaServer;
import com.github.eureka.example.service.EurekaServerService;
import com.github.eureka.example.util.RetryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

import static org.springframework.cloud.netflix.eureka.EurekaConstants.DEFAULT_PREFIX;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 14:19
 */
@ConfigurationProperties(EurekaClientConfigBean.PREFIX)
@Component
@Primary
public class DynamicEurekaClientConfigBean extends EurekaClientConfigBean {

    private static final Logger logger = LoggerFactory.getLogger(DynamicEurekaClientConfigBean.class);

    public static final String DEFAULT_ZONE_HOST = "localhost";
    public static final int DEFAULT_ZONE_PORT = 8761;
    public static final int DEFAULT_HTTP_PORT = 8080;

    private final SecurityProperties securityProperties;

    private final ServerProperties serverProperties;

    private final DiscoveryClient discoveryClient;

    private final EurekaServerService eurekaServerService;

    private final Environment environment;

    /**
     * 本地IP
     **/
    private String localip;
    /**
     * 监听地址
     **/
    private int localport;

    private String hostname;

    @Autowired(required = false)
    public DynamicEurekaClientConfigBean(DiscoveryClient discoveryClient,
                                         SecurityProperties securityProperties,
                                         ServerProperties serverProperties,
                                         EurekaServerService eurekaServerService,
                                         Environment environment) {
        this.discoveryClient = discoveryClient;
        this.securityProperties = securityProperties;
        this.serverProperties = serverProperties;
        this.eurekaServerService = eurekaServerService;
        this.environment = environment;

        this.initLocalIpAndPort();
    }

    private void initLocalIpAndPort() {

        this.localip = this.environment.getProperty("spring.cloud.client.ipAddress", this.environment.getProperty("spring.cloud.client.ip-address"));
        this.localport = getServerPort();
        this.hostname = this.environment.getProperty("eureka.instance.hostname", this.localip);

    }

    @PostConstruct
    public void init() {

        String localEurekaServiceUrl = getLocalEurekaServiceUrl("localhost");

        // 适配 defaultZone 的 serviceUrl
        adapterDefaultZone(localEurekaServiceUrl);

        registerMySelf();
    }

    protected void registerMySelf() {
        // 自定义 Eureka 注册服务发现列表，然后扔进 serviceUrl 中去就可以了
        eurekaServerService.add(new EurekaServer(getLocalEurekaServiceUrl(this.hostname)));
    }

    @Override
    public List<String> getEurekaServerServiceUrls(String myZone) {

        // 合并 EurekaServer
        mergeEurekaServer();

        logger.info("EurekaServer服务列表： ");
        Map<String, String> map = getServiceUrl();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            logger.info(entry.getKey() + " : " + entry.getValue());
        }

        return super.getEurekaServerServiceUrls(myZone);
    }

    private void filterSelfServiceUrl() {

        Map<String, String> newMap = new HashMap<>();

        Map<String, String> map = getServiceUrl();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String serviceUrl = entry.getValue();
            String[] urls = serviceUrl.split(",");
            StringBuilder builder = new StringBuilder();
            boolean containLocal = false;
            for (String url : urls) {
                try {
                    URI uri = new URI(url);
                    if (!isLocalHostAndPort(uri.getHost(), uri.getPort())) {
                        builder.append(url).append(",");
                    } else {
                        containLocal = true;
                    }
                } catch (URISyntaxException ignored) {
                }
            }

            if (builder.length() > 0) {
                if (containLocal) {
                    builder.append(getLocalEurekaServiceUrl("localhost")).append(",");
                }
                builder.setLength(builder.length() - 1);
                newMap.put(entry.getKey(), builder.toString());
            }
        }

        if (!newMap.isEmpty()) {
            this.setServiceUrl(newMap);
        } else {
            newMap.put(DEFAULT_ZONE, getLocalEurekaServiceUrl(this.localip));
            this.setServiceUrl(newMap);
        }
    }

    /**
     * 合并 EurekaServer
     */
    private void mergeEurekaServer() {

        // 获取所有的
        List<EurekaServer> serverList = eurekaServerService.list();
        if (null == serverList || serverList.isEmpty()) {
            return;
        }

        Map<String, String> serviceUrlMap = getServiceUrl();

        for (EurekaServer eurekaServer : serverList) {
            String zone = eurekaServer.getZone();
            if (StringUtils.isEmpty(zone)) {
                zone = DEFAULT_ZONE;
            }
            String serviceUrl = eurekaServer.getServiceUrl();

            String serviceUrls = serviceUrlMap.get(zone);
            if (StringUtils.isEmpty(serviceUrls)) {
                serviceUrlMap.put(zone, serviceUrl);
            } else {
                if (!serviceUrls.contains(serviceUrl)) {
                    serviceUrlMap.put(zone, serviceUrls + "," + serviceUrl);
                }
            }
        }

        // 过滤自己的 ServiceUrl
        filterSelfServiceUrl();

    }

    private String getLocalEurekaServiceUrl(String hostname) {
        SecurityProperties.User user = null == securityProperties ? null : securityProperties.getUser();
        if (!isUserApplied(user)) {
            return "http://" + hostname + ":" + this.localport + DEFAULT_PREFIX;
        } else {
            return "http://" + user.getName() + ":" + user.getPassword() + "@" + hostname + ":" + this.localport + DEFAULT_PREFIX;
        }
    }

    /**
     * 适配 默认的 Zone ，主要判断端口， 如果没有配置的话，那么默认就是本地的
     */
    private void adapterDefaultZone(String localEurekaServiceUrl) {
        Map<String, String> map = this.getServiceUrl();
        String serviceUrl = map.get(DEFAULT_ZONE);
        try {
            URI uri = new URI(serviceUrl);
            if (DEFAULT_ZONE_HOST.equals(uri.getHost()) && DEFAULT_ZONE_PORT == uri.getPort()) {
                map.put(DEFAULT_ZONE, localEurekaServiceUrl);
            }
        } catch (URISyntaxException ignored) {
        }
    }

    private int getServerPort() {

        int defaultServerPort = DEFAULT_HTTP_PORT;
        if (this.serverProperties == null) {
            return defaultServerPort;
        }
        if (this.serverProperties.getPort() == null) {
            return defaultServerPort;
        }
        return this.serverProperties.getPort();
    }

    private boolean isUserApplied(SecurityProperties.User user) {

        if (null == user) {
            return false;
        }

        if (StringUtils.isEmpty(user.getName()) && StringUtils.isEmpty(user.getPassword())) {
            return false;
        }

        return true;

    }

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 每隔 10 秒执行一次, 检查服务，如果不可用则从列表中删除
     */
    @Scheduled(cron = "0/10 * * * * *")
    public void eurekaServerMonitor() throws Exception {

        // 注册本服务
        registerMySelf();

        List<EurekaServer> serverList = eurekaServerService.list();
        logger.info("执行监控： " + JSON.toJSONString(serverList));

        if (null == serverList || serverList.isEmpty()) {
            return;
        }

        for (EurekaServer eurekaServer : serverList) {

            String serviceUrl = eurekaServer.getServiceUrl();
            final URI uri = new URI(serviceUrl);

            String host = uri.getHost();
            int port = uri.getPort();

            if (isLocalHostAndPort(host, port)) {
                continue;
            }

            String responseText = RetryUtil.execute(1, 1000, false, new RetryUtil.Executor<String>() {
                @Override
                public String execute() {
                    String url = "http://" + uri.getHost() + ":" + uri.getPort() + "/monitor/monitor.do";
                    return restTemplate.getForObject(url, String.class);
                }
            });

            if (StringUtils.isEmpty(responseText)) {
                removeUnAvailableEurekaServer(eurekaServer);
            }

        }
    }


    /**
     * 移除服务
     *
     * @param eurekaServer 要移除的 EurekaServer
     */
    private void removeUnAvailableEurekaServer(EurekaServer eurekaServer) {
        eurekaServerService.remove(eurekaServer);
        String serviceUrl = eurekaServer.getServiceUrl();
        try {
            final URI uri = new URI(serviceUrl);
            filterServiceUrl(uri.getHost(), uri.getPort());
        } catch (URISyntaxException ignored) {
        }

        logger.info("移除服务[" + eurekaServer.getServiceUrl() + "]，监控失败！");

    }

    private void filterServiceUrl(String host, int port) {
        Map<String, String> newMap = new HashMap<>();

        Map<String, String> map = getServiceUrl();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String serviceUrl = entry.getValue();
            String[] urls = serviceUrl.split(",");
            StringBuilder builder = new StringBuilder();
            for (String url : urls) {
                try {
                    URI uri = new URI(url);
                    if (uri.getHost().equalsIgnoreCase(host) && uri.getPort() != port){
                        builder.append(url).append(",");
                    }
                } catch (URISyntaxException ignored) {
                }
            }

            if (builder.length() > 0) {
                builder.setLength(builder.length() - 1);
                newMap.put(entry.getKey(), builder.toString());
            }
        }

        if (!newMap.isEmpty()) {
            this.setServiceUrl(newMap);
        } else {
            newMap.put(DEFAULT_ZONE, getLocalEurekaServiceUrl(this.localip));
            this.setServiceUrl(newMap);
        }

    }

    private boolean isLocalHostAndPort(String host, int port) {

        try {
            String ip = InetAddress.getByName(host).getHostAddress();
            String localip = InetAddress.getByName(this.localip).getHostAddress();
            Set<String> localhostSet = new HashSet<>(Arrays.asList(
                    this.localip, localip, "localhost", this.hostname
            ));

            return (localhostSet.contains(ip) || localhostSet.contains(host))
                    && this.localport == port;
        } catch (UnknownHostException e) {
            return false;
        }
    }


}
