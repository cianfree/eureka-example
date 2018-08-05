package com.github.eureka.example.util;

import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

/**
 * Created by Arvin.
 */
public class IpAddressUtil {

    private IpAddressUtil() {
    }

    public static String getLocalIpAddress() {
        try (InetUtils utils = new InetUtils(new InetUtilsProperties())) {
            InetUtils.HostInfo hostInfo = utils.findFirstNonLoopbackHostInfo();
            return hostInfo.getIpAddress();
        }
    }
}
