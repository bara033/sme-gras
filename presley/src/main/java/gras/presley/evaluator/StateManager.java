/*
 * StateManager
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.evaluator;

import gras.presley.ctx.ApplicationContext;
import gras.presley.metadata.BeanType;
import gras.presley.metadata.PlainBeanProp;
import gras.presley.metadata.TypeManager;
import lombok.NonNull;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class StateManager {

    protected StateManager() {
    }

    @NonNull
    public StateManager instance() {
        return ApplicationContext.getStateManager();
    }

    public <_B> boolean evaluateState(_B bean, BeanType<_B> beanMetadata, EvaluatedState state) {
        EvaluationContext<_B, ?> context = new EvaluationContext<>(bean, beanMetadata, state);
        return context.evaluate();
    }

    public <_B> boolean isVisible(_B bean) {
        return evaluateState(bean, (BeanType<_B>) TypeManager.forBean(bean), EvaluatedState.VISIBLE);
    }

    public <_B> boolean isEditable(_B bean) {
        return evaluateState(bean, (BeanType<_B>) TypeManager.forBean(bean), EvaluatedState.EDITABLE);
    }

    public <_B> boolean hasError(_B bean) {
        return evaluateState(bean, (BeanType<_B>) TypeManager.forBean(bean), EvaluatedState.ERRORS);
    }

    public <_B> MessageBuilder evaluateMessages(_B bean, BeanType<_B> beanMetadata, EvaluatedState state) {
        EvaluationContext<_B, ?> context = new EvaluationContext<>(bean, beanMetadata, state);
        return context.getMessages();
    }

    public <_B> MessageBuilder getErrors(_B bean) {
        return evaluateMessages(bean, (BeanType<_B>) TypeManager.forBean(bean), EvaluatedState.ERRORS);
    }

    public <_B> MessageBuilder getWarnings(_B bean) {
        return evaluateMessages(bean, (BeanType<_B>) TypeManager.forBean(bean), EvaluatedState.WARNINGS);
    }

    /**
     * Property level evaluation, that we can wire out also into the PropertyMetaData for convenience.
     */
    public <_B, _P> boolean evaluateState(_B bean, PlainBeanProp<_B, _P> propertyMetadata, EvaluatedState state) {
        EvaluationContext<_B, _P> context = new EvaluationContext<>(bean, propertyMetadata, state);
        return context.evaluate();
    }

    public <_B, _P> boolean isVisible(_B bean, PlainBeanProp<_B, _P> propertyMetadata) {
        return evaluateState(bean, propertyMetadata, EvaluatedState.VISIBLE);
    }

    public <_B, _P> boolean isEditable(_B bean, PlainBeanProp<_B, _P> propertyMetadata) {
        return evaluateState(bean, propertyMetadata, EvaluatedState.EDITABLE);
    }

    public <_B, _P> boolean isMandatory(_B bean, PlainBeanProp<_B, _P> propertyMetadata) {
        return evaluateState(bean, propertyMetadata, EvaluatedState.MANDATORY);
    }

    public <_B, _P> MessageBuilder evaluateMessages(_B bean, PlainBeanProp<_B, _P> propertyMetadata, EvaluatedState state) {
        EvaluationContext<_B, ?> context = new EvaluationContext<>(bean, propertyMetadata, state);
        return context.getMessages();
    }

    public <_B, _P> MessageBuilder getErrors(_B bean, PlainBeanProp<_B, _P> propertyMetadata) {
        return evaluateMessages(bean, propertyMetadata, EvaluatedState.ERRORS);
    }

    public <_B, _P> MessageBuilder getWarnings(_B bean, PlainBeanProp<_B, _P> propertyMetadata) {
        return evaluateMessages(bean, propertyMetadata, EvaluatedState.WARNINGS);
    }
}
