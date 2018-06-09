
package com.yq.exceptiondemo.exception;

import com.alibaba.fastjson.JSONObject;
import com.yq.exceptiondemo.utils.Constants;
import com.yq.exceptiondemo.utils.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;


@ControllerAdvice
@Slf4j
public class GlobalException {
    /*
    这种BaseException 无法涵盖devide方法跑出的Exception，因为我们可以再增加一个processException(Exception ex)
    但是增加了该方法后baseException就失效了，因为所有的exception都是Exception
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(BaseException.class)
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleIllegalParamException(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String tips = "参数不合法";
        if (errors.size() > 0) {
            tips = errors.get(0).getDefaultMessage();
        }
        ReturnResult result = new ReturnResult(Constants.PARAMETER_ERROR, tips);

        return result.toString();
    }

    @ExceptionHandler(NumberFormatException.class)
    public String handleIllegalNumberFormatException(NumberFormatException e) {
        String tips = "参数不能转换为数字";
        ReturnResult result = new ReturnResult(Constants.PARAMETER_ERROR, e.getMessage());
        return result.toString();
    }

    /**
     * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {}

    /**
     * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
     * @param model
     */
    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("author", "Tom");
    }
}