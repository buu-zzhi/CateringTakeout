package com.example.controller.notify;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.service.OrdersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


@RestController
@RequestMapping("/notify")
public class PayNotifyController {
    @Autowired
    private OrdersService ordersService;
    @PostMapping("/paySuccess")
    public void paySuccessNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO 支付成功后的逻辑处理
        String body = readData(request);
        System.out.println(body);
        JSONObject jsonObject = JSON.parseObject(body);
        String number = jsonObject.getString("number");
        ordersService.paySuccess(number);
        responseToWeixin(response);
    }

    private String readData(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                builder.append("\n");
            }
            builder.append(line);
        }
        return builder.toString();
    }

    private void responseToWeixin(HttpServletResponse response) throws Exception{
        response.setStatus(200);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("code", "SUCCESS");
        map.put("message", "SUCCESS");
        response.setHeader("Content-type", ContentType.APPLICATION_JSON.toString());
        response.getOutputStream().write(
                JSON.toJSONString(map).getBytes(StandardCharsets.UTF_8)
        );
        response.flushBuffer();
    }
}
