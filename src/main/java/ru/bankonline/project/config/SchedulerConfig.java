package ru.bankonline.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.bankonline.project.services.scheduler.DepositScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final DepositScheduler depositScheduler;

    public SchedulerConfig(DepositScheduler depositScheduler) {
        this.depositScheduler = depositScheduler;
    }

    @Scheduled(fixedDelay = 86400000)
    public void scheduleDeposit() {
        depositScheduler.deposit();
    }
}
