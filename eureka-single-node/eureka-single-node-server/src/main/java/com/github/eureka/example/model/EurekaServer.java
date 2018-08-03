package com.github.eureka.example.model;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 17:01
 */
public class EurekaServer {

    /**
     * 区域
     **/
    private String region;
    /**
     * 机房
     **/
    private String zone;
    /**
     * 服务地址
     **/
    private String serviceUrl;

    public EurekaServer() {
    }

    public EurekaServer(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
