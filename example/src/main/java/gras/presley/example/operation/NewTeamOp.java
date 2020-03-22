/*
 * NewTeamOp
 * Create Date: 2020. 03. 10.
 * Copyright: SONY MUSIC ENTERTAINMENT
 * Initial-Author: barazakos
 */
package gras.presley.example.operation;


import gras.presley.example.bean.Team;
import gras.presley.operation.TypeOperation;

/**
 * @version $Revision$ $LastChangedDate$ 
 * @author $Author$
 */
public class NewTeamOp implements TypeOperation {

    @Override
    public Object process() {
        Team team = new Team();
        team.name = "some name"; // this can come from some context that contains the client request, but it is not in this mockup
        return team;
    }
}
