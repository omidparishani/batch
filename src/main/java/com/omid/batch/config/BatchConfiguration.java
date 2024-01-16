package com.omid.batch.config;
import com.omid.entity.Person;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Bean
    public ItemReader<Person> excelItemReader() {
        PoiItemReader<Person> reader = new PoiItemReader<>();
        reader.setLinesToSkip(1); // Skip the header row
        reader.setResource(new ClassPathResource("Persons.xlsx")); // Path to your Excel file
        reader.setRowMapper(excelRowMapper());

        return reader;
    }

    @Bean
    public RowMapper<Person> excelRowMapper() {
        return (row, rowNum) -> {
            Person Person = new Person();
            Person.setId(row.getCell(0).getStringCellValue());
            Person.setName(row.getCell(1).getStringCellValue());
            // Set other properties as needed
            return Person;
        };
    }

    @Bean
    public ItemWriter<Person> databaseItemWriter() {
        return items -> {
            for (Person item : items) {
                // Save each item to the database
                // Use JPA or JDBC to persist the data
            }
        };
    }

    @Bean
    public Step step(ItemReader<Person> reader, ItemWriter<Person> writer) {
        return stepBuilderFactory.get("step")
                .<Person, Person>chunk(10)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get("job")
                .flow(step)
                .end()
                .build();
    }

}
