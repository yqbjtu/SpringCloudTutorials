

package com.yq.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    Date regDate;
}
