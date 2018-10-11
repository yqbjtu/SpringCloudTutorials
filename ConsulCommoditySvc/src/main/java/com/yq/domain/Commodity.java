

package com.yq.domain;

import lombok.Data;

import java.util.Date;

/**
 * Simple to Introduction
 * className: Commodity
 *
 * @author EricYang
 * @version 2018/6/30 0:13
 */
@Data
public class Commodity {
    String id;
    String name;
    String description;
    String price;
    String count;
    String picUrl;
    Date onlineDate;
}
