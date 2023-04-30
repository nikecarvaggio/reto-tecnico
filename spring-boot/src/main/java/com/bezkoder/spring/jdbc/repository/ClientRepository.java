package com.bezkoder.spring.jdbc.repository;

import com.bezkoder.spring.jdbc.model.Client;
import com.bezkoder.spring.jdbc.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface ClientRepository {
  int save(Client name);

  int update(Client name);

  void transactionBalance(Transaction transaction);

  Client findById(Long id);

  int deleteById(Long id);

  List<Client> findAll();

  List<Client> findByActive(boolean active);

  List<Client> findByNameContaining(String title);

  int deleteAll();


}
