package com.bezkoder.spring.jdbc.repository;

import com.bezkoder.spring.jdbc.model.Client;
import com.bezkoder.spring.jdbc.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class JdbcClientRepository implements ClientRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private DataSource dataSource;

  private TransactionTemplate transactionTemplate;

  @PostConstruct
  public void init() {
    transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
  }

  @Override
  public int save(Client user) {
    return jdbcTemplate.update("INSERT INTO clients (name, balance, active) VALUES(?,?,?)",
        new Object[] { user.getBalance(), user.getName(), user.isActive() });
  }

  @Override
  public int update(Client user) {
    return jdbcTemplate.update("UPDATE clients SET name=?, balance=?, active=? WHERE id=?",
        new Object[] { user.getBalance(), user.getName(), user.isActive(), user.getId() });
  }

  @Override
  public void transactionBalance(Transaction transaction) {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        try {

          // Sumar el importe al saldo del cliente2
          jdbcTemplate.update("UPDATE clients SET balance = balance + ? WHERE id = ?", transaction.getBalance(), transaction.getCliente2().getId());

          // Restar el importe al saldo del cliente1
          jdbcTemplate.update("UPDATE clients SET balance = balance - ? WHERE id = ?", transaction.getBalance(), transaction.getCliente1().getId());


        } catch (Exception e) {
          status.setRollbackOnly();
          throw e;
        }
      }
    });
  }

  @Override
  public Client findById(Long id) {
    try {
      Client user = jdbcTemplate.queryForObject("SELECT * FROM clients WHERE id=?",
          BeanPropertyRowMapper.newInstance(Client.class), id);

      return user;
    } catch (IncorrectResultSizeDataAccessException e) {
      return null;
    }
  }

  @Override
  public int deleteById(Long id) {
    return jdbcTemplate.update("DELETE FROM clients WHERE id=?", id);
  }

  @Override
  public List<Client> findAll() {
    return jdbcTemplate.query("SELECT * from clients", BeanPropertyRowMapper.newInstance(Client.class));
  }

  @Override
  public List<Client> findByActive(boolean active) {
    return jdbcTemplate.query("SELECT * from clients WHERE active=?",
        BeanPropertyRowMapper.newInstance(Client.class), active);
  }

  @Override
  public List<Client> findByNameContaining(String name) {
    String q = "SELECT * from clients WHERE name ILIKE '%" + name + "%'";

    return jdbcTemplate.query(q, BeanPropertyRowMapper.newInstance(Client.class));
  }

  @Override
  public int deleteAll() {
    return jdbcTemplate.update("DELETE from clients");
  }
}
