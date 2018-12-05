//
//
//package com.yq.controller;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.yq.domain.User;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.cloud.client.serviceregistry.Registration;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///*
//为了掩饰不注册consul的情况，暂时注销该代码
// */
//@RestController
//@RequestMapping("/svc")
//public class SvcInfoController {
//    private Logger log = LoggerFactory.getLogger(SvcInfoController.class);
//
//    @Autowired
//    DiscoveryClient discoveryClient;
//
//    @Autowired
//    Registration registration;
//
//    @ApiOperation(value = "按服务name查询")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", value = "user-service", required = true, dataType = "string", paramType = "query"),
//    })
//    @GetMapping(value = "/info", produces = "application/json;charset=UTF-8")
//    public String getInfo(@RequestParam String name) {
//        String result = null;
//        JSONObject json = new JSONObject();
//        try {
//            List<ServiceInstance> list = discoveryClient.getInstances(name);
//            if (list != null && list.size() != 0) {
//                JSONArray jsonArray = new JSONArray();
//                for (ServiceInstance svcInstance : list) {
//                    log.info("ServiceId={}", svcInstance.getServiceId());
//                    log.info("ServiceUri={}", svcInstance.getUri());
//
//                    JSONObject jsonTemp = new JSONObject();
//                    jsonTemp.put("ServiceId", svcInstance.getServiceId());
//                    jsonTemp.put("ServiceUri", svcInstance.getUri());
//                    jsonTemp.put("ServiceHost", svcInstance.getHost());
//                    jsonTemp.put("ServiceSchema", svcInstance.getScheme());
//                    jsonTemp.put("ServicePort", svcInstance.getPort());
//                    jsonTemp.put("ServiceMetadata", svcInstance.getMetadata());
//                    jsonArray.add(jsonTemp);
//                }
//                result = jsonArray.toJSONString();
//            }
//            else {
//                json.put("can't find this service", name);
//                result = json.toJSONString();
//            }
//        } catch (Exception ex) {
//            log.info("Failed to get service ", ex);
//            json.put("error cause", ex.getMessage());
//            result = json.toJSONString();
//        }
//        return result;
//    }
//
//    @ApiOperation(value = "查询自身的服务id")
//    @GetMapping(value = "/info/local", produces = "application/json;charset=UTF-8")
//    public String getInfoLocal() {
//        JSONObject jsonTemp = new JSONObject();
//
//        jsonTemp.put("ServiceId", registration.getServiceId());
//        jsonTemp.put("ServiceUri", registration.getUri());
//        jsonTemp.put("ServiceHost", registration.getHost());
//        jsonTemp.put("ServiceSchema", registration.getScheme());
//        jsonTemp.put("ServicePort", registration.getPort());
//        jsonTemp.put("ServiceMetadata", registration.getMetadata());
//        return jsonTemp.toJSONString();
//    }
//}