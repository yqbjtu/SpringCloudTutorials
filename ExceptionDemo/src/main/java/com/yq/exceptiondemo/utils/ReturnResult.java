package com.yq.exceptiondemo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 */
@Data
public class ReturnResult<T> {
    private int code;
    private T obj;

    public ReturnResult(int code, T obj) {
        this.code = code;
        this.obj = obj;
    }

    @Override
    public String toString(){
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("obj", obj.toString());

        return json.toJSONString();
    }


}
