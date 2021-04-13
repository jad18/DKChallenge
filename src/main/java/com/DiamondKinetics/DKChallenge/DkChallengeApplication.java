package com.DiamondKinetics.DKChallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import challenge.Driver;

@SpringBootApplication
public class DkChallengeApplication {

	/* Run with two arguments:
	 * 1) groupSize, an integer indicating the group size hyperparameter
	 * 2) filename, a path to the .csv file
	 */
	public static void main(String[] args) {
		SpringApplication.run(DkChallengeApplication.class, args);
		
		System.out.println("\n-----Results-----\n");
		
		Driver d = new Driver();
		d.run(args);
	}

}
