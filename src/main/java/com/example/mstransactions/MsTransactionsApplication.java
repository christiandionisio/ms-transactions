package com.example.mstransactions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Transaction application.
 *
 * @author Alisson Arteaga / Christian Dionisio
 * @version 1.0
 */
//@EnableEurekaClient
@SpringBootApplication
public class MsTransactionsApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsTransactionsApplication.class, args);
  }

}
