package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

  private CustomerService customerService;

  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/save")
  public void saveCustomer(@RequestBody List<Customer> customers) {
    customerService.saveCustomer(customers);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/get/order/asc")
  public List<Customer> getCustomersOrderByNameAsc() {
    return customerService.getCustomersOrderByNameAsc();
  }

  @RequestMapping(method = RequestMethod.GET, path = "/get/byEmail/{mail}")
  public List<Customer> findAllByEmailContains(@PathVariable("mail") String mail) {
    return customerService.findAllByEmailContains(mail);
  }

  @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{id}")
  public void deleteById(@PathVariable("id") String id) {
    customerService.deleteById(id);
  }

  @RequestMapping(method = RequestMethod.PATCH, path = "/patch/{id}/{name}")
  public void updateCustomerById(@PathVariable("id") String id, @PathVariable("name") String name) {
    customerService.updateCustomerById(id, name);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/get/students/over/{age}")
  public List<Customer> getStudentCustomersOver(@PathVariable("age") int age){
    return customerService.getStudentCustomersOver(age);
  }

}
