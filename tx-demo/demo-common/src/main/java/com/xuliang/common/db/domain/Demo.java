package com.xuliang.common.db.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.math.RandomUtils;

import java.util.Date;

/**
 * Description:
 * Date: 2018/12/25
 *
 * @author ujued
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Demo {
    private Long id;
    private String kid = String.valueOf(RandomUtils.nextInt(10000));
    private String demoField;
    private String groupId;
    private Date createTime;
    private String appName;

}
