package com.omid;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@EnableBatchProcessing
@SpringBootApplication
public class BatchApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }


    private final JobLauncher jobLauncher;
    private final ApplicationContext applicationContext;

    public BatchApplication(JobLauncher jobLauncher, ApplicationContext applicationContext) {
        this.jobLauncher = jobLauncher;
        this.applicationContext = applicationContext;
    }


    @Override
    public void run(String... args) throws Exception {

        Job job = (Job) applicationContext.getBean("insertIntoDbFromCsvJob");

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();

        var jobExecution = jobLauncher.run(job, jobParameters);

        var batchStatus = jobExecution.getStatus();
        while (batchStatus.isRunning()) {
            System.out.println("Still running...");
            Thread.sleep(5000L);
        }
    }
}
