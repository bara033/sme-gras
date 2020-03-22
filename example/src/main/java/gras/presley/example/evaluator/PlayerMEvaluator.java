/*
 * PlayerMEvaluator
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.example.evaluator;

import gras.presley.evaluator.EvaluationContext;
import gras.presley.evaluator.Evaluator;
import gras.presley.evaluator.MessageSeverity;
import gras.presley.example.bean.Person;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PlayerMEvaluator implements Evaluator<Person, Object> {

    @Override
    public boolean evaluateState(EvaluationContext<Person, Object> ctx) {
        Person person = ctx.getBean();
        if (person.isPlayer && person.weight < 80)
            ctx.getMessages().addMessage("This guy will die on the first mach.", MessageSeverity.WARNING);

        return true; // this is not nice, message evaluator should not return value, we need an other interface for them.
    }
}
