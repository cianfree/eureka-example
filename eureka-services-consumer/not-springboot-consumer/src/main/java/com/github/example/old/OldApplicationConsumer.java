package com.github.example.old;

import com.alibaba.fastjson.JSON;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by Arvin.
 */
public class OldApplicationConsumer {

    private static ApplicationInfoManager applicationInfoManager;
    private static EurekaClient eurekaClient;

//    private static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
//        if (applicationInfoManager == null) {
//            EurekaConfigBasedInstanceInfoProvider provider = new EurekaConfigBasedInstanceInfoProvider(instanceConfig);
//            InstanceInfo instanceInfo = provider.get();
//            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
//        }
//
//        return applicationInfoManager;
//    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        EurekaInstanceConfig eurekaInstanceConfig = new MyInstanceConfig();

        InstanceInfo instanceInfo = new EurekaInstanceInfoFactory().create(eurekaInstanceConfig);

//        EurekaConfigBasedInstanceInfoProvider provider = new EurekaConfigBasedInstanceInfoProvider(eurekaInstanceConfig);
//        Method method = EurekaConfigBasedInstanceInfoProvider.class.getMethod("get");
//        method.setAccessible(true);
//        InstanceInfo instanceInfo = (InstanceInfo) method.invoke(provider);

        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(eurekaInstanceConfig, instanceInfo);

        DiscoveryClient client = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());

        Application application = client.getApplication("hi-service");

        List<InstanceInfo> instanceInfoList = application.getInstances();

        for (InstanceInfo instanceInfo1 : instanceInfoList) {

            System.out.println(JSON.toJSONString(instanceInfo1));
        }


        System.out.println(client);
    }
}
