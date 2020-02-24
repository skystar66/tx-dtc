package com.xuliang.tc.annotation;


import com.xuliang.tc.aspect.enums.DTXPropagation;

import java.lang.annotation.*;

/**
 * Description: type [lcn] of DTX
 * Date: 1/4/19
 *
 * @author xuliang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LcnTransaction {


    /**
     * 分布式事务传播行为
     *
     * @return 传播行为
     * @see DTXPropagation
     */
    DTXPropagation propagation() default DTXPropagation.REQUIRED;


}
