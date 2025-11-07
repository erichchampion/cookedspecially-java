package com.cookedspecially.enums.check;

public enum CustomPaymentType {
    ADD_CASH("CASH"), WITHDRAW_CASH("CASH"), TRANSACTION_CASH("CASH");

    private final String value;

    private CustomPaymentType(String s) {
        value = s;
    }

    public String value() {
        return this.value;
    }

    public String toString() {
        return this.name();
    }

}
