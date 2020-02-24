package com.xuliang.lcn.txmsg.dto;

import lombok.Data;

import java.util.Date;

/**
 * Description: 服务应用注册信息
 * Company: wanba
 * Date: 2019/1/17
 *
 * @author xuliang
 */
@Data
public class AppInfo {


    /**
     * 应用名称
     */
    private String appName;

    /**
     * TC标识名称
     */
    private String labelName;


    private Date createTime;


}
