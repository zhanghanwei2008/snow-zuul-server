package com.npcgo.zuul;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import com.npcgo.zuul.filter.AccessZuulFilter;
import com.npcgo.zuul.tools.FilterInfo;
import com.npcgo.zuul.tools.FilterVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.File;


@EnableZuulProxy
@SpringBootApplication
@EnableDiscoveryClient
@EnableHystrixDashboard

//@RibbonClient(name = "spring-snow-service-provider")
public class SnowZuulServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnowZuulServerApplication.class, args);

    }
    //配置过滤器的目录
    @Value("${zuul.filter.root:groovy/filters}")
    private String scriptRoot;
    //过滤器拉取的时间间隔
    @Value("${zuul.filter.refreshInterval:5}")
    private String refreshInterval;
    //加载动态过滤器
    @PostConstruct
    public void zuulInit() {
        FilterLoader.getInstance().setCompiler(new GroovyCompiler());

       // FilterInfo filterInfo = FilterVerifier.getInstance().verifyFilter("");
        // 读取配置，获取脚本根目录
       //        String scriptRoot = System.getProperty("zuul.filter.root", "groovy/filters");
        // 获取刷新间隔
//        String refreshInterval = System.getProperty("zuul.filter.refreshInterval", "5");
        if (scriptRoot.length() > 0) scriptRoot = scriptRoot + File.separator;
        try {
            FilterFileManager.setFilenameFilter(new GroovyFileFilter());
            FilterFileManager.init(Integer.parseInt(refreshInterval), scriptRoot + "pre",
                    scriptRoot + "route", scriptRoot + "post");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Bean
//    public AccessZuulFilter accessZuulFilter() {
//        return new AccessZuulFilter();
//    }

    @Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }

}
