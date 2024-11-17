package com.project.parkingfinder.config;

import org.quartz.Job;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    private final AutowireCapableBeanFactory beanFactory;
    //TODO: HOW THIS CONFIG WORK
    // WHY Quartz need empty constructor
    //
    public QuartzConfig(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Bean
    public JobFactory springBeanJobFactory() {
        return (bundle, scheduler) -> {
            Object job = beanFactory.createBean(bundle.getJobDetail().getJobClass());
            return (Job) job;
        };
    }
}