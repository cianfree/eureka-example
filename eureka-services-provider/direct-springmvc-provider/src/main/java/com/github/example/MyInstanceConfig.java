package com.github.example;

import com.netflix.appinfo.MyDataCenterInstanceConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 让注册到服务的名称是机器的ip ，非主机名
 * Created by Arvin.
 */
public class MyInstanceConfig extends MyDataCenterInstanceConfig {

    @Override
    public String getHostName(boolean refresh) {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return super.getHostName(refresh);
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }
}
