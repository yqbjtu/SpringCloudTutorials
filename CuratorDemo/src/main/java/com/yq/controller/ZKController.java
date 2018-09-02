

package com.yq.controller;


import com.alibaba.fastjson.JSONObject;
import com.yq.service.CuratorService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/zk")
@Slf4j
public class ZKController {

   @Autowired
   private CuratorService zkClient;


    @ApiOperation(value = "createNode", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", defaultValue = "A001", value = "uuid", required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "content", defaultValue = "topic01", value = "content", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/createNode", produces = "application/json;charset=UTF-8")
    public String createChildNode(@RequestParam String uuid, @RequestParam String content) {
        String value = zkClient.createNode(uuid, content);
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("value", value);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "deleteNode", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuid", defaultValue = "A001", value = "uuid", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/deleteNode", produces = "application/json;charset=UTF-8")
    public String delChildNode(@RequestParam String uuid) {
        Boolean isDelOk = zkClient.deleteNode(uuid);

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("isDelOk", isDelOk);
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "getData", notes="post")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", defaultValue = "/allSubList/A001", value = "path", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/getData", produces = "application/json;charset=UTF-8")
    public String getData(@RequestParam String path) {
        String str = zkClient.getData(path);

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