/*
 * JavaType
 * Create Date: 2020. 03. 20.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.metadata;

import lombok.NonNull;

import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Describes a Java type, including simple types, lists, bean types, etc.
 * Uses shared unique instances which can be retrieved with {@link TypeManager#forClass}.
 *
 * <p> Implementation note: Serialization should be done via class name only.
 *
 * @version $Revision$ $LastChangedDate$
 * @author $Author$
 */
public class JavaType<_O> implements Serializable {

    @NonNull private final Class<_O> javaType;
    @NonNull private final String simpleName;

    private static final IdentityHashMap<JavaType, JavaType> primitive2Wrapper = new IdentityHashMap<>(10);
    private static final IdentityHashMap<JavaType, JavaType> wrapper2Primitive = new IdentityHashMap<>(10);

    static {
        // primitive types and wrapper types
        Class map[] = {
                Integer.class,  int.class,
                Short.class,    short.class,
                Byte.class,     byte.class,
                Boolean.class,  boolean.class,
                Float.class,    float.class,
                Double.class,   double.class,
                Character.class, char.class,
                Long.class,     long.class
        };

        for (int i = 0, n = map.length; i < n; i += 2) {
            wrapper2Primitive.put(TypeManager.forClass(map[i]), TypeManager.forClass(map[i + 1]));
            primitive2Wrapper.put(TypeManager.forClass(map[i + 1]), TypeManager.forClass(map[i]));
        }
    }

    JavaType(Class<_O> javaType) {
        this.javaType = javaType;
        this.simpleName = javaType.getSimpleName(); // to avoid calculation
    }

    /**
     * Returns the Java class described by this instance.
     * @return the class of the given type
     */
    @NonNull
    public final Class<_O> getTypeClass() {
        return javaType;
    }

    /**
     * The qualified java class name described by this instance.
     * @return the class name of the given type
     */
    @NonNull
    public final String getTypeName() {
        return getTypeClass().getName();
    }

    /**
     * Short class name without packages.
     * @return short none null class name to be used as keys in properties
     */
    @NonNull
    public final String getSimpleName() {
        return simpleName;
    }

    /**
     * Gets a none null label which is a more user friendly name for the technical class name.
     * Falls back to simple class name.
     */
    public String getLabel() {
        return simpleName;
    }

    /**
     * Determines if the class or interface represented by this
     * {@code JavaType} object is either the same as, or is a superclass or
     * superinterface of, the class or interface represented by the specified
     * {@code JavaType} parameter.
     *
     * @see Class#isAssignableFrom(Class)
     */
    public final boolean isAssignableFrom(JavaType<?> other) {
        return javaType.isAssignableFrom(other.javaType);
    }

    /**
     * Returns if this type is a {@link Collection}.
     */
    public final boolean isCollection() {
        return Collection.class.isAssignableFrom(javaType);
    }

    /**
     * Returns if this type is a {@link List}.
     */
    public final boolean isList() {
        return List.class.isAssignableFrom(javaType);
    }

    /**
     * Returns if this type is a {@link Set}.
     */
    public final boolean isSet() {
        return Set.class.isAssignableFrom(javaType);
    }

    /**
     * Returns if this type is a {@link Map}.
     */
    public final boolean isMap() {
        return Map.class.isAssignableFrom(javaType);
    }

    /**
     * Returns the declared annotation for this type.
     */
    public final <_A extends Annotation> _A getAnnotation(Class<_A> annoClass) {
        return javaType.getAnnotation(annoClass);
    }

    /**
     * Gets if this instance represents a primitive java type.
     */
    public final boolean isPrimitive() {
        return javaType.isPrimitive();
    }

    /**
     * Returns this instance or the wrapper type if this is a primitive type.
     */
    @NonNull
    public final JavaType getWrapperType() {
        JavaType t = primitive2Wrapper.get(this);
        return t == null ? this : t;
    }

    /**
     * Returns this instance or the primitive type if this is a wrapper type.
     */
    @NonNull
    public final JavaType getPrimitiveType() {
        JavaType t = wrapper2Primitive.get(this);
        return t == null ? this : t;
    }

    static final class SerialReplacement implements Serializable {

        private static final long serialVersionUID = 1L;

        private transient String className;

        SerialReplacement(String className) {
            this.className = className;
        }

        @NonNull
        protected JavaType readResolve() throws ObjectStreamException {
            return TypeManager.forClassName(className);
        }

        private void writeObject(@NonNull ObjectOutputStream out) throws IOException {
            out.writeObject(className);
        }

        private void readObject(@NonNull ObjectInputStream in) throws IOException, ClassNotFoundException {
            className = (String) in.readObject();
        }
    }

    /**
     * Serialize only the reference and then lookup the reference again on deserialization.
     */
    @NonNull
    protected SerialReplacement writeReplace() throws ObjectStreamException {
        return new SerialReplacement(getTypeName());
    }
}
