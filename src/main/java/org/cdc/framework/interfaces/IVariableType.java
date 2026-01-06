package org.cdc.framework.interfaces;

import org.cdc.framework.builder.ProcedureBuilder;

public interface IVariableType {

	/**
	 * higherName
	 * eg. ActionResultType
	 * @return name
	 */
    String getBlocklyVariableType();

    /**
     * lowerName
	 * eg. actionresulttype
     * @return name
     */
    String getVariableType();

	default void initDefaultToolBox(ProcedureBuilder procedureBuilder,String name){
	}
}
