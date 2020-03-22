/*
 * OpeartionManager
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.operation;

import gras.berry.collection.ExtendedArray;
import gras.berry.collection.ReadOnlyList;
import gras.presley.metadata.BeanType;
import gras.presley.metadata.PlainBeanProp;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class OperationManager {

    protected OperationManager() {
    }

    public OperationManager instance() {
        return null; // gets the configured instance
    }

    /**
     * Returns all registered bean operations for the given bean type.
     * @return the collection of {@link TypeOperation} instances or an empty list if there are none.
     */
    public <_B> List<TypeOperation<_B>> getTypeOperations(BeanType<_B> beanMetadata) {
        ReadOnlyList<TypeOperationInfo<_B>> operations = getTypeOperationsImpl(beanMetadata);

        ArrayList<TypeOperation<_B>> result = new ArrayList<>(operations.size());
        for (int i = operations.size(); --i >= 0; ) {
            TypeOperationInfo<_B> operation = operations.get(i);
            if (!operation.supports(beanMetadata))
                continue;

            TypeOperation<_B> opInstance = operation.createInstance();
            result.add(opInstance);
        }

        return result;
    }

    protected <_B> ReadOnlyList<TypeOperationInfo<_B>> getTypeOperationsImpl(BeanType<_B> beanMetadata) {
        ReadOnlyList<TypeOperationInfo<_B>> operations = MetadataAccess.getTypeOperations(beanMetadata);
        if (operations == null) {
            List<TypeOperationInfo<_B>> all = OperationRegistry.instance().getTypeOperations();
            ExtendedArray<TypeOperationInfo<_B>> supportedOperations = new ExtendedArray<>(all.size());

            for (int i = all.size(); --i >= 0; ) {
                TypeOperationInfo<_B> operation = all.get(i);
                if (operation.supports(beanMetadata))
                    supportedOperations.add(operation);
            }
            operations = new ReadOnlyList<>(supportedOperations);
            MetadataAccess.setTypeOperations(beanMetadata, operations);
        }
        return operations;
    }

    /**
     * Returns all registered bean operations for the given bean. If the bean is wrapped, then unwraps it recursively and finds all
     * registered operations for the wrapped beans. Operations with the same name are listed only once.
     * @return the collection of {@link BeanOperation} instances or an empty list if there are none.
     */
    public <_B> List<BeanOperation<_B>> getBeanOperations(_B bean, BeanType<_B> beanMetadata) {
        List<BeanOperationInfo<_B>> operations = getBeanOperationsImpl(beanMetadata);

        ArrayList<BeanOperation<_B>> result = new ArrayList<>(operations.size());
        for (int i = operations.size(); --i >= 0; ) {
            BeanOperationInfo<_B> operation = operations.get(i);
            if (!operation.supports(bean))
                continue;

            BeanOperation<_B> opInstance = operation.createInstance(bean);
            result.add(opInstance);
        }

        return result;
    }

    protected <_B> ReadOnlyList<BeanOperationInfo<_B>> getBeanOperationsImpl(BeanType<_B> beanMetadata) {
        ReadOnlyList<BeanOperationInfo<_B>> operations = MetadataAccess.getBeanOperations(beanMetadata);
        if (operations == null) {
            List<BeanOperationInfo<_B>> all = OperationRegistry.instance().getBeanOperations();
            ExtendedArray<BeanOperationInfo<_B>> supportedOperations = new ExtendedArray<>(all.size());

            for (int i = all.size(); --i >= 0; ) {
                BeanOperationInfo<_B> operation = all.get(i);
                if (operation.supports(beanMetadata))
                    supportedOperations.add(operation);
            }
            operations = new ReadOnlyList<>(supportedOperations);
            MetadataAccess.setBeanOperations(beanMetadata, operations);
        }
        return operations;
    }

    /**
     * Returns all registered property operations for the given bean and property.<br>
     * @return the collection of {@link PropertyOperation} instances or an empty list if there are none.
     */
    @NonNull
    public <_B, _P> List<PropertyOperation<_B, _P>> getPropertyOperations(_B bean, PlainBeanProp<_B, _P> propertyMetadata) {
        ReadOnlyList<PropertyOperationInfo<_B, _P>> operations = getPropertyOperationsImpl(propertyMetadata);

        ArrayList<PropertyOperation<_B, _P>> result = new ArrayList<>(operations.size());
        for (int i = operations.size(); --i >= 0; ) {
            PropertyOperationInfo<_B, _P> operation = operations.get(i);
            if (!operation.supports(bean))
                continue;

            PropertyOperation<_B, _P> opInstance = operation.createInstance(bean);
            result.add(opInstance);
        }

        return result;
    }

    protected <_B, _P> ReadOnlyList<PropertyOperationInfo<_B, _P>> getPropertyOperationsImpl(PlainBeanProp<_B, _P> propertyMetadata) {
        ReadOnlyList<PropertyOperationInfo<_B, _P>> operations = MetadataAccess.getOperations(propertyMetadata);
        if (operations == null) {
            List<PropertyOperationInfo<_B, _P>> all = OperationRegistry.instance().getPropertyOperations();
            ExtendedArray<PropertyOperationInfo<_B, _P>> supportedOperations = new ExtendedArray<>(all.size());

            for (int i = all.size(); --i >= 0; ) {
                PropertyOperationInfo<_B, _P> operation = all.get(i);
                if (operation.supports(propertyMetadata))
                    operations.add(operation);
            }
            operations = new ReadOnlyList<>(supportedOperations);
            MetadataAccess.setOperations(propertyMetadata, operations);
        }
        return operations;
    }

    private static final class MetadataAccess extends gras.presley.metadata.PackageAccess {

        protected static <_B> ReadOnlyList<TypeOperationInfo<_B>> getTypeOperations(BeanType<_B> beanType) {
            return gras.presley.metadata.PackageAccess.getTypeOperations(beanType);
        }

        protected static <_B> void setTypeOperations(BeanType<_B> beanType, ReadOnlyList<TypeOperationInfo<_B>> typeOperations) {
            gras.presley.metadata.PackageAccess.setTypeOperations(beanType, typeOperations);
        }

        protected static <_B> ReadOnlyList<BeanOperationInfo<_B>> getBeanOperations(BeanType<_B> beanType) {
            return gras.presley.metadata.PackageAccess.getBeanOperations(beanType);
        }

        protected static <_B> void setBeanOperations(BeanType<_B> beanType, ReadOnlyList<BeanOperationInfo<_B>> beanOperations) {
            gras.presley.metadata.PackageAccess.setBeanOperations(beanType, beanOperations);
        }

        protected static <_B, _P> ReadOnlyList<PropertyOperationInfo<_B, _P>> getOperations(PlainBeanProp<_B, _P> prop) {
            return gras.presley.metadata.PackageAccess.getOperations(prop);
        }

        protected static <_B, _P> void setOperations(PlainBeanProp<_B, _P> prop, ReadOnlyList<PropertyOperationInfo<_B, _P>> operations) {
            gras.presley.metadata.PackageAccess.setOperations(prop, operations);
        }
    }
}
