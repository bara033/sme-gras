/*
 * BeanOperationInfo
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import gras.presley.metadata.BeanType;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class BeanInterceptorInfo<_B> {

    final BeanInterceptor<_B> interceptor;
    final BeanType<_B> beanMetadata;

    BeanInterceptorInfo(BeanInterceptor<_B> interceptor) {
        this.interceptor = interceptor;
        this.beanMetadata = null; // here comes the reflective inspection of the given operation instance
    }

    public boolean supports(BeanType<?> metadata) {
        return metadata.isAssignableFrom(beanMetadata);
    }

    public boolean supports(_B bean, BeanOperation operation) {
        return true;
    }

    BeanInterceptor<_B> getInstance() {
        return interceptor;
    }
}
