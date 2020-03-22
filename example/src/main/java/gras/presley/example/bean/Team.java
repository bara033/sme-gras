/*
 * Team
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.example.bean;

import gras.presley.metadata.BeanClass;
import gras.presley.metadata.BeanProp;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
@BeanClass(label = "Team")
public class Team {

    @BeanProp
    public String name;
}
