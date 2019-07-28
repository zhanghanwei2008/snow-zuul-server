package com.npcgo.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;


@Slf4j
public class AccessZuulFilter extends ZuulFilter {


    @Override
    public String filterType() {

        //处理请求时发生错误时被调用。
//        FilterConstants.ERROR_TYPE;
        //在路由请求时候被调用。
     //  return   FilterConstants.ROUTE_TYPE;
        //在routing和error过滤器之后被调用。
//        FilterConstants.POST_TYPE;

        //前置过滤器 可以在请求被路由之前调用。
        return FilterConstants.PRE_TYPE ;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        boolean iRet=false;
        //此方法可以根据请求的url进行判断是否需要拦截

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest  request = requestContext.getRequest();
        String uri= request.getRequestURI();
        //URI为user-service 做拦截，其他的不拦截
        if(uri.substring(1,13).contains("user-service"))
        {
            iRet=true;
        }

        return iRet;
    }

    @Override
    public Object run() throws ZuulException {
        //获取请求的上下文类 注意是：com.netflix.zuul.context包下的
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取request对象
        HttpServletRequest request = ctx.getRequest();
        //避免中文乱码
        ctx.addZuulResponseHeader("Content-type", "text/json;charset=UTF-8");
        ctx.getResponse().setCharacterEncoding("UTF-8");
        //打印日志
        log.info("请求方式：{},地址：{}", request.getMethod(),request.getRequestURI());
        String token = request.getParameter("token");
        if(StringUtils.isBlank(token)) {
            //使其不进行转发 自定义route类型时，在shouldFilter中也需要进行此参数判断。
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody("{\"code\":\"999500\",\"msg\":\"非法访问\"}");
            ctx.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());//401
            //或者添加一个额外参数也可以 传递参数可以使用
//           ctx.set("checkAuth",false);
        }

        log.info("URI:{}",request.getRequestURI());
        log.info("token{}",request.getHeader("token"));
        log.info("token{}",request.getHeader("token"));

        //这返回值没啥用
        return null;
    }
}
