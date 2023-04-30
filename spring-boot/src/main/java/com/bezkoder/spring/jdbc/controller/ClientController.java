package com.bezkoder.spring.jdbc.controller;

import com.bezkoder.spring.jdbc.model.Client;
import com.bezkoder.spring.jdbc.model.Transaction;
import com.bezkoder.spring.jdbc.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class ClientController {

  @Autowired
  ClientRepository clientRepository;

  @GetMapping("/clients")
  public ResponseEntity<List<Client>> getAllclients(@RequestParam(required = false) String title) {
    try {
      List<Client> clients = new ArrayList<Client>();

      if (title == null)
        clientRepository.findAll().forEach(clients::add);
      else
        clientRepository.findByNameContaining(title).forEach(clients::add);

      if (clients.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(clients, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/clients/{id}")
  public ResponseEntity<Client> getClientById(@PathVariable("id") long id) {
    Client client = clientRepository.findById(id);

    if (client != null) {
      return new ResponseEntity<>(client, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/clients/transactionBalance")
  public ResponseEntity<String> transactionBalance(@RequestBody Transaction transaction) {
    System.out.println("esta aqui");
    try {
      clientRepository.transactionBalance(new Transaction(transaction.getBalance(),
              new Client(transaction.getCliente1().getId(), transaction.getCliente1().getBalance(), transaction.getCliente1().getName(), false),
              new Client(transaction.getCliente2().getId(), transaction.getCliente2().getBalance(), transaction.getCliente2().getName(), false)));
      return new ResponseEntity<>("Balance applied successfully.", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>("Balance cant less zero", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }


  @PostMapping("/clients")
  public ResponseEntity<String> createClient(@RequestBody Client client) {
    try {
      clientRepository.save(new Client(client.getBalance(), client.getName(), false));
      return new ResponseEntity<>("Client was created successfully.", HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/clients/{id}")
  public ResponseEntity<String> updateClient(@PathVariable("id") long id, @RequestBody Client client) {
    Client _client = clientRepository.findById(id);

    if (_client != null) {
      _client.setId(id);
      _client.setBalance(client.getBalance());
      _client.setName(client.getName());
      _client.setActive(client.isActive());

      clientRepository.update(_client);
      return new ResponseEntity<>("Client was updated successfully.", HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Cannot find Client with id=" + id, HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/clients/{id}")
  public ResponseEntity<String> deleteClient(@PathVariable("id") long id) {
    try {
      int result = clientRepository.deleteById(id);
      if (result == 0) {
        return new ResponseEntity<>("Cannot find Client with id=" + id, HttpStatus.OK);
      }
      return new ResponseEntity<>("Client was deleted successfully.", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Cannot delete client.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/clients")
  public ResponseEntity<String> deleteAllclients() {
    try {
      int numRows = clientRepository.deleteAll();
      return new ResponseEntity<>("Deleted " + numRows + " Client(s) successfully.", HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Cannot delete clients.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @GetMapping("/clients/published")
  public ResponseEntity<List<Client>> findByPublished() {
    try {
      List<Client> clients = clientRepository.findByActive(true);

      if (clients.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(clients, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
