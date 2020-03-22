/*
 * ExtendedArray
 * Create Date: 2020. 03. 21.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.berry.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Placeholder of GRAS fw collection classes.
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class ExtendedArray<_E> extends ArrayList<_E> {

    public ExtendedArray(int initialCapacity) {
        super(initialCapacity);
    }

    public ExtendedArray() {
    }

    public ExtendedArray(Collection<? extends _E> c) {
        super(c);
    }

    public ExtendedArray(Object... items) {
        super(items != null ? items.length : 10);
        if (items != null)
            addAll((List) Arrays.asList(items));
    }
}
