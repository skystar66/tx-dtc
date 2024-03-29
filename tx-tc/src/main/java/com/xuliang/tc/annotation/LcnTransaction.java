package com.xuliang.tc.annotation;



import com.xuliang.lcn.common.enums.TransactionPropagation;

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
     * @see TransactionPropagation
     */
    TransactionPropagation propagation() default TransactionPropagation.REQUIRED;


}
