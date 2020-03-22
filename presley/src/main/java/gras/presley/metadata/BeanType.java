/*
 * BeanType
 * Create Date: 2020. 03. 20.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.metadata;

import gras.berry.collection.ReadOnlyList;
import gras.presley.evaluator.EvaluatorInfo;
import gras.presley.interceptor.BeanInterceptorInfo;
import gras.presley.operation.BeanOperationInfo;
import gras.presley.operation.TypeOperationInfo;
import lombok.NonNull;

/**
 * A java type that has properties.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class BeanType<_B> extends JavaType<_B> {

    private final String label;
    private final boolean defaultVisible;
    private final boolean defaultEditable;

    /** lazy initialized property list */
    PropertyList<PlainBeanProp<_B, ?>> properties;

    private ReadOnlyList<BeanInterceptorInfo<_B>> interceptors;
    private ReadOnlyList<EvaluatorInfo<_B, ?>> evaluators;
    private ReadOnlyList<TypeOperationInfo<_B>> typeOperations;
    private ReadOnlyList<BeanOperationInfo<_B>> beanOperations;

    BeanType(Class<_B> javaType) {
        super(javaType);

        BeanClass bc = getAnnotation(BeanClass.class);
        this.label = bc != null ? bc.label() : getSimpleName();
        this.defaultVisible = bc == null || bc.visible();
        this.defaultEditable = bc == null || bc.editable();
    }

    @Override
    public String getLabel() {
        return label;
    }

    /**
     * Default value for VISIBLE evaluation, if false, bean type is never visible for any user.
     */
    public final boolean isDefaultVisible() {
        return defaultVisible;
    }

    /**
     * Default value for EDITABLE evaluation, if false, bean type is never editable for any user.
     */
    public final boolean isDefaultEditable() {
        return defaultEditable;
    }

    /**
     * Gets all properties of this type.
     */
    @NonNull
    public final PropertyList<PlainBeanProp<_B, ?>> getProperties() {
        if (properties != null)
            return properties;
        return TypeManager.instance().buildBeanProperties(this);
    }

    /**
     * Gets a property by index.
     */
    @NonNull
    public final PlainBeanProp<_B, ?> getProperty(int index) {
        return getProperties().get(index);
    }

    /**
     * Gets a property of this type that has the given name.
     */
    @NonNull
    public final PlainBeanProp<_B, ?> getProperty(@NonNull String name) {
        PlainBeanProp property = getProperties().getByName(name);
        if (property != null)
            return property;

        throw new IllegalArgumentException("Property not found " + this + '.' + name);
    }

    protected final ReadOnlyList<BeanInterceptorInfo<_B>> getInterceptors() {
        return interceptors;
    }

    protected final void setInterceptors(ReadOnlyList<BeanInterceptorInfo<_B>> interceptors) {
        this.interceptors = interceptors;
    }

    protected final ReadOnlyList<EvaluatorInfo<_B, ?>> getEvaluators() {
        return evaluators;
    }

    protected final void setEvaluators(ReadOnlyList<EvaluatorInfo<_B, ?>> evaluators) {
        this.evaluators = evaluators;
    }

    protected final ReadOnlyList<TypeOperationInfo<_B>> getTypeOperations() {
        return typeOperations;
    }

    protected final void setTypeOperations(ReadOnlyList<TypeOperationInfo<_B>> typeOperations) {
        this.typeOperations = typeOperations;
    }

    protected final ReadOnlyList<BeanOperationInfo<_B>> getBeanOperations() {
        return beanOperations;
    }

    protected final void setBeanOperations(ReadOnlyList<BeanOperationInfo<_B>> beanOperations) {
        this.beanOperations = beanOperations;
    }

    @Override
    public String toString() {
        return getTypeName();
    }
}
