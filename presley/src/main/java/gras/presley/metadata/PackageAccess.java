/*
 * PackageAccess
 * Create Date: 2020. 03. 22.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.metadata;

import gras.berry.collection.ReadOnlyList;
import gras.presley.evaluator.EvaluatorInfo;
import gras.presley.interceptor.BeanInterceptorInfo;
import gras.presley.interceptor.PropertyInterceptorInfo;
import gras.presley.operation.BeanOperationInfo;
import gras.presley.operation.PropertyOperationInfo;
import gras.presley.operation.TypeOperationInfo;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PackageAccess {

    protected PackageAccess() {
    }

    protected static <_B> ReadOnlyList<BeanInterceptorInfo<_B>> getInterceptors(BeanType<_B> beanType) {
        return beanType.getInterceptors();
    }

    protected static <_B> void setInterceptors(BeanType<_B> beanType, ReadOnlyList<BeanInterceptorInfo<_B>> interceptors) {
        beanType.setInterceptors(interceptors);
    }

    protected static <_B> ReadOnlyList<EvaluatorInfo<_B, ?>> getEvaluators(BeanType<_B> beanType) {
        return beanType.getEvaluators();
    }

    protected static <_B> void setEvaluators(BeanType<_B> beanType, ReadOnlyList<EvaluatorInfo<_B, ?>> evaluators) {
        beanType.setEvaluators(evaluators);
    }

    protected static <_B> ReadOnlyList<TypeOperationInfo<_B>> getTypeOperations(BeanType<_B> beanType) {
        return beanType.getTypeOperations();
    }

    protected static <_B> void setTypeOperations(BeanType<_B> beanType, ReadOnlyList<TypeOperationInfo<_B>> typeOperations) {
        beanType.setTypeOperations(typeOperations);
    }

    protected static <_B> ReadOnlyList<BeanOperationInfo<_B>> getBeanOperations(BeanType<_B> beanType) {
        return beanType.getBeanOperations();
    }

    protected static <_B> void setBeanOperations(BeanType<_B> beanType, ReadOnlyList<BeanOperationInfo<_B>> beanOperations) {
        beanType.setBeanOperations(beanOperations);
    }

    protected static <_B, _P> ReadOnlyList<PropertyInterceptorInfo<_B, _P>> getInterceptors(PlainBeanProp<_B, _P> prop) {
        return prop.getInterceptors();
    }

    protected static <_B, _P> void setInterceptors(PlainBeanProp<_B, _P> prop, ReadOnlyList<PropertyInterceptorInfo<_B, _P>> interceptors) {
        prop.setInterceptors(interceptors);
    }

    protected static <_B, _P> ReadOnlyList<EvaluatorInfo<_B, _P>> getEvaluators(PlainBeanProp<_B, _P> prop) {
        return prop.getEvaluators();
    }

    protected static <_B, _P> void setEvaluators(PlainBeanProp<_B, _P> prop, ReadOnlyList<EvaluatorInfo<_B, _P>> evaluators) {
        prop.setEvaluators(evaluators);
    }

    protected static <_B, _P> ReadOnlyList<PropertyOperationInfo<_B, _P>> getOperations(PlainBeanProp<_B, _P> prop) {
        return prop.getOperations();
    }

    protected static <_B, _P> void setOperations(PlainBeanProp<_B, _P> prop, ReadOnlyList<PropertyOperationInfo<_B, _P>> operations) {
        prop.setOperations(operations);
    }
}
