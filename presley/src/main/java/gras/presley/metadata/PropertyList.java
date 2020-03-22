/*
 * PropertyList
 * Create Date: 2020. 03. 21.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.metadata;

import gras.berry.collection.ReadOnlyList;
import lombok.NonNull;

import java.util.Collection;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class PropertyList<_P extends PlainBeanProp<?, ?>> extends ReadOnlyList<_P> {

    public PropertyList(Collection<? extends _P> c) {
        super(c);
    }

    /**
     * Gets the property that has the given name.
     *
     * The GRAS fw provides special map for this. Right now we iterate through the props.
     */
    public _P getByName(@NonNull String name) {
        for (int i = size(); --i >= 0; ) {
            _P property = get(i);
            if (property.getPropertyName().equals(name))
                return property;
        }
        return null;
    }
}
