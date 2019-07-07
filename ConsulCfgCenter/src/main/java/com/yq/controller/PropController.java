package com.yq.controller;

import com.alibaba.fastjson.JSONObject;
import com.yq.domain.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/v1")
@Slf4j
public class PropController {

    @Value("${demo.prop1}")
    private String prop1;

    @ApiOperation(value = "按用户id查询， 参数在path部分", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "prop", defaultValue = "demo.prop1", value = "prop", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/props/{prop}", produces = "application/json;charset=UTF-8")
    public String getUser(@PathVariable String prop) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("key", prop);
        jsonObj.put("value", prop1);
        return jsonObj.toJSONString();
    }
}