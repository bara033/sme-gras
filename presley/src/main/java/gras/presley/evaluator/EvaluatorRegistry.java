/*
 * EvaluatorRegistry
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.evaluator;

import gras.presley.ctx.ApplicationContext;

import java.util.List;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface EvaluatorRegistry {

    <_B, _P> List<EvaluatorInfo<_B, _P>> getEvaluators();

    static EvaluatorRegistry instance() {
        return ApplicationContext.getEvaluatorRegistry();
    }
}
