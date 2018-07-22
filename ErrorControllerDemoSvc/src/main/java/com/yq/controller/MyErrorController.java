

package com.yq.controller;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


//@RestController
@Controller
public class MyErrorController implements ErrorController {

//    @Autowired
//    private ErrorAttributes errorAttributes;

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "myError";
    }


    @Override
    public String getErrorPath() {
        return PATH;
    }


}