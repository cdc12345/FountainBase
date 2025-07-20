package org.cdc.framework.interfaces;

import org.cdc.framework.builder.ProcedureBuilder;

public interface IVariableType {

	/**
	 * higherName
	 * @return name
	 */
    String getBlocklyVariableType();

    /**
     * lowerName
     * @return name
     */
    String getVariableType();

	default void initDefaultToolBox(ProcedureBuilder procedureBuilder,String name){
	}
}
