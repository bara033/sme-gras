/*
 * TargetProperty
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyDef {

    String[] name() default {"*"};
}
