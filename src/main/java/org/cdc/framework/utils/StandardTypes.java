package org.cdc.framework.utils;

public enum StandardTypes {
    Number("number","Number"),Direction("direction","Direction"),Entity("entity","Entity"),ItemStack("itemstack","MCItem"),Boolean("logic","Boolean"),String("string","String")
    ,BlockState("blockstate","MCItemBlock"),DamageSource("damagesource","DamageSource"),ActionResultType("actionresulttype","ActionResultType");

    private final String lowerName;
    private final String higherName;
    StandardTypes(String lowerName,String higherName){
        this.lowerName = lowerName;
        this.higherName = higherName;
    }

    public String getHigherName() {
        return higherName;
    }

    public String getLowerName() {
        return lowerName;
    }
}
