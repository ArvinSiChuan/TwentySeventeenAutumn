package com.arvinsichuan.twentyseventeenautumn.experiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExperimentApplication implements CommandLineRunner{
	@Autowired
	private CustomerRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ExperimentApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		repository.deleteAll();

		// save a couple of customers
		String[] titleA={"A","B"};
		String[][] contentA={{"AContent"},{"BContent"}};
		repository.save(new Customer(titleA,contentA));

		// fetch all customers
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Customer customer : repository.findAll()) {
			System.out.println(customer);
		}


	}

}
