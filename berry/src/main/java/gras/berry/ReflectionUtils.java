/*
 * ReflectionUtils
 * Create Date: 2020. 03. 21.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.berry;

import gras.berry.collection.ExtendedArray;
import gras.berry.collection.ReadOnlyList;
import lombok.NonNull;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Convenience utils of reflective class access.
 *
 * <p> The GRAS fw implements several improvements here, right now they are not transferred.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class ReflectionUtils {

    private static final ConcurrentHashMap<Class, ReadOnlyList<Method>> implementedMethods = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class, ReadOnlyList<Field>> declaredFields = new ConcurrentHashMap<>();

    /**
     * static methods only.
     */
    private ReflectionUtils() {
    }

    /**
     * Returns non null read only list of all implemented non-synthetic methods of the given class including its superclasses. <br>
     * NOTE: if a method is actually invoked via reflection, then it may need to be made accessible.
     * This is not done here, because accessible methods can be less optimized, so we do not want to make all of them accessible.
     * @param clazz the non null class in which to search
     */
    @NonNull
    public static ReadOnlyList<Method> getImplementedMethods(@NonNull Class clazz) {
        ReadOnlyList<Method> methods = implementedMethods.get(clazz);
        if (methods != null)
            return methods;

        methods = getImplementedMethodsImpl(clazz);
        implementedMethods.put(clazz, methods);
        return methods;
    }

    @NonNull
    private static ReadOnlyList<Method> getImplementedMethodsImpl(@NonNull Class clazz) {
        ExtendedArray<Method> result = new ExtendedArray<>();
        while (clazz != null) {
            for (Method method: clazz.getDeclaredMethods()) {
                if (!method.isSynthetic())
                    result.add(method);
            }
            clazz = clazz.getSuperclass();
        }
        return new ReadOnlyList<>(result);
    }

    /**
     * Finds the given declared method on the class or any super classes.
     * Unlike normal methods, declared methods may be protected or private.
     * If the method is overridden multiple times in the class hierarchy the
     * closest one will be returned to the <code>clazz</code>.
     * @param clazz the non null class in which to start search.
     * @param methodName the non null method to look for.
     * @param paramTypes optional parameter type list of the desired method.
     * @return the method found or <code>null</code> if none was found.
     */
    public static Method getDeclaredMethod(@NonNull Class clazz, @NonNull String methodName, @NonNull Class... paramTypes) {
        return getDeclaredMethodImpl(clazz, methodName, paramTypes, false);
    }

    /**
     * Finds the given declared method on the class or any super classes which parameters, one by one,
     * are assignable from the given parameter types.
     * Unlike normal methods, declared methods may be protected or private.
     * If the method is overridden multiple times in the class hierarchy the
     * closest one will be returned to the <code>clazz</code>.
     * @param clazz the non null class in which to start search.
     * @param methodName the non null method to look for.
     * @param paramTypes optional parameter type list of the desired method. The method formal parameters must be
     * super class of the given parameter types
     * @return the method found or <code>null</code> if none was found.
     */
    public static Method getMatchingDeclaredMethod(@NonNull Class clazz, @NonNull String methodName, Class... paramTypes) {
        return getDeclaredMethodImpl(clazz, methodName, paramTypes, true);
    }

    private static Method getDeclaredMethodImpl(@NonNull Class clazz, @NonNull String methodName, @NonNull Class[] paramTypes, boolean checkInheritance) {
        ReadOnlyList<Method> list = getImplementedMethods(clazz);

        Method res = null;
        if (paramTypes == null || paramTypes.length == 0)
            for (int idx = 0, n = list.size(); idx < n; idx++) {
                Method m = list.get(idx);
                if (methodName.equals(m.getName()))
                    return res;
                if (m.getParameterTypes().length != 0)
                    continue;
                if (res == null || (m.getDeclaringClass() == clazz && res.getReturnType().isAssignableFrom(m.getReturnType())))
                    res = m;
            }
        else {
            int pn = paramTypes.length;
            outer:
            for (int idx = 0, n = list.size(); idx < n; idx++) {
                Method m = list.get(idx);
                if (methodName.equals(m.getName()))
                    return res;
                Class[] pt = m.getParameterTypes();
                if (pt.length != pn)
                    continue;
                for (int i = 0; i < pn; i++)
                    if (paramTypes[i] != pt[i])
                        if (!checkInheritance || !pt[i].isAssignableFrom(paramTypes[i]))
                            continue outer;

                if (res == null || (m.getDeclaringClass() == clazz && res.getReturnType().isAssignableFrom(m.getReturnType())))
                    res = m;
            }
        }

        return res;
    }

    /**
     * return non null read only list of all declared fields of the given class including its superclasses. <br>
     * NOTE: if a field is actually accessed via reflection, then it may need to be made accessible.
     * This is not done here, because accessible fields can be less optimized, so we do not want to make all of them accessible.
     * @param clazz the class in which to start search.
     */
    public static ReadOnlyList<Field> getDeclaredFields(@NonNull Class clazz) {
        ReadOnlyList<Field> fields = declaredFields.get(clazz);
        if (fields != null)
            return fields;

        fields = getDeclaredFieldsImpl(clazz);
        declaredFields.put(clazz, fields);
        return fields;
    }

    @NonNull
    private static ReadOnlyList<Field> getDeclaredFieldsImpl(@NonNull Class clazz) {
        ExtendedArray<Field> result = new ExtendedArray<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field: clazz.getDeclaredFields()) {
                result.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return new ReadOnlyList<>(result);
    }

    /**
     * Returns declared (inclusive super classes) field (any field modifier) for given class and field name or null if none found.
     */
    public static Field getDeclaredField(@NonNull Class clazz, @NonNull String fieldName) {
        ReadOnlyList<Field> fields = getDeclaredFields(clazz);
        for (int i = fields.size(); --i >= 0; ) {
            Field f = fields.get(i);
            if (fieldName.equals(f.getName()))
                return f;
        }
        return null;
    }

    /**
     * Return the getter name of a simple property name. Prefixes the property name with "get" plus
     * capitalizes the first letter of the property name.
     * @param propertyName the simple (not qualified) property name.
     * @return the getter name for the property.
     */
    public static String getterPropertyName(@NonNull String propertyName) {
        return buildMemberName("get", propertyName);
    }

    /**
     * Returns the boolean getter name of a simple property name. Prefixes the property name with
     * "is" plus capitalizes the first letter of the property name.
     * @param propertyName the simple (not qualified) property name.
     * @return the getter name for a boolean property.
     */
    public static String isGetterPropertyName(@NonNull String propertyName) {
        return buildMemberName("is", propertyName);
    }

    /**
     * Return the setter name of a simple property name. Prefixes the property name with "set" plus
     * capitalizes the first letter of the property name.
     * @param propertyName the simple (not qualified) property name.
     * @return the setter name for a property
     */
    public static String setterPropertyName(@NonNull String propertyName) {
        return buildMemberName("set", propertyName);
    }

    /**
     * Returns the property name from a given field or getter/setter name.
     * @param name a field or getter or setter name
     * @return property name
     */
    @NonNull
    public static String getPropertyName(@NonNull String name) {
        if ( name.length() > 2 ) {
            if ( name.length() > 3 ) {
                if ( name.startsWith("set") && Character.isUpperCase(name.charAt(3)) )
                    return buildMemberName(name, 3);
                else if ( name.startsWith("get") && Character.isUpperCase(name.charAt(3)) )
                    return buildMemberName(name, 3);
            }
            if ( name.startsWith("is") && Character.isUpperCase(name.charAt(2)) )
                return buildMemberName(name, 2);
        }
        return name;
    }

    /**
     * Builds a member name that follows the Java naming convention for a member (field or method).
     * Dash ('-') characters are also removed and the next following character will be capitalized.
     * This doesn't work if everything is upper cased.
     * @param name the optionally dashed name
     * @param startIdx start index within the name. Allows to skip "get", "is", "set" at the beginning
     * @return a standard member name.
     */
    @NonNull
    public static String buildMemberName(@NonNull String name, int startIdx) {
        StringBuilder builder = new StringBuilder(name.length() - startIdx);

        char c = Character.toLowerCase(name.charAt(startIdx++));
        builder.append(c);
        int p;
        while ((p = name.indexOf('-', startIdx)) > 0) {
            builder.append(name, startIdx, p);
            startIdx = p + 1;
            if (startIdx < name.length())
                builder.append(Character.toUpperCase(name.charAt(startIdx++)));
        }
        if (startIdx < name.length())
            builder.append(name, startIdx, name.length());

        return builder.toString();
    }

    /**
     * Builds a member name that follows the Java naming convention for a member (field or method).
     * Each component (except the first one) is converted to start with an upper case. Dash ('-') characters are also removed
     * and the next following character will be capitalized. This doesn't work if everything is upper cased.
     * @param prefix non null prefix
     * @param name non null name containing optional dashes
     * @return a standard member name.
     */
    @NonNull
    public static String buildMemberName(@NonNull String prefix, @NonNull String name) {
        StringBuilder builder = new StringBuilder(prefix.length() + name.length());
        builder.append(prefix);

        int startIdx = 0;
        char c = Character.toUpperCase(name.charAt(startIdx++));
        builder.append(c);
        int p;
        while ((p = name.indexOf('-', startIdx)) > 0) {
            builder.append(name, startIdx, p);
            startIdx = p + 1;
            if (startIdx < name.length())
                builder.append(Character.toUpperCase(name.charAt(startIdx++)));
        }
        if (startIdx < name.length())
            builder.append(name, startIdx, name.length());

        return builder.toString();
    }

    /**
     * Returns the getter {@code Method} for the given property name.
     * <ul>
     * <li>First tries "get"&lt;propertyName&gt;()
     * <li>second tries "is"&lt;propertyName&gt;().
     * <ul>
     * @param clazz the class in which to search.
     * @param propertyName property for which the getter must be found.
     * @return the method or {@code null} if nothing found.
     */
    public static Method getPropertyGetter(@NonNull Class clazz, @NonNull String propertyName) {
        // first try get<propertyName>()
        Method getter = getDeclaredMethod(clazz, getterPropertyName(propertyName), (Class[]) null);
        if (getter == null) {
            // next try is<propertyName>()
            getter = getDeclaredMethod(clazz, isGetterPropertyName(propertyName), (Class[]) null);
            if (getter == null) {
                // next try direct name method like "wasInitialized"
                getter = getDeclaredMethod(clazz, propertyName, (Class[]) null);
                if ( getter == null )
                    return null;
            }
        }
        getter.setAccessible(true);
        return getter;
    }

    /**
     * Searches for a setter <code>Method</code> for for the given property name.
     * The search performs for a <code>"set"<propertyName>(TYPE)</code> method where TYPE is
     * identical with the return type of the corresponding property getter. Therefor the search is
     * unsuccessful if no corresponding getter was found.
     * @param clazz the class in which to search.
     * @param propertyName property for which the setter must be found.
     * @return the method or <code>null</code> if not found.
     */
    public static Method getPropertySetter(@NonNull Class clazz, @NonNull String propertyName) {
        // check the getter for the parameter type
        Method getter = getPropertyGetter(clazz, propertyName);
        if (getter == null)
            return null;

        // OK this is the first time
        // try get<propertyName>()
        Method setter = getDeclaredMethod(clazz, setterPropertyName(propertyName), getter.getReturnType());
        if (setter == null)
            return null;

        setter.setAccessible(true);
        return setter;
    }

    /**
     * adds the raw class type(s) for the given (maybe) generic type into the given result list.
     * It converts any given type (incl. a simple class) which may be a generic type variable or a parameterized type
     * into one or more classes.
     * <br>
     * Raw types: To facilitate interfacing with non-generic legacy code, it is also possible to
     * use as a type the erasure of a parameterized type. Such a type is called a raw type.
     * @param type the type which must be "ungenerify".
     * @return true if any class was added
     */
    public static boolean addRawTypes(Type type, @NonNull Set<Class> result ) {
        boolean modified = false;

        if (type instanceof TypeVariable) {
            // Example: <_Jdo extends Jdo & Described> void exec(_Jdo param)
            Type[] bounds = ((TypeVariable) type).getBounds();
            for (Type aType : bounds)
                modified |= addRawTypes(aType,result);
            return modified;
        }
        else if (type instanceof ParameterizedType) {
            return addRawTypes(((ParameterizedType) type).getRawType(),result);
        }
        else if (type instanceof WildcardType) {
            // Example <? extends Jdo>
            Type[] bounds = ((WildcardType) type).getLowerBounds();
            for (Type aType : bounds)
                modified |= addRawTypes(aType,result);
            bounds = ((WildcardType) type).getUpperBounds();
            for (Type aType : bounds)
                modified |= addRawTypes(aType,result);
            return modified;
        }
        else if (type instanceof Class) {
            if (type == Object.class)
                return false;

            return result.add((Class) type);
        }

        // OK we lost
        return modified;
    }

    /**
     * Returns the raw class type(s) for the given (maybe) generic type.
     * It converts any given type (incl. a simple class) which may be a generic type variable or a parameterized type
     * into one or more classes.
     * <br>
     * Raw types: To facilitate interfacing with non-generic legacy code, it is also possible to
     * use as a type the erasure of a parameterized type. Such a type is called a raw type.
     * @param type the type which must be "ungenerify".
     * @return the set of raw types not being <code>Object.class</code> or empty set.
     */
    @NonNull
    public static HashSet<Class> getRawTypes(Type type) {
        HashSet<Class> set = new HashSet<>(10);
        addRawTypes(type,set);
        return set;
    }

    /**
     * adds all reflective element types of a given generic collection or map type. For map
     * instances the value types are returned.
     * Collects the list of classes and interfaces that each element in the collection must implement/extend.
     * If the type is really a collection type then at least Object.class is added to the result.
     * <br> NOTE: Only field types, method return types and method parameters can be reflective types.
     * @param collectionType a type that was returned by Method/Field getGenericXxxxType().
     *          If it is a Map instance the maps value type is taken (not the key type).
     * @return true if any class was added to the result
     */
    public static boolean addCollectionElementTypes(Type collectionType, @NonNull Set<Class> result ) {

        if ( collectionType instanceof TypeVariable) {
            // collecting type within all bounds
            boolean modified = false;
            for ( Type aType : ((TypeVariable) collectionType).getBounds() )
                modified |= addCollectionElementTypes(aType, result);
            return modified;
        }
        else if ( collectionType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) collectionType;
            Type rawType = pType.getRawType();

            if ( !(rawType instanceof Class) )
                return false;

            if ( !Collection.class.isAssignableFrom((Class) rawType) && !Map.class.isAssignableFrom((Class) rawType) )
                return false;

            // the parameter index that defines the element type
            int paramIdx = Map.class.isAssignableFrom((Class) rawType) ? 1 : 0;
            Type[] params = pType.getActualTypeArguments();
            if ( paramIdx >= params.length )
                // Custom class which is not following parameter order
                return addCollectionElementTypes(rawType, result);

            return addRawTypes(params[paramIdx], result);
        }
        else if ( collectionType instanceof Class ) {
            // if the class is not a collection nor a map then we are done
            if ( !Collection.class.isAssignableFrom((Class) collectionType) && !Map.class.isAssignableFrom((Class) collectionType) )
                return false;

            // not sure if this needs to be checked. May be a redundant check which is done already in recurse call.
            TypeVariable[] typeParams = ((Class) collectionType).getTypeParameters();
            int paramIdx = Map.class.isAssignableFrom((Class) collectionType) ? 1 : 0;
            if ( paramIdx < typeParams.length )
                return addRawTypes(typeParams[paramIdx], result);

            // we may have a custom non parameterized collection or map, so we need to check the generic super type(s)
            Class superCls = ((Class) collectionType).getSuperclass();
            if ( !Collection.class.isAssignableFrom(superCls) && !Map.class.isAssignableFrom(superCls) ) {
                // Since our class implements Collection or Map (see above check) and the super class does not,
                // then the class is directly implementing it
                Type[] ifaces = ((Class) collectionType).getGenericInterfaces();
                for ( Type ifa : ifaces )
                    if ( ifa instanceof Class ) {
                        if ( Collection.class.isAssignableFrom((Class) ifa) || Map.class.isAssignableFrom((Class) ifa) )
                            return addCollectionElementTypes(ifa, result);
                    }
                    else if ( ifa instanceof ParameterizedType && ((ParameterizedType) ifa).getRawType() instanceof Class ) {
                        Class ifaCls = (Class) ((ParameterizedType) ifa).getRawType();
                        if ( Collection.class.isAssignableFrom(ifaCls) || Map.class.isAssignableFrom(ifaCls) )
                            return addCollectionElementTypes(ifa, result);
                    }
                throw new UnsupportedOperationException("Could not find Map or Collection on " + collectionType);
            }

            Type superType = ((Class) collectionType).getGenericSuperclass();
            if ( superType == Object.class )
                throw new UnsupportedOperationException("Could not find Map or Collection on " + collectionType);
            return addCollectionElementTypes(superType, result);
        }
        else
            // Neither a map nor a collection
            return false;
    }

    /**
     * return the none interface type of the value in the given collection type.
     * For Map the value type is used. Abstract types are also allowed.
     */
    public static Class getCollectionElementType(Type collectionType) {
        HashSet<Class> result = new HashSet<Class>(10);
        addCollectionElementTypes(collectionType, result);
        for( Class cls : result ) {
            if ( !cls.isInterface()
                    && !cls.isAnonymousClass() )
                return cls;
        }

        // ToDo: not allowed until we have BeanType for interface and abstract types
/*
        for( Class cls : result ) {
            if ( !cls.isInterface()
                    && !cls.isAnonymousClass() )
                return cls;
        }
        for( Class cls : result ) {
            if ( !cls.isAnonymousClass() )
                return cls;
        }
*/
        return null;
    }
}
