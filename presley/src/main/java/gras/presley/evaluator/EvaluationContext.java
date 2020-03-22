/*
 * EvaluationContext
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.evaluator;

import gras.berry.collection.ExtendedArray;
import gras.berry.collection.ReadOnlyList;
import gras.presley.ctx.ApplicationContext;
import gras.presley.metadata.BeanType;
import gras.presley.metadata.PlainBeanProp;

import java.util.List;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class EvaluationContext<_B, _P> {

    private _B bean;
    private BeanType<_B> beanMetadata;
    private PlainBeanProp<_B, _P> propertyMetadata;
    private EvaluatedState state;
    private MessageBuilder messages;

    public EvaluationContext(_B bean, BeanType<_B> beanMetadata, EvaluatedState state) {
        this.bean = bean;
        this.beanMetadata = beanMetadata;
        this.propertyMetadata = null;
        this.state = state;
    }

    public EvaluationContext(_B bean, PlainBeanProp<_B, _P> propertyMetadata, EvaluatedState state) {
        this.bean = bean;
        this.beanMetadata = null;
        this.propertyMetadata = propertyMetadata;
        this.state = state;
    }

    public _B getBean() {
        return bean;
    }

    public BeanType<_B> getBeanMetadata() {
        return beanMetadata;
    }

    public PlainBeanProp<_B, _P> getPropertyMetadata() {
        return propertyMetadata;
    }

    public EvaluatedState getState() {
        return state;
    }

    public MessageBuilder getMessages() {
        return messages;
    }

    public ApplicationContext getApplicationContext() {
        return null; // comes from the current session?
    }

    ReadOnlyList<EvaluatorInfo<_B, _P>> getEvaluators() {
        ReadOnlyList<EvaluatorInfo> evaluators =
                propertyMetadata != null ?
                (ReadOnlyList) MetadataAccess.getEvaluators(propertyMetadata) :
                (ReadOnlyList) MetadataAccess.getEvaluators(beanMetadata);

        if (evaluators == null) {
            List<EvaluatorInfo<_B, _P>> all = EvaluatorRegistry.instance().getEvaluators();
            ExtendedArray supportedEvaluators = new ExtendedArray<>();
            for (int i = all.size(); --i >= 0; ) {
                EvaluatorInfo<_B, _P> evaluator = all.get(i);
                if (propertyMetadata == null && !evaluator.supports(beanMetadata))
                    continue;
                if (propertyMetadata != null && !evaluator.supports(propertyMetadata))
                    continue;

                supportedEvaluators.add(evaluator);
            }

            evaluators = new ReadOnlyList<>(supportedEvaluators);
            if (propertyMetadata != null)
                MetadataAccess.setEvaluators(propertyMetadata, (ReadOnlyList) evaluators);
            else
                MetadataAccess.setEvaluators(beanMetadata, (ReadOnlyList) evaluators);
        }
        return (ReadOnlyList) evaluators;
    }

    boolean evaluate() {
        List<EvaluatorInfo<_B, _P>> evaluators = getEvaluators();

        boolean returnOnFirst = !state.isMessageState(); // we need to run all the evaluators to get all the messages
        boolean startState = state.isInitial();
        boolean actState = startState;

        for (int i = evaluators.size(); --i >= 0; ) {
            EvaluatorInfo<_B, _P> evaluator = evaluators.get(i);
            if (!evaluator.supports(state, bean))
                continue;

            boolean evaluatedState = evaluator.getInstance().evaluateState(this);

            if (returnOnFirst && evaluatedState != startState)
                return evaluatedState;
            else
                actState = actState | evaluatedState;
        }

        return actState;
    }

    private static final class MetadataAccess extends gras.presley.metadata.PackageAccess {

        protected static <_B> ReadOnlyList<EvaluatorInfo<_B, ?>> getEvaluators(BeanType<_B> beanType) {
            return gras.presley.metadata.PackageAccess.getEvaluators(beanType);
        }

        protected static <_B> void setEvaluators(BeanType<_B> beanType, ReadOnlyList<EvaluatorInfo<_B, ?>> evaluators) {
            gras.presley.metadata.PackageAccess.setEvaluators(beanType, evaluators);
        }

        protected static <_B, _P> ReadOnlyList<EvaluatorInfo<_B, _P>> getEvaluators(PlainBeanProp<_B, _P> prop) {
            return gras.presley.metadata.PackageAccess.getEvaluators(prop);
        }

        protected static <_B, _P> void setEvaluators(PlainBeanProp<_B, _P> prop, ReadOnlyList<EvaluatorInfo<_B, _P>> evaluators) {
            gras.presley.metadata.PackageAccess.setEvaluators(prop, evaluators);
        }
    }
}
