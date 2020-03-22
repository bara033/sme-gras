/*
 * PropertyOperationInfo
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.operation;

import gras.presley.metadata.PlainBeanProp;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PropertyOperationInfo<_B, _P> {

    final Class<PropertyOperation<_B, _P>> operation;
    final PlainBeanProp<_B, _P> propertyMetadata;

    PropertyOperationInfo(Class<PropertyOperation<_B, _P>> operation) {
        this.operation = operation;
        this.propertyMetadata = null; // here comes the reflective inspection of the given operation instance
    }

    public boolean supports(PlainBeanProp<?, ?> propertyMetadata) {
        return true;
    }

    public boolean supports(_B bean) {
        return true;
    }

    PropertyOperation<_B, _P> createInstance(_B bean) {
        return null; // reflective createNew
    }
}
