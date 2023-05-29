package ru.bankonline.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.bankonline.project.services.scheduler.DepositScheduler;

/***
 * Конфигурация планировщика задач для периодичного выполнения метода deposit
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final DepositScheduler depositScheduler;

    @Autowired
    public SchedulerConfig(DepositScheduler depositScheduler) {
        this.depositScheduler = depositScheduler;
    }

    /***
     * Метод для планирования периодичного выполнения deposit
     * В данном случае интервал равен 24 часам (86400000 миллисекунд)
     */
    @Scheduled(fixedDelay = 86400000)
    public void scheduleDeposit() {
        depositScheduler.performInterestAccrualOnSavingsAccounts();
    }
}