/*
 * Strings
 * Create Date: 2020. 03. 22.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.berry;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class Strings {

    /** static methods only */
    private Strings() {
    }

    /**
     * Replaces simple text matches.
     * Since the java.lang.String implementation uses regular expressions, we supply here a simple faster version.
     *
     * @param src the original string
     * @param matchReplacements a list of (match,replacement) tupples
     * @return the modified string
     */
    public static String replace(String src, String... matchReplacements) {
        if ( src == null || src.length() == 0 || matchReplacements == null )
            return src;
        StringBuilder b = null;
        for( int i = 0, n = matchReplacements.length; i < n; i++ ) {
            String match = matchReplacements[i++];
            if ( match == null || match.length() == 0 )
                continue;
            int idx = b == null ? src.indexOf(match) : b.indexOf(match);
            if ( idx < 0 )
                continue;
            if ( b == null )
                b = new StringBuilder(src.length()+300).append(src);
            String replacement = matchReplacements[i];
            if ( replacement == null )
                replacement = "null";
            do {
                b.delete(idx, idx + match.length());
                b.insert(idx, replacement);
                idx += replacement.length();
            } while ( (idx = b.indexOf(match,idx)) > 0 );

        }
        return b != null ? b.toString() : src;
    }
}
