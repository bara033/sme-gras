/*
 * Person
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
@BeanClass(label = "Person")
public class Person {

    @BeanProp
    public String firstName;
    @BeanProp
    public String lastName;
    @BeanProp
    public String name;
    @BeanProp
    public int weight;

    @BeanProp
    public boolean isPlayer;
    @BeanProp
    public boolean isManager;

    @BeanProp
    public Team team;
}
