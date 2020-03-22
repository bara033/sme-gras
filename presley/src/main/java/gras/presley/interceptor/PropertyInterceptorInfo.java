/*
 * PropertyOperationInfo
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import gras.berry.ReflectionUtils;
import gras.berry.collection.ExtendedArray;
import gras.presley.metadata.JavaType;
import gras.presley.metadata.PlainBeanProp;
import gras.presley.metadata.PropertyPattern;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PropertyInterceptorInfo<_B, _P> extends InterceptorInfo<PropertyInterceptor<_B, _P>> {

    /** constant internally used if no property names are defined */
    private static final String[] NO_NAMES = {};

    /** constant internally used if no property names are defined */
    private static final PropertyPattern[] NO_PATTERNS = {};

    /** Holds the property names on which this interceptor should be invoked */
    @NonNull
    private final String[] names;

    /** Holds an array of patterns to match the property name */
    @NonNull
    private final PropertyPattern[] propertyPatterns;

    /** Holds the type of the property on which the interceptor is invoked */
    @NonNull
    private final Class<?>[] propertyTypes;


    PropertyInterceptorInfo(PropertyInterceptor<_B, _P> interceptor) {
        super(interceptor);

        PropertyInterceptorDef def = getInterceptMethod().getAnnotation(PropertyInterceptorDef.class);
        if ( def != null ) {
            names = def.names();
            propertyPatterns = new PropertyPattern[names.length];
            for ( int i = names.length; --i >= 0; )
                propertyPatterns[i] = PropertyPattern.compile(names[i]);
        }
        else {
            names = NO_NAMES;
            propertyPatterns = NO_PATTERNS;
        }

        propertyTypes = buildPropertyTypes();
    }

    @Override
    protected Method getInterceptMethod() {
        return ReflectionUtils.getDeclaredMethod(getInterceptor().getClass(), "intercept", PropertyInvocation.class);
    }

    public boolean supports(PlainBeanProp<?, ?> target) {
        return super.supports(target.getBeanType()) &&
                supportsPropertyType(target.getValueType()) &&
                supportsPropertyName(target);
    }

    /**
     * Checks whether the given class is assignable from all property types. This is used by the <code>InterceptorManager</code>
     * to find out if this interceptor fits to the <code>JdoPropertyMetaData</code> to which this interceptor will be added.
     * @param valueType a type to check against the property type
     * @return a boolean which is true when the given class is assignable from the internal property type
     */
    public final boolean supportsPropertyType(@NonNull JavaType<?> valueType) {
        if (valueType.isPrimitive())
            valueType = valueType.getWrapperType();
        Class valueClass = valueType.getTypeClass();

        for (Class<?> targetClass: propertyTypes) {
            if (!targetClass.isAssignableFrom(valueClass))
                return false;
        }
        return true;
    }

    /**
     * Fits whether the given propertyName (either qualified or simple) fits to the propertyPatterns which are given
     * in the annotations of this interceptor. This is used by the <code>InterceptorManager</code> to find out if this
     * interceptor fits to the <code>JdoPropertyMetaData</code> to which this interceptor will be added.
     * @param prop the property to check if matches
     * @return true if the name matches one of the names given by the annotation
     */
    public final boolean supportsPropertyName(@NonNull PlainBeanProp<?, ?> prop) {
        if (propertyPatterns == null || propertyPatterns.length == 0)
            return true;

        for (int i = propertyPatterns.length; i > 0; )
            if (propertyPatterns[--i].matches(prop))
                return true;
        return false;
    }

    public boolean supports(_B bean) {
        return true;
    }

    /**
     * This method determines the type of the property on which this interceptor works. The type is retrieved via
     * reflection from the generics definition of the interceptor class.
     */
    @NonNull
    private Class<?>[] buildPropertyTypes() {
        ExtendedArray<Class<?>> tmpPropertyTypes = new ExtendedArray<>();
        Type targetInterface = getTargetInterface();
        if (targetInterface instanceof ParameterizedType) {
            Type targetType = ((ParameterizedType) targetInterface).getActualTypeArguments()[1];
            if (targetType instanceof Class) {
                tmpPropertyTypes.add((Class) targetType);
            }
            else if (targetType instanceof ParameterizedType) {
                ParameterizedType paraType = (ParameterizedType) targetType;
                tmpPropertyTypes.add((Class) paraType.getRawType());
                Type targetObjectType = paraType.getActualTypeArguments()[0];
                if (targetObjectType instanceof Class) {
                    tmpPropertyTypes.add((Class) targetObjectType);
                }
            }
            else if (targetType instanceof TypeVariable) {
                Type[] boundTypes = ((TypeVariable) targetType).getBounds();
                for (int i = 0; i < boundTypes.length; i++) {
                    Type boundType = boundTypes[i];
                    if (boundType instanceof Class)
                        tmpPropertyTypes.add((Class) boundType);
                    else if (boundType instanceof ParameterizedType)
                        tmpPropertyTypes.add((Class)((ParameterizedType) boundType).getRawType());
                }
            }
            return tmpPropertyTypes.toArray(EMPTY_CLASSES);
        }
        else
            throw new RuntimeException("Interceptor must define generic targets and generic property types.");
    }
}
