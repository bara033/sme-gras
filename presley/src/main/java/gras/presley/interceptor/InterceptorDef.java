/*
 * InterceptorDef
 * Create Date: 2020. 03. 22.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import java.lang.annotation.*;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InterceptorDef {

    /**
     * the position of this interceptor within the interceptor chain. Defaults to {@link InterceptorPosition#DEFAULT}.
     */
    InterceptorPosition position() default InterceptorPosition.DEFAULT;

    /**
     * if an interceptor method implementation may not call the <code>invocation.proceed()</code> method then this must be set to true.
     * Otherwise a warning log entry will be written. Defaults to false.
     */
    boolean mayNotProceed() default false;
}
