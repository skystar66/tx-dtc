package com.xuliang.lcn.common.util;

import java.util.Objects;
import java.util.UUID;

public class RandomUtils {



    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String simpleKey() {
        return String.valueOf(System.nanoTime());
    }



}
