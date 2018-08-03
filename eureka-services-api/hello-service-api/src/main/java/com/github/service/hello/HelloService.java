package com.github.service.hello;

/**
 * @author Arvin
 * @version 1.0
 * @since 2018/8/3 9:39
 */
public interface HelloService {

    /**
     * Say hello
     * @param name user name
     * @return 返回 "Hello, " 前缀
     */
    String sayHello(String name);
}
