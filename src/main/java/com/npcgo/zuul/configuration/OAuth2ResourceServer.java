package com.npcgo.zuul.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.curator.shaded.com.google.common.collect.ImmutableList;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.client.RestTemplate;

import java.util.List;

// 资源服务器配置
@Configuration
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest()
            .authenticated()
        .and()
            .requestMatchers()
            .antMatchers("/oauth2/**");
    }

    @Bean
    public RestTemplate restTemplate() {
        //httpRequestFactory()
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter jsonConverter = (MappingJackson2HttpMessageConverter) converter;
                jsonConverter.setObjectMapper(new ObjectMapper());
                jsonConverter.setSupportedMediaTypes(ImmutableList.of(new MediaType("application", "json", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET), new MediaType("text", "javascript", MappingJackson2HttpMessageConverter.DEFAULT_CHARSET)));
            }
        }
        return restTemplate;
    }

    @Bean
    public AccessTokenConverter accessTokenConverter() {
        return new DefaultAccessTokenConverter();
    }
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenServices(tokenServices());//.resourceId(SPARKLR_RESOURCE_ID);
    }
    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://127.0.0.1:8080/oauth/check_token");
//这里硬编码客户端信息，服务端硬编码保存在内存里，生产上请修改
        remoteTokenServices.setClientId("clientapp");
        remoteTokenServices.setClientSecret("112233");
        remoteTokenServices.setRestTemplate(restTemplate());
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
        return remoteTokenServices;
    }

}
