/*
 * InterceptorInfo
 * Create Date: 2020. 03. 22.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import gras.berry.collection.ExtendedArray;
import gras.presley.metadata.BeanType;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
abstract class InterceptorInfo<_I> {

    /** constant internally used */
    protected static final Class[] EMPTY_CLASSES = {};

    /** Holds the interceptor itself */
    @NonNull private final _I interceptor;

    /** Classes that the interceptor assigned to. */
    @NonNull private Class<?>[] targets;

    @NonNull private InterceptorPosition position;

    protected InterceptorInfo(@NonNull _I interceptor) {
        this.interceptor = interceptor;

        InterceptorDef def = getInterceptMethod().getAnnotation(InterceptorDef.class);
        position = def != null ? def.position() : InterceptorPosition.DEFAULT; // we can also use the anno attribute default value

        targets = buildTargets();
    }

    public _I getInterceptor() {
        return interceptor;
    }

    /**
     * Getter for the property targets. The interceptor is only attached to JDO's that are from the same type
     * @return none null array of class objects
     */
    @NonNull
    protected final Class<?>[] getTargets() {
        return targets;
    }

    /**
     * Checks whether the given target matches with all target classes of this interceptor.
     * @param target the class to check
     * @return a boolean which is true if the given class is assignable from all target types of this interceptor
     */
    public boolean supports(BeanType<?> target) {
        Class targetClass = target.getTypeClass();

        for (Class supportedClass: getTargets()) {
            if (!supportedClass.isAssignableFrom(targetClass))
                return false;
        }
        return true;
    }

    /**
     * Getter for the position of this interceptor
     * @return a InterceptorPosition object
     */
    public final InterceptorPosition getPosition() {
        return position;
    }

    /**
     * Return the non null intercept method from the implementer class.
     * Can be used for reading further annotations.
     */
    protected abstract Method getInterceptMethod();

    /**
     * Retrieves the type of the target Jdo on which the interceptor should be registered. The type is defined by the
     * generic definition on the interceptor.
     * @return a Type which is the type of the Jdo class
     */
    protected Type getTargetInterface() {
        for (Type inter: interceptor.getClass().getGenericInterfaces()) {
            Class clazz = null;
            if (inter instanceof Class)
                clazz = (Class) inter;
            else if (inter instanceof ParameterizedType)
                clazz = (Class) ((ParameterizedType) inter).getRawType();

            if (Interceptor.class.isAssignableFrom(clazz))
                return inter;
        }
        throw new IllegalArgumentException("Target interface not found for " + interceptor.getClass().getName() + '.');
    }

    /**
     * This method determines the type of the targets on which this interceptor works. The type is retrieved via
     * reflection from the generics definition of the interceptor class.
     */
    protected Class<?>[] buildTargets() {
        ExtendedArray<Class<?>> tmpTargets = new ExtendedArray<>();
        Type targetInterface = getTargetInterface();
        if (targetInterface instanceof ParameterizedType) {
            Type targetType = ((ParameterizedType) targetInterface).getActualTypeArguments()[0];
            if (targetType instanceof Class) {
                tmpTargets.add((Class) targetType);
            }
            else if (targetType instanceof ParameterizedType) {
                ParameterizedType paraType = (ParameterizedType) targetType;
                tmpTargets.add((Class) paraType.getRawType());
                Type targetObjectType = paraType.getActualTypeArguments()[0];
                if (targetObjectType instanceof Class) {
                    tmpTargets.add((Class) targetObjectType);
                }
                else if (targetObjectType instanceof ParameterizedType) {
                    tmpTargets.add((Class) ((ParameterizedType) targetObjectType).getRawType());
                }
                else if (targetObjectType instanceof TypeVariable) {
                    Type[] bounds = ((TypeVariable) targetObjectType).getBounds();
                    for (int i = 0; i < bounds.length; i++) {
                        Type boundType = bounds[i];
                        if (boundType instanceof Class)
                            tmpTargets.add((Class) boundType);
                        else if (boundType instanceof ParameterizedType)
                            tmpTargets.add((Class)((ParameterizedType) boundType).getRawType());
                    }
                }
            }
            else if (targetType instanceof TypeVariable) {
                Type[] boundTypes = ((TypeVariable) targetType).getBounds();
                for (int i = 0; i < boundTypes.length; i++) {
                    Type boundType = boundTypes[i];
                    if (boundType instanceof Class)
                        tmpTargets.add((Class) boundType);
                    else if (boundType instanceof ParameterizedType)
                        tmpTargets.add((Class)((ParameterizedType) boundType).getRawType());
                }
            }
            return tmpTargets.toArray(EMPTY_CLASSES);
        }
        else
            throw new RuntimeException("Interceptor must define generic targets. " + targetInterface);
    }
}
