package com.github.example.listener;

import com.github.example.EurekaInstanceInfoFactory;
import com.github.example.MyInstanceConfig;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Arvin.
 */
public class EurekaRegisterListener implements ServletContextListener {

    private EurekaInstanceConfig eurekaInstanceConfig = new MyInstanceConfig();

    private ApplicationInfoManager applicationInfoManager;

    private EurekaClient eurekaClient;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        InstanceInfo instanceInfo = new EurekaInstanceInfoFactory().create(eurekaInstanceConfig);

        applicationInfoManager = new ApplicationInfoManager(eurekaInstanceConfig, instanceInfo);

        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);

        eurekaClient = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());
        InstanceInfo nextServerInfo = null;
        while (nextServerInfo == null) {
            try {
                nextServerInfo = eurekaClient.getNextServerFromEureka("hi-service", false);
            } catch (Exception e) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        eurekaClient.shutdown();
    }
}
