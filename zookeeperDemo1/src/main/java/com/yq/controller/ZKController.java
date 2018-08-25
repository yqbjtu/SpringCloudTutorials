

package com.yq.controller;


import com.alibaba.fastjson.JSONObject;
import com.yq.service.ZkClientDemo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/zk")
public class ZKController {
    private Logger logger = LoggerFactory.getLogger(ZKController.class);

   @Autowired
    ZkClientDemo  zkClient;

    @ApiOperation(value = "设置key", notes="set")
    @ApiImplicitParams({

    })
    @GetMapping(value = "/init", produces = "application/json;charset=UTF-8")
    public String getInit() {
        String value = zkClient.init();
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("curentTime", LocalDateTime.now().toString());
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "createNode", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", defaultValue = "topic01", value = "topic", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/createNode", produces = "application/json;charset=UTF-8")
    public String createChildNode(@RequestParam String topic) {
        String value = zkClient.createNode(topic);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "deleteNode", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", defaultValue = "topic01", value = "topic", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/deleteNode", produces = "application/json;charset=UTF-8")
    public String delChildNode(@RequestParam String topic) {
        Boolean isDelOk = zkClient.deleteNode(topic);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("isDelOk", isDelOk);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "getData", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "topic", defaultValue = "topic01", value = "topic", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/getData", produces = "application/json;charset=UTF-8")
    public String getData(@RequestParam String topic) {
        String str = zkClient.getData(topic);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("str", str);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "getCurrentList", notes="post")
    @GetMapping(value = "/getCurrentList", produces = "application/json;charset=UTF-8")
    public String getCurrentList() {
        List<String> topics = zkClient.getCurrentList();

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("topics", topics);
        return jsonObj.toJSONString();
    }
}