package com.bezkoder.spring.jdbc.model;

import java.math.BigDecimal;

public class Client {

  private long id;
  private long balance;
  private String name;

  private Client cliente1;
  private Client cliente2;

  private boolean active;

  public Client() {

  }

  public Client(long id, long balance, String name, boolean active) {
    this.id = id;
    this.balance = balance;
    this.name = name;
    this.active = active;
  }



  public Client(long balance, String name, boolean active) {
    this.balance = balance;
    this.name = name;
    this.active = active;
  }

  public void setId(long id) {
    this.id = id;
  }
  
  public long getId() {
    return id;
  }

  public long getBalance() {
    return balance;
  }

  public void setBalance(long balance) {
    this.balance = balance;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean isActive) {
    this.active = isActive;
  }

  @Override
  public String toString() {
    return "usuario [id=" + id + ", balance=" + balance + ", name=" + name + ", active=" + active + "]";
  }

}
