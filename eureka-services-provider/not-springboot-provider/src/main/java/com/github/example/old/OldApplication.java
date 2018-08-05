package com.github.example.old;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;

/**
 * Created by Arvin.
 */
public class OldApplication {

    private static final DynamicPropertyFactory configInstance = DynamicPropertyFactory
            .getInstance();


    public static void main(String[] args) throws InterruptedException {


        registerWithEureka2();
//        registerWithEureka();

        Thread.sleep(3600000);
    }

    public static void registerWithEureka2() {
        EurekaInstanceConfig eurekaInstanceConfig = new MyInstanceConfig();

        InstanceInfo instanceInfo = new EurekaInstanceInfoFactory().create(eurekaInstanceConfig);

        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(eurekaInstanceConfig, instanceInfo);

        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);


        DiscoveryClient client = new DiscoveryClient(applicationInfoManager, new DefaultEurekaClientConfig());
        InstanceInfo nextServerInfo = null;
        while (nextServerInfo == null) {
            try {
                nextServerInfo = client.getNextServerFromEureka("hi-service", false);
            } catch (Exception e) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    public static void registerWithEureka() {
        // Register with Eureka
        DiscoveryManager.getInstance().initComponent(
                new MyInstanceConfig(),
                new DefaultEurekaClientConfig());
        ApplicationInfoManager.getInstance().setInstanceStatus(
                InstanceInfo.InstanceStatus.UP);
        String vipAddress = configInstance.getStringProperty(
                "eureka.vipAddress", "hmc-web.mydomain.net").get();
        InstanceInfo nextServerInfo = null;
        while (nextServerInfo == null) {
            try {
                nextServerInfo = DiscoveryManager.getInstance()
                        .getDiscoveryClient()
                        .getNextServerFromEureka(vipAddress, false);
            } catch (Throwable e) {
                System.out
                        .println("Waiting for service to register with eureka..");


                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }


            }
        }
    }
}
