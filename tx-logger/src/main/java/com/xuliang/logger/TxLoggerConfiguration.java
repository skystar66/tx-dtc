/*
 * Copyright 2017-2019 CodingApi .
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xuliang.logger;

import com.xuliang.lcn.common.util.SpringUtils;
import com.xuliang.logger.db.LogDbHelper;
import com.xuliang.logger.db.LogDbProperties;
import com.xuliang.logger.exception.TxLoggerException;
import com.xuliang.logger.helper.MysqlLoggerHelper;
import com.xuliang.logger.helper.TxLcnLogDbHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/26
 *
 * @author codingapi
 */
@ComponentScan
@Configuration
public class TxLoggerConfiguration {

    @Configuration
    @ConditionalOnProperty(name = "tx-lcn.logger.enabled", havingValue = "true")
    class LoggerEnabledTrueConfig {

        @Bean
        public LogDbHelper logDbHelper(LogDbProperties logDbProperties) throws TxLoggerException {
            return new LogDbHelper(logDbProperties);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public TxLcnLogDbHelper txLcnLoggerHelper() {
        return new MysqlLoggerHelper();
    }

    @Bean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }
}
