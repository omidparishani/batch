package com.omid.batch.job;

import com.omid.batch.job.listener.JobCompletionNotificationListener;
import com.omid.batch.job.listener.PersonItemReadListener;
import com.omid.batch.job.mapper.PersonRowMapper;
import com.omid.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.PlatformTransactionManager;

public class ExcelToDatabaseJob {
    private final JobRepository jobRepository;
    public static final Logger logger = LoggerFactory.getLogger(CsvToDatabaseJob.class);

    @Value("classpath:excel/e.xlsx")
    byte[] excelFile;

    public ExcelToDatabaseJob(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Bean
    public Job insertIntoDbFromExcelJob(Step step1) {
        var name = "Persons Import Job";
        var builder = new JobBuilder(name, jobRepository);
        return builder.start(step1).listener(new JobCompletionNotificationListener()).build();
    }

    @Bean
    public Step step1(ItemReader<Person> reader,
                      ItemWriter<Person> writer,
                      ItemProcessor<Person, Person> processor,
                      PlatformTransactionManager txManager) {

        var name = "INSERT CSV RECORDS To DB Step";
        var builder = new StepBuilder(name, jobRepository);

        return builder
                .<Person, Person>chunk(5, txManager)
                .reader(reader)
                .processor(processor)
                .listener(new PersonItemReadListener())   //Reader listener
                .writer(writer)
                .build();
    }

    @Bean
    public ItemStreamReader<Person> reader(@Value("#{stepExecution}") StepExecution stepExecution){
        System.out.println(stepExecution.getExecutionContext());
        PoiItemReader<Person> reader = new PoiItemReader<>();

        reader.setLinesToSkip(1);
        reader.setResource(new ByteArrayResource(excelFile));
        reader.setRowMapper(new PersonRowMapper());
        reader.setMaxItemCount(100_000);

        logger.info("################insured excel file id :]");

        return reader;    }
}
