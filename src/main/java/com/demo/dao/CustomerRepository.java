package com.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

}
