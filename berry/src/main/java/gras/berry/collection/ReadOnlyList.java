/*
 * ReadOnlyList
 * Create Date: 2020. 03. 21.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.berry.collection;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Placeholder of GRAS fw collection classes.
 *
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class ReadOnlyList<_E> extends ArrayList<_E> {

    public static final ReadOnlyList EMPTY = new ReadOnlyList<>();

    private ReadOnlyList() {
    }

    public ReadOnlyList(Collection<? extends _E> c) {
        super(c);
    }
}
