/*
 * PropertyOperation
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.operation;

import gras.presley.metadata.PlainBeanProp;

/**
 * Base class for operations that work on bean properties.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public interface PropertyOperation<_B, _P> {

    Object process(_B bean, PlainBeanProp<_B, _P> propertyMetadata);
}
