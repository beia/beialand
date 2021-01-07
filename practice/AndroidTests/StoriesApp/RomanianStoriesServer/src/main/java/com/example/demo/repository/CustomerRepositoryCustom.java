package com.example.demo.repository;

import com.example.demo.model.Customer;

import java.util.List;

public interface CustomerRepositoryCustom {
  void updateCustomer(String id, String name);
  List<Customer> getStudentCustomersOver(int age);
}
