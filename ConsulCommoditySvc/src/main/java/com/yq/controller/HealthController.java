package com.yq.controller;

import com.yq.domain.Commodity;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/my")
@Slf4j
public class HealthController {
    private Logger logger = LoggerFactory.getLogger(HealthController.class);

    @ApiOperation(value = "health", notes="private")
    @GetMapping(value = "/health", produces = "application/json;charset=UTF-8")
    public String getMyHealth() {
        String str = "{\"description\":\"my customized health\",\"status\":\"UP\"}";
        return str;
    }

}