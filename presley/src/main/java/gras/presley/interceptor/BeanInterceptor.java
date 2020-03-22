/*
 * BeanInterceptor
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

/**
 * Interceptor invoked on persistent bean level.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface BeanInterceptor<_B> {

    /**
     * This is invoked/executed by the framework.
     * The implementation must call <code>invocation.proceed()</code> to continue the interceptor chain.
     * The implementation may throw any Exception in which case the interceptor chain is aborted.
     */
    void intercept(BeanInvocation<_B> invocation) throws Exception;
}
