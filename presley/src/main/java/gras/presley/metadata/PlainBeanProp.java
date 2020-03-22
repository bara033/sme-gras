/*
 * PropertyMetadata
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.metadata;

import gras.berry.ReflectionUtils;
import gras.berry.collection.ReadOnlyList;
import gras.presley.evaluator.EvaluatorInfo;
import gras.presley.interceptor.PropertyInterceptorInfo;
import gras.presley.operation.PropertyOperationInfo;
import lombok.NonNull;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Describes a property on a bean and gives access to the bean's property.
 *
 * <p/> This is similar to java.beans.PropertyDescriptor, but it is immutable. So
 * it accesses the same property all the time.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PlainBeanProp<_B, _P> implements Serializable {

    protected static final Logger log = Logger.getLogger(PlainBeanProp.class.getName());

    @NonNull private final BeanType<_B> beanType;
    protected int index; // should not change, but we need to set them for un-indexed properties
    @NonNull private final String propertyName;

    private final Field field;
    private final Method getter;
    private final Method setter;
    private final boolean fieldAnnotated;

    private final JavaType<_P> valueType;
    private final JavaType<_P> elementType;

    private final String label;
    private final boolean defaultVisible;
    private final boolean defaultEditable;
    private final boolean defaultMandatory;

    private ReadOnlyList<PropertyInterceptorInfo<_B, _P>> interceptors;
    private ReadOnlyList<EvaluatorInfo<_B, _P>> evaluators;
    private ReadOnlyList<PropertyOperationInfo<_B, _P>> operations;

    public PlainBeanProp(@NonNull BeanType<_B> beanType, boolean fieldAnnotated, Field field, Method getter, Method setter) {
        this.beanType = beanType;
        this.field = field;
        this.getter = getter;
        this.setter = setter;
        this.fieldAnnotated = fieldAnnotated;

        Class returnType = getter != null ? getter.getReturnType() : field.getType();
        valueType = (JavaType<_P>) TypeManager.forClass(returnType);

        Class elementType = null;
        if (List.class.isAssignableFrom(returnType) || Set.class.isAssignableFrom(returnType)) {
            elementType = ReflectionUtils.getCollectionElementType(fieldAnnotated ? field.getGenericType() : getter.getGenericReturnType());
            if (elementType == null)
                log.warning("Bean property " + beanType + '.' + (fieldAnnotated ? field.getName() : getter.getName()) + " is NOT a valid getter. No element type found.");
        }
        else if (Map.class.isAssignableFrom(returnType))
            elementType = Map.Entry.class;
        else if (returnType.isArray())
            elementType = returnType.getComponentType();
        this.elementType = elementType != null ? TypeManager.forClass(elementType) : null;

        if (field != null)
            field.setAccessible(true);
        if (getter != null)
            getter.setAccessible(true);
        if (setter != null)
            setter.setAccessible(true);

        propertyName = ReflectionUtils.getPropertyName(fieldAnnotated ? field.getName() : getter.getName());

        BeanProp anno = getAnnotation(BeanProp.class);

        String label = anno.label();
        this.label = "".equals(label) ? propertyName : label;
        this.defaultVisible = anno.visible();
        this.defaultEditable = anno.editable();
        this.defaultMandatory = anno.mandatory();
    }

    /**
     * Returns the none null bean type of the bean where this property is placed at.
     */
    @NonNull
    public final BeanType<_B> getBeanType() {
        return beanType;
    }

    /**
     * Returns the zero based index of this instances in {@link #getBeanType()}
     */
    public final int getIndex() {
        return index;
    }

    void setIndex(int index) {
        this.index = index;
    }

    @NonNull
    public final String getPropertyName() {
        return propertyName;
    }

    public final String getLabel() {
        return label;
    }

    /**
     * Optional field.
     * Can be {@code null} if {@link #getGetter()} is defined.
     */
     public final Field getField() {
        return field;
     }

    /**
     * Optional getter method.
     * Can be {@code null} if {@link #getField()} is defined.
     */
     public final Method getGetter() {
        return getter;
    }

    /**
     * Optional setter method.
     */
    public final Method getSetter() {
        return setter;
    }

    /**
     * Returns non null member represented by this property instance to be used for fetching annotations.
     */
    @NonNull
    final Member getAnnotatedMember() {
        return fieldAnnotated ? field : getter;
    }

    /**
     * Returns the none null java type of the property value.
     */
    public final JavaType<_P> getValueType() {
        return valueType;
    }

    /**
     * If the property is of type Map, Set or List then this method returns the element type of the collection.
     * The value type for Map and the element type for List.
     * Otherwise null is returned.
     */
    public final JavaType<_P> getElementType() {
        return elementType;
    }

    /**
     * Returns default value for VISIBLE evaluation, if false, property is never visible for the user.
     */
    public final boolean isDefaultVisible() {
        return defaultVisible;
    }

    /**
     * Returns default value for EDITABLE evaluation, if false, property is never editable for the user.
     */
    public final boolean isDefaultEditable() {
        return defaultEditable;
    }

    /**
     * Returns default value for logical MANDATORY evaluation, if true, non null value is required for the prop.
     * NOTE: for property path it is false if one of the properties is optional!
     */
    public final boolean isDefaultMandatory() {
        return defaultMandatory;
    }

    public _P getValue(_B bean) {
        try {
            return getter != null ? (_P) getter.invoke(bean, (Object[]) null) : (_P) field.get(bean);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to get " + this, e);
        }
    }

    public void setValue(_B bean, _P value) {
        try {
            if (setter != null)
                setter.invoke(bean, value);
            else
                field.set(bean, value);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to set " + this, e);
        }
    }

    protected final ReadOnlyList<PropertyInterceptorInfo<_B, _P>> getInterceptors() {
        return interceptors;
    }

    protected final void setInterceptors(ReadOnlyList<PropertyInterceptorInfo<_B, _P>> interceptors) {
        this.interceptors = interceptors;
    }

    protected final ReadOnlyList<EvaluatorInfo<_B, _P>> getEvaluators() {
        return evaluators;
    }

    protected final void setEvaluators(ReadOnlyList<EvaluatorInfo<_B, _P>> evaluators) {
        this.evaluators = evaluators;
    }

    protected final ReadOnlyList<PropertyOperationInfo<_B, _P>> getOperations() {
        return operations;
    }

    protected final void setOperations(ReadOnlyList<PropertyOperationInfo<_B, _P>> operations) {
        this.operations = operations;
    }

    /**
     * Returns the declared annotation for this property.
     */
    protected final <_A extends Annotation> _A getAnnotation(Class<_A> annoClass) {
        return fieldAnnotated ? field.getAnnotation(annoClass) : getter.getAnnotation(annoClass);
    }

    static final class PlainSerialReplacement implements Serializable {
        private static final long serialVersionUID = 1L;

        private transient String beanClassName;
        private transient int    propIndex;

        PlainSerialReplacement(String beanClassName, int propIndex) {
            this.beanClassName = beanClassName;
            this.propIndex = propIndex;
        }

        @NonNull
        protected PlainBeanProp readResolve() throws ObjectStreamException {
            return ((BeanType)TypeManager.forClassName(beanClassName)).getProperty(propIndex);
        }

        private void writeObject(@NonNull ObjectOutputStream out) throws IOException {
            out.writeObject(beanClassName);
            out.writeInt(propIndex);
        }

        private void readObject(@NonNull ObjectInputStream in) throws IOException, ClassNotFoundException {
            beanClassName = (String) in.readObject();
            propIndex = in.readInt();
        }
    }

    /**
     * Serialize only the reference and then lookup the reference again on deserialization.
     */
    @NonNull
    protected final PlainSerialReplacement writeReplace() throws ObjectStreamException {
        return new PlainSerialReplacement(getBeanType().getTypeName(),getIndex());
    }

    @Override
    public String toString() {
        return beanType.getTypeName() + '.' + getPropertyName();
    }
}
