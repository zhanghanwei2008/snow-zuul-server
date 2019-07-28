package com.npcgo.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.npcgo.zuul.Service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;


@Slf4j
public class Oauth2ResourceFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(Oauth2ResourceFilter.class);
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
//    @Autowired
//    RedisTemplate<String,Object> redisTemplate;

    @Override
    public int filterOrder() {
        return 200;
    }


    private RedisService redisService=new RedisService();
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String authorizationValue=  request.getHeader("authorization");
        String token= request.getHeader("token");
        logger.info("authorization====>${authorizationValue}");
        if( authorizationValue!=null)
        {

           if( redisService.ValidToken(authorizationValue)) {
               ctx.setResponseStatusCode(HttpStatus.OK.value());
           }else {
               ctx.setSendZuulResponse(false);
               ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
           }
        }
        else
        {
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }


        return null;
    }
}

