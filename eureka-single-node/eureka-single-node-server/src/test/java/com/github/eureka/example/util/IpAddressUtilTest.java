package com.github.eureka.example.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Arvin.
 */
public class IpAddressUtilTest {

    @Test
    public void getLocalIpAddress() {

        System.out.println(IpAddressUtil.getLocalIpAddress());
    }
}