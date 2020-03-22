/*
 * InterceptorManager
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.interceptor;

import gras.berry.collection.ExtendedArray;
import gras.berry.collection.ReadOnlyList;
import gras.presley.ctx.ApplicationContext;
import gras.presley.metadata.BeanType;
import gras.presley.metadata.PlainBeanProp;
import lombok.NonNull;

import java.util.List;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class InterceptorManager {

    protected InterceptorManager() {
    }

    public static InterceptorManager instance() {
        return ApplicationContext.getInterceptorManager();
    }

    protected <_B> void processBean(@NonNull BeanType<_B> beanMetadata, @NonNull _B bean, @NonNull BeanOperation operation) throws Exception {
        ReadOnlyList<BeanInterceptorInfo<_B>> interceptors = getInterceptors(beanMetadata);
        BeanInvocation<_B> invocation = new BeanInvocation<_B>(bean, beanMetadata, operation, interceptors);
        invocation.proceed();
    }

    /**
     * Gets all the registered interceptors for the given bean type.
     *
     * <p> Retrieves all the interceptors from {@link InterceptorRegistry} and checks all of them if they support the type.
     * Stores the calculated interceptor list on the {@link BeanType} instance for the next call.
     *
     * @see BeanInterceptorInfo#supports(BeanType)
     * @see InterceptorRegistry#getBeanInterceptors()
     */
    @NonNull
    protected <_B> ReadOnlyList<BeanInterceptorInfo<_B>> getInterceptors(@NonNull BeanType<_B> beanMetadata) {
        ReadOnlyList<BeanInterceptorInfo<_B>> interceptors = MetadataAccess.getInterceptors(beanMetadata);
        if (interceptors == null) {
            List<BeanInterceptorInfo<_B>> all = InterceptorRegistry.instance().getBeanInterceptors();

            ExtendedArray<BeanInterceptorInfo<_B>> supportedInterceptors = new ExtendedArray<>(all.size());
            for (int i = all.size(); --i >= 0; ) {
                BeanInterceptorInfo<_B> interceptor = all.get(i);
                if (interceptor.supports(beanMetadata))
                    supportedInterceptors.add(interceptor);
            }

            interceptors = new ReadOnlyList<>(supportedInterceptors);
            MetadataAccess.setInterceptors(beanMetadata, interceptors);
        }
        return interceptors;
    }

    protected <_B, _P> void processProperty(@NonNull PlainBeanProp<_B, _P> propertyMetadata, @NonNull _B bean, _P originalValue, _P newValue) throws Exception {
        ReadOnlyList<PropertyInterceptorInfo<_B, _P>> interceptors = getInterceptors(propertyMetadata);
        PropertyInvocation<_B, _P> invocation = new PropertyInvocation<_B, _P>(bean, originalValue, newValue, propertyMetadata, interceptors);
        invocation.proceed();
    }

    /**
     * Gets all the registered property interceptors for the given prop.
     *
     * <p> Retrieves all the interceptors from {@link InterceptorRegistry} and checks all of them if they support the property.
     * Stores the calculated interceptor list on the {@link PlainBeanProp} instance for the next call.
     *
     * @see PropertyInterceptorInfo#supports(PlainBeanProp)
     * @see InterceptorRegistry#getPropertyInterceptors()
     */
    @NonNull
    protected <_B, _P> ReadOnlyList<PropertyInterceptorInfo<_B, _P>> getInterceptors(@NonNull PlainBeanProp<_B, _P> propertyMetadata) {
        ReadOnlyList<PropertyInterceptorInfo<_B, _P>> interceptors = MetadataAccess.getInterceptors(propertyMetadata);
        if (interceptors == null) {
            List<PropertyInterceptorInfo<_B, _P>> all = InterceptorRegistry.instance().getPropertyInterceptors();

            ExtendedArray<PropertyInterceptorInfo<_B, _P>> supportedInterceptors = new ExtendedArray<>(all.size());
            for (int i = all.size(); --i >= 0; ) {
                PropertyInterceptorInfo<_B, _P> interceptor = all.get(i);
                if (interceptor.supports(propertyMetadata))
                    supportedInterceptors.add(interceptor);
            }

            interceptors = new ReadOnlyList<>(supportedInterceptors);
            MetadataAccess.setInterceptors(propertyMetadata, new ReadOnlyList<>(interceptors));
        }
        return interceptors;
    }

    private static final class MetadataAccess extends gras.presley.metadata.PackageAccess {

        protected static <_B> ReadOnlyList<BeanInterceptorInfo<_B>> getInterceptors(BeanType<_B> beanType) {
            return gras.presley.metadata.PackageAccess.getInterceptors(beanType);
        }

        protected static <_B> void setInterceptors(BeanType<_B> beanType, ReadOnlyList<BeanInterceptorInfo<_B>> interceptors) {
            gras.presley.metadata.PackageAccess.setInterceptors(beanType, interceptors);
        }

        protected static <_B, _P> ReadOnlyList<PropertyInterceptorInfo<_B, _P>> getInterceptors(PlainBeanProp<_B, _P> prop) {
            return gras.presley.metadata.PackageAccess.getInterceptors(prop);
        }

        protected static <_B, _P> void setInterceptors(PlainBeanProp<_B, _P> prop, ReadOnlyList<PropertyInterceptorInfo<_B, _P>> interceptors) {
            gras.presley.metadata.PackageAccess.setInterceptors(prop, interceptors);
        }
    }
}
