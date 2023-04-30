package com.bezkoder.spring.jdbc.repository;

import java.util.List;

import com.bezkoder.spring.jdbc.model.User;

public interface UserRepository {
  int save(User name);

  int update(User name);

  User findById(Long id);

  int deleteById(Long id);

  List<User> findAll();

  List<User> findByActive(boolean active);

  List<User> findByNameContaining(String title);

  int deleteAll();
}
