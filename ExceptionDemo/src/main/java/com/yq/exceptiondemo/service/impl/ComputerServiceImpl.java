

package com.yq.exceptiondemo.service.impl;

import com.yq.exceptiondemo.exception.ComputerException;
import com.yq.exceptiondemo.service.ComputerService;
import com.yq.exceptiondemo.utils.Constants;
import org.springframework.stereotype.Service;

/**
 */
@Service
public class ComputerServiceImpl implements ComputerService {

    @Override
    public int devide(int a, int b) {
        int result = a / b;
        return result;
    }

    @Override
    public int devideCatchExecption(int a, int b) throws ComputerException {
        int result = 0;
        try {
            result = a / b;
        }
        catch (Exception ex) {
            throw new ComputerException(ex.getMessage() + ", a:" + a + ", b:" + b);
        }

        return result;
    }
}
