

package com.yq.domain;

import lombok.Data;

import java.util.Date;

/**
 * Simple to Introduction
 * className: User
 *
 * @author EricYang
 * @version 2018/6/29 23:43
 */
@Data
public class User {
    String id;
    String name;
    String mail;
    Date regDate;
}
