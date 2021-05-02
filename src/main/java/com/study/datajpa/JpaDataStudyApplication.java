package com.study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing
@SpringBootApplication
public class JpaDataStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JpaDataStudyApplication.class, args);
    }


    @Bean
    public AuditorAware<String> auditorProvider() {
        // 실제는 세션에서 넣어서 쓰면 된다.
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
