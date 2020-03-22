/*
 * EvaluatorDef
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.evaluator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EvaluatorDef {

    EvaluatedState[] states();
}
