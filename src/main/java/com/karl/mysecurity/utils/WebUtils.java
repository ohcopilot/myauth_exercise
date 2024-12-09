package org.karl.mytest01.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import org.karl.mytest01.entity.ResponseResult;

import java.io.IOException;

public class WebUtils {
    public static void renderString(HttpServletResponse response, int code, String msg){
        try {
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.setStatus(code);
            response.getWriter().println(JSON.toJSONString(ResponseResult.fail(msg)));
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}