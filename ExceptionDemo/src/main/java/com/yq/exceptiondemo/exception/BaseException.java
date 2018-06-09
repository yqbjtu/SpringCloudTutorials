

package com.yq.exceptiondemo.exception;

import lombok.Data;

@Data
public class BaseException extends Throwable {
    private int code;
    private String myCause;


    public BaseException(int code, String myCause) {
        this.code = code;
        this.myCause = myCause;

    }
}
