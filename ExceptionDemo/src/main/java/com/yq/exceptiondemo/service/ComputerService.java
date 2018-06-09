

package com.yq.exceptiondemo.service;

import com.yq.exceptiondemo.exception.ComputerException;

/**
 * Simple to Introduction
 * className: ComputerService

 */
public interface ComputerService {
    int devide(int a, int b);

    int devideCatchExecption(int a, int b) throws ComputerException;
}