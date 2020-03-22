/*
 * PersonSEvaluator
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.example.evaluator;

import gras.presley.evaluator.EvaluatedState;
import gras.presley.evaluator.EvaluationContext;
import gras.presley.evaluator.Evaluator;
import gras.presley.evaluator.EvaluatorDef;
import gras.presley.example.bean.Person;
import gras.presley.example.ctx.User;
import gras.presley.interceptor.PropertyDef;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PersonSEvaluator implements Evaluator<Person, Object> {

    @Override
    @EvaluatorDef(states = {EvaluatedState.VISIBLE, EvaluatedState.EDITABLE})
    @PropertyDef(name = "weight")
    public boolean evaluateState(EvaluationContext<Person, Object> ctx) {
        switch (ctx.getState()) {
            case VISIBLE:
                return evaluateVisible(ctx);
            case EDITABLE:
                return evaluateEditable(ctx);
            default:
                return ctx.getState().getDefault();
        }
    }

    private boolean evaluateVisible(EvaluationContext<Person, Object> ctx) {
        User currentUser = null; // ctx.getApplicationContext().getCurrentUser();
        return ctx.getBean().team.name.equals(currentUser.team);
    }

    private boolean evaluateEditable(EvaluationContext<Person, Object> ctx) {
        User currentUser = null; // ctx.getApplicationContext().getCurrentUser();
        return ctx.getBean().team.name.equals(currentUser.team) && currentUser.teamManager;
    }
}
