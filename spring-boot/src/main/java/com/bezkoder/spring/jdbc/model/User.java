package com.bezkoder.spring.jdbc.model;

public class User {

  private long id;
  private String lastname;
  private String name;
  private boolean active;

  public User() {

  }
  
  public User(long id, String lastname, String name, boolean active) {
    this.id = id;
    this.lastname = lastname;
    this.name = name;
    this.active = active;
  }

  public User(String lastname, String name, boolean active) {
    this.lastname = lastname;
    this.name = name;
    this.active = active;
  }
  
  public void setId(long id) {
    this.id = id;
  }
  
  public long getId() {
    return id;
  }

  public String getBalance() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
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
    return "usuario [id=" + id + ", lastname=" + lastname + ", name=" + name + ", active=" + active + "]";
  }

}
