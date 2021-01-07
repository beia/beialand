package com.example.demo.service;

import java.util.List;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  private CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public void saveCustomer(List<Customer> customers) {
    customerRepository.saveAll(customers);
  }

  public List<Customer> getCustomersOrderByNameAsc() {
    return customerRepository.findAllByOrderByNameAsc();
  }

  public List<Customer> findAllByEmailContains(String mail) {
    return customerRepository.findAllByEmailContains(mail);
  }

  public void deleteById(String id) {
    customerRepository.deleteById(id);
  }

  public void updateCustomerById(String id, String name) {
    customerRepository.updateCustomer(id, name);
  }

  public List<Customer> getStudentCustomersOver(int age) {
    return customerRepository.getStudentCustomersOver(age);
  }
}
