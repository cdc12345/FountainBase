package org.cdc.framework.utils;

public enum Side {
    Server,Client;

    public static Side getSide(String side){
        for (Side side1 : Side.values()){
            if (side1.name().equalsIgnoreCase(side)){
                return side1;
            }
        }
        return Side.Client;
    }
}
