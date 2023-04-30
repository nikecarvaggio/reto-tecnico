package com.bezkoder.spring.jdbc.model;

public class Transaction {

    private long balance;
    private Client cliente1;
    private Client cliente2;

    public Transaction(long balance, Client client1, Client client2){
        this.balance=balance;
        this.cliente1=client1;
        this.cliente2=client2;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public Client getCliente1() {
        return cliente1;
    }

    public void setCliente1(Client cliente1) {
        this.cliente1 = cliente1;
    }

    public Client getCliente2() {
        return cliente2;
    }

    public void setCliente2(Client cliente2) {
        this.cliente2 = cliente2;
    }
}
