package com.demo.config;

import org.springframework.batch.item.ItemProcessor;

import com.demo.model.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer>{

	@Override
	public Customer process(Customer customer) throws Exception {
	
		System.out.println(customer);
		return customer;
	}

}
