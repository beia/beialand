package com.example.demo.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class CustomerRepositoryImpl implements CustomerRepositoryCustom {

  private MongoTemplate mongoTemplate;

  public CustomerRepositoryImpl(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public void updateCustomer(String id, String name) {
    Query updateQuery = Query.query(where("_id").is(id));
    Update updateCustomerName = Update.update("name", name);
    mongoTemplate.updateFirst(updateQuery, updateCustomerName, Customer.class);
  }

  @Override
  public List<Customer> getStudentCustomersOver(int age) {
    Query conditionQuery = Query.query(where("is_student").is(true)
        .and("age").gt(age));
    return mongoTemplate.find(conditionQuery, Customer.class);
  }
}
