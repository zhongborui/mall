package com.arui.mall.geteway.filter;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.common.result.R;
import com.arui.mall.common.result.RetValCodeEnum;
import com.arui.mall.common.util.IpUtil;
import com.arui.mall.geteway.constant.RedisConstant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author ...
 */
@Component
public class AuthGlobalFilter implements GlobalFilter{

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        // 1.登录页面直接访问
        if (path.contains("login")){
            return chain.filter(exchange);
        }
        // 2.内部接口不能访问，提示权限不够
        if (path.startsWith("/sku") || path.startsWith("/product")){
            return writeToBrowser(exchange, RetValCodeEnum.NO_PERMISSION);
        }

        // 判断用户是否携带token
        String token = hasTokenOrTempId(request, "token");

        if (!StringUtils.isEmpty(token)){
            // 通过token在redis中获取userId
            String userId = getUserIdByTokenInRedis(request, token);
            if (userId.equals("-1")){
                // 没有userId,
            }else {
                // 有userId
                // 将userId 放置header， 提供给下游服务
                request.mutate().header("userId", userId);
            }
        }

        // 判断是否有临时id，放置header
        String userTempId = hasTokenOrTempId(request, "userTempId");
        if (!StringUtils.isEmpty(userTempId)) {
            // userTempId 放置header， 提供给下游服务
            request.mutate().header("userTempId", userTempId);
        }
        return chain.filter(exchange);
    }

    /**
     * 通过token在redis中获取userId
     * @param request
     * @param token
     * @return
     */
    private String getUserIdByTokenInRedis(ServerHttpRequest request, String token) {
        // 通过token在redis中信息比较
        String userKey = RedisConstant.USER_LOGIN_KEY_PREFIX + token;
        String valueInRedis = (String) redisTemplate.opsForValue().get(userKey);
        JSONObject jsonObject = JSONObject.parseObject(valueInRedis);
        String ip = jsonObject.getString("ip");
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        if (gatwayIpAddress.equals(ip)){
            String userId = jsonObject.getString("userId");
            return userId;
        }else {
            // ip地址不同，返回-1
            return "-1";
        }
    }

    /**
     *  判断用户是否携带token或者tempId
     * @param request
     * @param tokenOrTempId
     * @return
     */
    private String hasTokenOrTempId(ServerHttpRequest request, String tokenOrTempId) {
        // url中是否存在
        String token = request.getQueryParams().getFirst(tokenOrTempId);
        if (StringUtils.isEmpty(token)){
            // header是否有token
            token = request.getHeaders().getFirst(tokenOrTempId);
            if (StringUtils.isEmpty(token)){
                // cookie中是否有token
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                if (!StringUtils.isEmpty(cookies)){
                    HttpCookie httpCookie = cookies.getFirst(tokenOrTempId);
                    if (!StringUtils.isEmpty(httpCookie)) {
                        token = httpCookie.getValue();
                    }else {
                        return null;
                    }
                }
            }
        }
        return token;
    }

    /**
     * 直接输出到浏览器
     * @param exchange
     * @param retValCodeEnum
     * @return
     */
    private Mono<Void> writeToBrowser(ServerWebExchange exchange, RetValCodeEnum retValCodeEnum) {
        ServerHttpResponse response = exchange.getResponse();
        R<Object> resData = R.data(null, retValCodeEnum);
        byte[] resDataBytes = JSONObject.toJSONString(resData).getBytes(StandardCharsets.UTF_8);
        DataBuffer data = response.bufferFactory().wrap(resDataBytes);
        response.getHeaders().add("Content-Type","application/json;charset=UTF-8");
        return response.writeWith(Mono.just(data));
    }
}
