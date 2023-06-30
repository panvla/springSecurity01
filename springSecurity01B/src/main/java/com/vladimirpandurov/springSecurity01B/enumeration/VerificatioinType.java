package com.vladimirpandurov.springSecurity01B.enumeration;

public enum VerificatioinType {
    ACCOUNT("ACCOUNT"),
    PASSWORD("PASSWORD");

    private final String type;

    VerificatioinType(String type){
        this.type = type;
    }

    public String getType(){
        return this.type.toLowerCase();
    }
}
