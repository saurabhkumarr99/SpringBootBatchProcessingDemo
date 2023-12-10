package com.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.demo.dao.CustomerRepository;
import com.demo.model.Customer;

@Configuration
//@EnableBatchProcessing
public class SpringBatchConfig {

//	// Job builder
//	@Autowired
//	private JobBuilderFactory jobBuilderFactory;

	// Step Builder
//	@Autowired
//	private StepBuilderFactory stepBuilderFactory;

	// Cutomer repository
	@Autowired
	private CustomerRepository customerRepository;


	// Reader bean
	@Bean
	public FlatFileItemReader<Customer> reader() {

		FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();

		itemReader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
		itemReader.setName("csv-reader");
		itemReader.setLinesToSkip(1);
		itemReader.setLineMapper(lineMapper());

		return itemReader;
	}

	private LineMapper<Customer> lineMapper() {

		DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
		delimitedLineTokenizer.setDelimiter(",");
		delimitedLineTokenizer.setStrict(false);
		delimitedLineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo","dob");

		// Map csv data to customer class
		BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
		fieldSetMapper.setTargetType(Customer.class);

		// set both to line mapper
		lineMapper.setLineTokenizer(delimitedLineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);

		return lineMapper;
	}

	// Processor
	@Bean
	public CustomerProcessor processor() {
		return new CustomerProcessor();
	}

	// Writer
	@Bean
	public RepositoryItemWriter<Customer> writer() {

		RepositoryItemWriter<Customer> itemWriter = new RepositoryItemWriter<>();
		itemWriter.setRepository(customerRepository);
		itemWriter.setMethodName("save");

		return itemWriter;

	}

	// Step Builder
	@Bean
	public Step step1(JobRepository jobRepository , PlatformTransactionManager transactionManager) {

		return new StepBuilder("csv-step1",jobRepository).<Customer, Customer>chunk(10 , transactionManager).reader(reader()).processor(processor())
				.writer(writer()).build();
	}

	// Create Job
	@Bean
	public Job runJob(JobRepository jobRepository ,PlatformTransactionManager transactionManager) {

		return new JobBuilder("importCustomers",jobRepository).flow(step1(jobRepository,transactionManager))
				// .next(step1())
				.end().build();
	}

}
