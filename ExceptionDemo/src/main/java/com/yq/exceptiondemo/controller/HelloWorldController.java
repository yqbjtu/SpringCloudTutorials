package com.yq.exceptiondemo.controller;

import com.yq.exceptiondemo.config.SystemLog;
import com.yq.exceptiondemo.exception.ComputerException;
import com.yq.exceptiondemo.service.ComputerService;
import com.yq.exceptiondemo.utils.Constants;
import com.yq.exceptiondemo.utils.ReturnResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@Slf4j
public class HelloWorldController {

    @Autowired
    ComputerService computerSvc;

    @SystemLog(description = "helloWorld测试")
    @ApiOperation(value = "hello demo", notes = "just for demo")
    @GetMapping(value = "/hello", produces = "text/plain;charset=UTF-8")
    public String hello() {
        log.debug("hello");
        ReturnResult ret = new ReturnResult(Constants.QUERY_OK, "Hello World");
        return ret.toString();
    }

    @SystemLog(description = "devide测试")
    @ApiOperation(value = "hello exception demo, service method will throw native exception", notes = "just for demo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "a", defaultValue = "10", value = "a", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "b", defaultValue = "3", value = "b", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping(value = "/devide", produces = "text/plain;charset=UTF-8")
    public String exceptionDemo(@RequestParam("a") int a, @RequestParam("b") int b) {
        log.info("Enter exceptionDemo a={} devided by b={}", a, b);

        int c= computerSvc.devide(a, b);

        ReturnResult ret = new ReturnResult(Constants.QUERY_OK, null);
        ret.setObj(Integer.valueOf(c));

        log.info("End exceptionDemo ret={}", ret);
        return ret.toString();
    }

    @ApiOperation(value = "hello exception demo, service will throw wrapper exception", notes = "just for demo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "a", defaultValue = "10", value = "a", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "b", defaultValue = "0", value = "b", required = true, dataType = "int", paramType = "query")
    })
    @GetMapping(value = "/devideCatchException", produces = "text/plain;charset=UTF-8")
    public String exceptionCatchDemo(@RequestParam("a") int a, @RequestParam("b") int b) throws ComputerException {
        log.info("Enter exceptionDemo a={} devided by b={}", a, b);

        int c= computerSvc.devideCatchExecption(a, b);

        ReturnResult ret = new ReturnResult(Constants.QUERY_OK, null);
        ret.setObj(Integer.valueOf(c));

        log.info("End exceptionDemo ret={}", ret);
        return ret.toString();
    }
}