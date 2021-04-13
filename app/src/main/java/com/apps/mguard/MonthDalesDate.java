package com.apps.mguard;

public class MonthDalesDate {
    String useStatus;
    int amount;

    public MonthDalesDate(String useStatus, int amount) {
        this.useStatus = useStatus;
        this.amount = amount;
    }

    public String getUseStatus() {
        return useStatus;
    }

    public void setUseStatus(String useStatus) {
        this.useStatus = useStatus;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
