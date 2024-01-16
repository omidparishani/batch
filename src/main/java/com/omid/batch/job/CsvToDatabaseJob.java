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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class CsvToDatabaseJob {

    public static final Logger logger = LoggerFactory.getLogger(CsvToDatabaseJob.class);

    private static final String INSERT_QUERY = """
      insert into person (first_name, last_name)
      values (:firstName,:lastName)""";

    private final JobRepository jobRepository;

    public CsvToDatabaseJob(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Value("classpath:csv/inputData.csv")
    private Resource inputFeed;



    @Bean
    public Job insertIntoDbFromCsvJob(Step step1) {
        var name = "Persons Import Job";
        var builder = new JobBuilder(name, jobRepository);
        return builder.start(step1).listener(new JobCompletionNotificationListener()).build();
    }

    @Bean
    public Step step1(ItemReader<Person> reader,
                      ItemWriter<Person> writer,
//                      ItemProcessor<Person, Person> processor,
                      PlatformTransactionManager txManager) {

        var name = "INSERT CSV RECORDS To DB Step";
        var builder = new StepBuilder(name, jobRepository);

        return builder
                .<Person, Person>chunk(5, txManager)
                .reader(reader)
                .listener(new PersonItemReadListener())   //Reader listener
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<Person> csvFileReader(
            LineMapper<Person> lineMapper) {
        var itemReader = new FlatFileItemReader<Person>();
        itemReader.setLineMapper(lineMapper);
        itemReader.setResource(inputFeed);
        return itemReader;
    }

    @Bean
    public DefaultLineMapper<Person> lineMapper(LineTokenizer tokenizer,
                                                FieldSetMapper<Person> mapper) {
        var lineMapper = new DefaultLineMapper<Person>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);
        return lineMapper;
    }

    @Bean
    public BeanWrapperFieldSetMapper<Person> fieldSetMapper() {
        var fieldSetMapper = new BeanWrapperFieldSetMapper<Person>();
        fieldSetMapper.setTargetType(Person.class);
        return fieldSetMapper;
    }

    @Bean
    public DelimitedLineTokenizer tokenizer() {
        var tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("firstName", "lastName");
        return tokenizer;
    }

    @Bean
    public JdbcBatchItemWriter<Person> jdbcItemWriter(DataSource dataSource) {
        var provider = new BeanPropertyItemSqlParameterSourceProvider<Person>();
        var itemWriter = new JdbcBatchItemWriter<Person>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql(INSERT_QUERY);
        itemWriter.setItemSqlParameterSourceProvider(provider);
        return itemWriter;
    }

}