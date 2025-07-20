package org.cdc.framework.interfaces;

public interface IProcedureCategory {
    String getName();

    default String getDefaultColor(){
        return null;
    }
}
