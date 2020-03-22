/*
 * PropertyInterceptor
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface PropertyInterceptor<_B, _P> {

    /**
     * Called by interceptor manager for all matching property setter calls.
     * The implementation must call <code>invocation.proceed()</code> to continue the interceptor chain.
     * The implementation may throw any Exception in which case the interceptor chain is aborted.
     */
    void intercept(PropertyInvocation<_B, _P> invocation) throws Exception;
}
