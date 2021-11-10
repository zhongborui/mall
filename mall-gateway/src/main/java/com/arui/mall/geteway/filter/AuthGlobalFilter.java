package com.arui.mall.geteway.filter;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.common.result.R;
import com.arui.mall.common.result.RetValCodeEnum;
import com.arui.mall.common.util.AuthContextHolder;
import com.arui.mall.common.util.IpUtil;
import com.arui.mall.geteway.constant.RedisConstant;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferWrapper;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author ...
 */
@Component
public class AuthGlobalFilter implements GlobalFilter {

    @Value("${filter.whiteList}")
    private String whiteList;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 只能自己new
     */
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();


        // 1.内部接口不能访问:比如 /sku/**
        if (antPathMatcher.match("/sku/**", path)){
            ServerHttpResponse response = exchange.getResponse();
            // 将数据写到浏览器
            return writeDataToBrowser(response, RetValCodeEnum.NO_PERMISSION);
        }

        // 2.验证用户，检验用户是否携带userId, 再组装成userKey从redis中获得
        String userId = getLoginUerId(request);
        if ("-1".equals(userId)){
            ServerHttpResponse response = exchange.getResponse();
            // 将数据写到浏览器
            return writeDataToBrowser(response, RetValCodeEnum.NO_PERMISSION);
        }

        // 直接取登录页面
        if ("-2".equals(userId)){
            // 需要重定向到 登录页面
//            ServerHttpResponse response = exchange.getResponse();
//            response.setStatusCode(HttpStatus.SEE_OTHER);
//            response.getHeaders().set(HttpHeaders.LOCATION, "http://www.gmall.com/login.html?originalUrl="+request.getURI());
//            return response.setComplete();
            return chain.filter(exchange);
        }

        // 订单模块需要登录
        if (antPathMatcher.match("/order/**",path)){
            //如果是未登录情况
            if (StringUtils.isEmpty(userId)){
                ServerHttpResponse response = exchange.getResponse();
                return writeDataToBrowser(response, RetValCodeEnum.NO_LOGIN);
            }
        }

        // 4.判断是否拦截路径白名单（order， cart）， 如果是，判断是否已经登录
        String[] whiteListSplit = whiteList.split(",");
        for (String oneWhile : whiteListSplit) {
            // -1是不包含, 既不是白名单，不可以访问
            if (path.indexOf(oneWhile) != -1 && StringUtils.isEmpty(userId)){
                // 需要重定向到 登录页面
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION, "http://www.gmall.com/login.html?originalUrl="+request.getURI());
                return response.setComplete();
            }
        }

        // 未登录添加购物车，前端随机生成的termId,封装到header给下游
//        HttpCookie cookie = request.getCookies().getFirst("userTempId");
//        if (!StringUtils.isEmpty(cookie) ){
//            request.mutate().header("userTempId", cookie.getValue()).build();
//            return chain.filter(exchange.mutate().request(request).build());
//        }

        // 如果用户登录了，需要将userId 放置再header中，以便下游可以在request中获取userId
        if (!StringUtils.isEmpty(userId) ){
            request.mutate().header("userId", userId).build();
            return chain.filter(exchange.mutate().request(request).build());
        }

        // 放行
        return chain.filter(exchange);
    }

    /**
     * 验证用户，检验用户是否携带userId
     * @param request
     * @return
     */
    private String getLoginUerId(ServerHttpRequest request) {
        // 从request中获得 token
        String token = null;
        List<String> result = request.getHeaders().get("token");
        if (!StringUtils.isEmpty(result)){
             token = result.get(0);
        }else {
             HttpCookie cookie = request.getCookies().getFirst("token");
             if (cookie != null){
                 token = cookie.getValue();
             }
        }
        // 从redis中获得 userId
        if (!StringUtils.isEmpty(token)){
            String userKey = RedisConstant.USER_LOGIN_KEY_PREFIX + token;
            Object o = redisTemplate.opsForValue().get(userKey);
            String valueFromRedis = (String) redisTemplate.opsForValue().get(userKey);


            // 重新登录
//            if (valueFromRedis == null){
//                return "-2";
//            }
            JSONObject jsonObject = JSONObject.parseObject(valueFromRedis);
            String userId = jsonObject.getString("userId");

            // 同时判断ip地址是否一致
            String ip = IpUtil.getGatwayIpAddress(request);
            if (ip.equals(jsonObject.getString("ip"))){
                return userId;
            }else {
                return "-1";
            }

        }
        return null;
    }

    /**
     * 将数据写到浏览器
     * @param response
     * @param retValCodeEnum
     * @return
     */
    private Mono<Void> writeDataToBrowser(ServerHttpResponse response, RetValCodeEnum retValCodeEnum) {
        // 返回到浏览器的数据格式
        R<Object> data = R.data(null, retValCodeEnum);
        byte[] bytes = JSONObject.toJSONString(data).getBytes(StandardCharsets.UTF_8);
        // 封装数据到dataBuffer中
        DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
        response.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        return response.writeWith(Mono.just(dataBuffer));
    }
}
