package com.yq.controller;

import com.yq.domain.Commodity;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/commodity")
public class CommodityController {
    private Logger logger = LoggerFactory.getLogger(CommodityController.class);

    private Map<String, Commodity> commodityMap = new HashMap<>();
    {
        for(int i=0;i < 5; i++) {
            Commodity commodity = new Commodity();
            commodity.setId(i + "");
            commodity.setName("Car" + i );
            commodity.setOnlineDate(new Date());
            commodityMap.put(i+ "",commodity );
        }

    }

    @ApiOperation(value = "按id查询", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commodityId", value = "commodityId", required = true, dataType = "string", paramType = "path"),
    })
    @GetMapping(value = "/commodities/{commodityId}", produces = "application/json;charset=UTF-8")
    public Commodity getCommodity(@PathVariable String commodityId) {
        Commodity commodity = (Commodity) commodityMap.get(commodityId);
        return commodity;
    }

    @ApiOperation(value = "查询所有商品")
    @GetMapping(value = "/commodities", produces = "application/json;charset=UTF-8")
    public Iterable<Commodity> findAllCommodities() {
        Collection<Commodity> commodities = commodityMap.values();
        return commodities;
    }
}