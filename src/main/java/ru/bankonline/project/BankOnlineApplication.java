package ru.bankonline.project;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BankOnlineApplication {

    /***
     * Создаёт и возвращает экземпляр ModelMapper для
     * маппинга объектов
     * @return экземпляр ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    public static void main(String[] args) {
        SpringApplication.run(BankOnlineApplication.class, args);
    }
}