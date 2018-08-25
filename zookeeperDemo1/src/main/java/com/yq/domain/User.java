package com.yq.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Simple to Introduction
 * className: User
 *
 * @author EricYang
 * @version 2018/8/24 23:57
 */
@Data
public class User implements Serializable {

    private Integer id;
    private String name;
}