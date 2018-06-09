

package com.yq.exceptiondemo.exception;

import com.yq.exceptiondemo.utils.Constants;
import lombok.Data;

/**
 */
@Data
public class ComputerException extends BaseException {

    public ComputerException(String cause) {
        super(Constants.DEVIDE_ERROR, cause);
    }
}
