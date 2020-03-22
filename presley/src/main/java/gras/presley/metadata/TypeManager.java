/*
 * TypeManager
 * Create Date: 2020. 03. 20.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.metadata;

import gras.berry.ReflectionUtils;
import gras.berry.collection.ExtendedArray;
import gras.berry.collection.ReadOnlyList;
import gras.presley.ctx.ApplicationContext;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class TypeManager {

    protected static final Logger log = Logger.getLogger(TypeManager.class.getName());

    /** Map of all types. */
    public final ConcurrentHashMap<Class, JavaType> allTypes = new ConcurrentHashMap<>(100, 0.6f, 1);
    /** Mutex on the allTypes map. */
    final Object typeMutex = new Object();
    /** Set of classes that represented as the lower level type ({@link JavaType}) in the type-system. */
    private final HashSet<Class> javaTypes = new HashSet<>();

    /**
     * Instantiated with wiring.
     */
    protected TypeManager() {
        initJavaTypes();
    }

    /**
     * Gets the singleton app-customized instance.
     */
    @NonNull
    public static TypeManager instance() {
        return ApplicationContext.getTypeManager();
    }

    @NonNull
    public static <_O> JavaType<_O> forClass(Class<_O> clazz) {
        return instance().forClassImpl(clazz);
    }

    protected <_O> JavaType<_O> forClassImpl(Class<_O> clazz) {
        JavaType<_O> type = allTypes.get(clazz);
        return type != null ? type : buildForClass(clazz);
    }

    @NonNull
    public static JavaType<?> forClassName(@NonNull String className) {
        try {
            return forClass(Class.forName(className));
        }
        catch (ClassNotFoundException e) {
            // ToDo: BA: exceptions:
            throw new IllegalArgumentException("Class not found with name " + className, e);
        }
    }

    @NonNull
    public static <_O> JavaType<_O> forBean(@NonNull _O bean) {
        return (JavaType<_O>) forClass(bean.getClass());
    }

    protected <_O> JavaType<_O> buildForClass(Class<_O> clazz) {
        JavaType<_O> type = allTypes.get(clazz);
        if (type != null) {
             // annotations are definitely needed and initialization of them is time consuming. So we do it outside the big lock
             clazz.getAnnotation(BeanClass.class); // any annotation will do

            synchronized(typeMutex) {
                type = allTypes.get(clazz);
                if (type == null) {
                    log.config("Creating JavaType " + clazz.getName());

                    type = createType(clazz);

                     if (log.isLoggable(Level.FINER))
                        log.config("Created " + type);

                    allTypes.put(clazz, type);
                }
            }
        }
        return type;
    }

    /**
     * Creates a type instance based on the given java class.
     * <p> This is a customization point. All implementation can return specialized types implementing
     * application specific type hierarchy.
     * In a simple case the package name alone can be enough to distinguish between beans and simple java objects.
     * Or specific annotations on the classes can help for differentiate the types of metadata.
     * <p> The default implementation creates {@link JavaType} based on the {@link #isJavaType(Class)} implementation,
     * creates {@link BeanType} for the rest.
     */
    protected <_O> JavaType<_O> createType(Class<_O> clazz) {
        return isJavaType(clazz) ? new JavaType<>(clazz) : new BeanType<>(clazz);
    }

    /**
     * Customizable check for {@link JavaType} building.
     * @see #createType(Class)
     */
    protected boolean isJavaType(Class<?> clazz) {
        return javaTypes.contains(clazz) ||
            clazz.isArray() ||
            clazz.isInterface() ||
            List.class.isAssignableFrom(clazz) ||
            Collection.class.isAssignableFrom(clazz) ||
            Set.class.isAssignableFrom(clazz) ||
            Map.class.isAssignableFrom(clazz);
    }

    /**
     * Initializes some basic java types.
     * @see #isJavaType(Class)
     */
    protected void initJavaTypes() {
        javaTypes.add(Integer.class);
        javaTypes.add(int.class);
        javaTypes.add(Short.class);
        javaTypes.add(short.class);
        javaTypes.add(Byte.class);
        javaTypes.add(byte.class);
        javaTypes.add(Boolean.class);
        javaTypes.add(boolean.class);
        javaTypes.add(Float.class);
        javaTypes.add(float.class);
        javaTypes.add(Double.class);
        javaTypes.add(double.class);
        javaTypes.add(Character.class);
        javaTypes.add(char.class);
        javaTypes.add(Long.class);
        javaTypes.add(long.class);

        javaTypes.add(Object.class);
        javaTypes.add(String.class);
        javaTypes.add(StringBuilder.class);
        javaTypes.add(java.util.Date.class);
        javaTypes.add(java.sql.Date.class);
        javaTypes.add(Timestamp.class);
    }

    @NonNull
    protected final <_B> PropertyList<PlainBeanProp<_B, ?>> buildBeanProperties(@NonNull BeanType<_B> beanType) {
        if (beanType.properties != null)
            return beanType.properties;

        synchronized (typeMutex) {
            beanType.properties = buildBeanPropertiesImpl(beanType);
            return beanType.properties;
        }
    }

    /**
     * Builds a read-only list of property metadata for the given class.
     * Takes only properties which are annotated with {@link BeanProp}.
     * Re-uses (includes) the base class properties.
     * @param beanType the bean type to create the properties for
     * @return none null list of properties.
     */
    @NonNull
    protected <_B> PropertyList<PlainBeanProp<_B, ?>> buildBeanPropertiesImpl(@NonNull BeanType<_B> beanType) {
        ReadOnlyList<Method> methods = ReflectionUtils.getImplementedMethods(beanType.getTypeClass());
        int size = methods.size();
        ExtendedArray<PlainBeanProp<_B, ?>> props = new ExtendedArray<>(size);
        ExtendedArray<PlainBeanProp<_B, ?>> unindexedProps = new ExtendedArray<>(size);

        // add annotated getters
        outer_loop1:
        for (int i = 0; i < size; i++) {
            Method m = methods.get(i);
            if (Modifier.isStatic(m.getModifiers()) ||
                m.isSynthetic() ||
                m.getReturnType() == Void.TYPE ||
                m.getParameterTypes().length != 0 ||
                (!m.getName().startsWith("get") && !m.getName().startsWith("is")))
                continue;

            BeanProp anno = m.getAnnotation(BeanProp.class);
            if (anno == null)
                continue;

            String propName = ReflectionUtils.getPropertyName(m.getName());
            Method setter = ReflectionUtils.getPropertySetter(beanType.getTypeClass(), propName);
            Field field = ReflectionUtils.getDeclaredField(beanType.getTypeClass(), propName);

            PlainBeanProp<_B, ?> prop = createProperty(beanType, false, field, m, setter);

            // Check if we already added this property
            for (int j = props.size(); --j >= 0; )
                if (checkDuplicate(beanType, prop, props.get(j)))
                    continue outer_loop1;

            // Same check for the unindexed properties
            for (int j = unindexedProps.size(); --j >= 0; )
                if (checkDuplicate(beanType, prop, unindexedProps.get(j)))
                    continue outer_loop1;

            int index = anno.index();
            if (index > -1) {
                ensureSize(props, index + 1);
                if (props.set(index, prop) != null)
                    throw new RuntimeException("Property " + prop.getPropertyName() + " index #" + index + " is defined twice on " + beanType.getSimpleName());
            }
            else
                unindexedProps.add(prop);
        }

        ReadOnlyList<Field> fields = ReflectionUtils.getDeclaredFields(beanType.getTypeClass());
        outer_loop2:
        for (int i = 0, n = fields.size(); i < n; i++) {
            Field m = fields.get(i);

            BeanProp anno = m.getAnnotation(BeanProp.class);
            if (anno == null)
                continue;

            String propName = m.getName();
            Method getter = ReflectionUtils.getPropertyGetter(beanType.getTypeClass(), propName);
            Method setter = ReflectionUtils.getPropertySetter(beanType.getTypeClass(), propName);

            PlainBeanProp<_B, ?> prop = createProperty(beanType, true, m, getter, setter);

            // Check if we already added this property
            for (int j = props.size(); --j >= 0; )
                if (checkDuplicate(beanType, prop, props.get(j)))
                    continue outer_loop2;

            // Same check for the unindexed properties
            for (int j = unindexedProps.size(); --j >= 0; )
                if (checkDuplicate(beanType, prop, unindexedProps.get(j)))
                    continue outer_loop2;

            int index = anno.index();
            if (index > -1) {
                ensureSize(props, index + 1);
                if (props.set(index, prop) != null)
                    throw new RuntimeException("Property " + prop.getPropertyName() + " index #" + index + " is defined twice on " + beanType.getSimpleName());
            }
            else
                unindexedProps.add(prop);
        }

        // merge together the indexed and un-indexed props
        props.addAll(unindexedProps);

        for (int i = 0, n = props.size(); i < n; i++) {
            PlainBeanProp<?, ?> prop = props.get(i);
            if (prop == null)
                throw new RuntimeException("Property #" + i + " is not defined on " + beanType.getTypeName());

            prop.setIndex(i);
        }

        return new PropertyList(props);
    }

    private static <_B> boolean checkDuplicate(@NonNull BeanType<_B> beanType, @NonNull PlainBeanProp<_B, ?> prop, @NonNull PlainBeanProp<_B, ?> added) {
        if (!added.getPropertyName().equals(prop.getPropertyName()))
            return false;

        if (added.getAnnotatedMember().getDeclaringClass() == prop.getAnnotatedMember().getDeclaringClass())
            // This is definitely not right so we show a warning and continue.
            log.warning("Annotated bean property " + beanType + '.' + added.getAnnotatedMember().getName() + " is annotated twice in " + added.getAnnotatedMember().getDeclaringClass());
        else if (added.getAnnotatedMember() instanceof Field ^ prop.getAnnotatedMember() instanceof Field) {
            // Getter and field should not both exist for the same property even when one is in a subclass show warning and continue.
            log.warning("Property " + prop + " is annotated as field and getter in " + prop.getAnnotatedMember().getDeclaringClass() + ".");
        }

        // Else we silently continue because we already added a derived version of this property,
        // meaning that the getter was overwritten in a subclass.
        // (We can be sure of this because ReflectionUtils.getImplementedMethods gives us the methods with
        // it's parameter class's methods coming first and the superclass(es)' methods after.)
        return true;
    }

    /**
     * Ensures the size of the list by adding {@code null} elements if necessary.
     */
    private static void ensureSize(@NonNull ExtendedArray<?> list, int size) {
        if (list.size() >= size)
            return;

        for (int i = list.size(); i < size; i++) {
            list.add(null);
        }
    }

    /**
     * Creates a bean property instance.
     * <p> This is a customization point. All implementation can return specialized property types implementing
     * application specific type hierarchy.
     * @param fieldAnnotated {@code false} if the getter method or {@code true} if the field annotated with {@link BeanProp}.
     */
    protected <_B> PlainBeanProp<_B, ?> createProperty(@NonNull BeanType<_B> beanType, @NonNull boolean fieldAnnotated, Field field, Method getter, Method setter) {
        return new PlainBeanProp<>(beanType, fieldAnnotated, field, getter, setter);
    }
}
