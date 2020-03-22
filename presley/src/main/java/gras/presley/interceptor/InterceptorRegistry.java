/*
 * InterceptorRegistry
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import gras.presley.ctx.ApplicationContext;

import java.util.List;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface InterceptorRegistry {

    <_B> List<BeanInterceptorInfo<_B>> getBeanInterceptors();

    <_B, _P> List<PropertyInterceptorInfo<_B, _P>> getPropertyInterceptors();

    static InterceptorRegistry instance() {
        return ApplicationContext.getInterceptorRegistry();
    }
}
