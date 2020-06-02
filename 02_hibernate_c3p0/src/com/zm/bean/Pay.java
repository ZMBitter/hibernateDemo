package com.zm.bean;

public class Pay {
    private int monthPay;  //月薪
    private int yearPay;  //年薪
    private int vocationWithPay;
    private Worker worker;

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public int getMonthPay() {
        return monthPay;
    }

    public void setMonthPay(int monthPay) {
        this.monthPay = monthPay;
    }

    public int getYearPay() {
        return yearPay;
    }

    public void setYearPay(int yearPay) {
        this.yearPay = yearPay;
    }

    public int getVocationWithPay() {
        return vocationWithPay;
    }

    public void setVocationWithPay(int vocationWithPay) {
        this.vocationWithPay = vocationWithPay;
    }

    @Override
    public String toString() {
        return "Pay{" +
                "monthPay=" + monthPay +
                ", yearPay=" + yearPay +
                ", vocationWithPay=" + vocationWithPay +
                '}';
    }
}
