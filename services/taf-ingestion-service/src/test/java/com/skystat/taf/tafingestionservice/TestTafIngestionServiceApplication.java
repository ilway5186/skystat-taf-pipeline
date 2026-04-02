package com.skystat.taf.tafingestionservice;

import org.springframework.boot.SpringApplication;

public class TestTafIngestionServiceApplication {

  public static void main(String[] args) {
    SpringApplication.from(TafIngestionServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
  }

}
