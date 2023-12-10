package com.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.dao.CustomerRepository;
import com.demo.model.Customer;

@Service
public class CustomerService {

	@Autowired
	CustomerRepository customerRepository;
	
	//Register Customer
	public Customer registerCustomer(Customer customer) {
		return customerRepository.save(customer);
	}
}
