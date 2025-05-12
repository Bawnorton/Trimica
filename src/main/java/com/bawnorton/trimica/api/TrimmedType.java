package com.bawnorton.trimica.api;

@SuppressWarnings("unused")
public enum TrimmedType {
    ITEM,
    ARMOUR,
    SHIELD;

    public boolean isItem() {
        return this == ITEM;
    }

    public boolean isArmour() {
        return this == ARMOUR;
    }

    public boolean isShield() {
        return this == SHIELD;
    }
}
