package com.example.demo.repository;

import java.util.List;

import com.example.demo.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String>, CustomerRepositoryCustom {

  List<Customer> findAllByOrderByNameAsc();
  List<Customer> findAllByEmailContains(String mail);
  @Query("{'name': ?0}")
  List<Customer> findAllByNameEquals(String name);
}
