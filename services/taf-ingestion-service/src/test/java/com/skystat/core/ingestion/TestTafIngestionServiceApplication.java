package com.skystat.core.ingestion;

import org.springframework.boot.SpringApplication;

public class TestTafIngestionServiceApplication {

  public static void main(String[] args) {
    SpringApplication.from(TafIngestionServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
