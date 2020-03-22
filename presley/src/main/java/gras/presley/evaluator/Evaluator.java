/*
 * StateEvaluator
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.evaluator;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface Evaluator<_B, _P> {

    /**
     * Evaluate the given state type on the given evaluable object.
     * If the implementation is not able to evaluate the given state, then it <b>must</b> return {@link EvaluatedState#getDefault}.
     *
     * @return the new state.
     */
    boolean evaluateState(EvaluationContext<_B, _P> ctx);
}
