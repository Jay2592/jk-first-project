package com.example.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@org.springframework.boot.autoconfigure.condition.ConditionalOnBean(org.springframework.batch.core.configuration.annotation.JobBuilderFactory.class)
public class BatchConfig {

    @Bean
    public Job sampleJob(JobBuilderFactory jobs, Step sampleStep) {
        return jobs.get("sampleJob")
                .incrementer(new RunIdIncrementer())
                .start(sampleStep)
                .build();
    }

    @Bean
    public Step sampleStep(StepBuilderFactory steps, SampleTasklet tasklet) {
        return steps.get("sampleStep").tasklet(tasklet).build();
    }
}
