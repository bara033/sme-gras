/* 
 * JdoPropertyPattern 
 * Date: 2007.04.06. 
 * Copyright: Sony BMG 
 * Created by: BarazA 
 */
package gras.presley.metadata;


import gras.berry.Strings;
import lombok.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regexp wrapper following specification of text property definitions
 *
 * @author $Author$
 * @version $Revision$ $LastChangedDate$
 */
public final class PropertyPattern {

    public static final PropertyPattern MATCH_ALL = new PropertyPattern("*");

    @NonNull
    private final String name;
    private final boolean qualified;
    private final boolean hasWildchard;
    @NonNull
    private final Pattern pattern;

    /**
     * Creates a {@link PropertyPattern} instance.<br>
     * The name may contain '*' and '?' wild cards, and may or may not contain the beans qualified class name part.
     * Whether to check the plain property name or the qualified class name plus the property name is indicated by having
     * at least one dot in the pattern.<br>
     * The names are always compared case sensitive.<br>
     * Examples:
     * <pre>
     * <code>*mod*</code>         - any property containing <i>mod</i>
     * <code>*Track*.*mod*</code> - any property containing <i>mod</i>
     *                              and belongs to a class with:
     *                              <i>Track</i> in the name
     * </pre>
     */
    private PropertyPattern(@NonNull String name) {
        this.name = name;
        qualified = name.indexOf('.') != -1;
        hasWildchard = name.indexOf('*') != -1;
        if (hasWildchard) {
            String p = Strings.replace(name, ".", "\\.", "*", ".*");
            pattern = Pattern.compile(p);
        }
        else
            pattern = null;
    }

    /**
     * creates a {@link PropertyPattern} instance.<br>
     * The name may contain '*' and '?' wild cards, and may or may not contain the beans qualified class name part.
     * Whether to check the plain property name or the qualified class name plus the property name is indicated by having
     * at least one dot in the pattern.<br>
     * The names are always compared case sensitive.<br>
     * Examples:
     * <pre>
     * <code>*mod*</code>         - any property containing <i>mod</i>
     * <code>*Track*.*mod*</code> - any property containing <i>mod</i>
     *                              and belongs to a JDO class with:
     *                              <i>Track</i> in the name
     * </pre>
     */
    @NonNull
    public static PropertyPattern compile(@NonNull String pattern) {
        if ( "*".equals(pattern) )
            return MATCH_ALL;
        return new PropertyPattern(pattern);
    }

    /**
     * Returns true if this pattern is mathes to the given property.
     */
    public final boolean matches(@NonNull PlainBeanProp prop) {
        String p = qualified ? prop.getSimplePropertyName() : prop.getPropertyName();
        if (!hasWildchard)
            return name.equals(p);
        else {
            if ( this == MATCH_ALL )
                return true;
            Matcher matcher = pattern.matcher(p);
            return matcher.matches();
        }
    }

    @NonNull
    public String toString() {
        return name;
    }
}
