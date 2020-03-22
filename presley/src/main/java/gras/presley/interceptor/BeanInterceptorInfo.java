/*
 * BeanOperationInfo
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import gras.berry.ReflectionUtils;
import gras.presley.metadata.BeanType;

import java.lang.reflect.Method;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class BeanInterceptorInfo<_B> extends InterceptorInfo<BeanInterceptor<_B>> {

    BeanInterceptorInfo(BeanInterceptor<_B> interceptor) {
        super(interceptor);
    }

    @Override
    protected Method getInterceptMethod() {
        return ReflectionUtils.getDeclaredMethod(getInterceptor().getClass(), "intercept", BeanInvocation.class);
    }

    public boolean supports(BeanType<?> target) {
        return super.supports(target);
    }

    public boolean supports(_B bean, BeanOperation operation) {
        return true;
    }
}
