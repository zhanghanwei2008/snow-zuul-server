package com.npcgo.zuul.controller;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.filters.FilterRegistry;
import com.npcgo.zuul.Service.RedisService;
import com.npcgo.zuul.configuration.Reset2HystrixConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
public class ResetController   {

    @GetMapping("/Reset")
    public void Reset()
    {
        ApplicationContext context = new AnnotationConfigApplicationContext(Reset2HystrixConfiguration.class);

    }
    @GetMapping("Filter/Del")
    public void   DelFilter(String key)
    {
         FilterRegistry filterRegistry = FilterRegistry.instance();
//         filterRegistry.getAllFilters().remove("H:\\scripts\\pre\\Oauth2ResourceFilter1.groovyOauth2ResourceFilter1.groovy");
         filterRegistry.remove(key);
    }
    @Autowired
    private RedisService redisService;
    @GetMapping("Token/get")
    public Boolean   getToken(String key)
    {
        return redisService.ValidToken(key);

    }

    @GetMapping("Token/add")
    public Integer   addToken(String key,String value)
    {
        return redisService.appendKey(key,value);

    }

    @GetMapping("oauth2")
    public  String Mytest()
    {
        return  "Hello world";

    }
}
