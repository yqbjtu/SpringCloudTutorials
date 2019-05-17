

package com.yq.exceptiondemo.service;

import com.yq.exceptiondemo.exception.ComputerException;

/**
 * Simple to Introduction
 * className: ComputerService

 */
public interface ComputerService {
    int divide(int a, int b);

    int divideCatchExecption(int a, int b) throws ComputerException;
}