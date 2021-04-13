package com.apps.mguard.detaillist;

public class ListData {
    private String useStatus;
    private int amount;
    private String date;
    private String content;
    private String Paymentmethod;


    public ListData(String useStatus, int amount, String date, String content, String paymentmethod) {
        this.useStatus = useStatus;
        this.amount = amount;
        this.date = date;
        this.content = content;
        this.Paymentmethod = paymentmethod;

    }
    public ListData(String useStatus, int amount) {
        this.useStatus = useStatus;
        this.amount = amount;

    }

    public int getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public String getPaymentmethod() {
        return Paymentmethod;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPaymentmethod(String paymentmethod) {
        Paymentmethod = paymentmethod;
    }

    public String getUseStatus() {return useStatus;}

    public void setUseStatus(String useStatus) {this.useStatus = useStatus;}
}
