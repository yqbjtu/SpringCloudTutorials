
package com.yq.exceptiondemo.exception;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
@Slf4j
public class GlobalException {
    /*
    这种BaseException 无法涵盖devide方法跑出的Exception，因为我们可以再增加一个processException(Exception ex)
    但是增加了该方法后baseException就失效了，因为所有的exception都是Exception
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler
    public String processBaseException(BaseException ex){
        String msg = "My customized base exception. msg:" + ex.getMessage();
        log.debug("processBaseException msg={}", msg);

        JSONObject json = new JSONObject();
        json.put("code", ex.getCode());
        json.put("obj", ex.getMyCause());
        json.put("msg", msg);

        return json.toJSONString();
    }

//    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    @ExceptionHandler
//    public String processException(Exception ex){
//        String msg = "My customized exception. msg:" + ex.getMessage();
//        log.debug("processException msg={}", msg);
//
//        JSONObject json = new JSONObject();
//        json.put("msg", msg);
//
//        return json.toJSONString();
//    }
}