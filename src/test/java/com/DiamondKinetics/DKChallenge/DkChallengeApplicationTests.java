package com.DiamondKinetics.DKChallenge;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import challenge.Driver;

@SpringBootTest
class DkChallengeApplicationTests {

	@Test
	void runTest() {
		System.out.println("\n-----Tests-----\n");
		Driver d = new Driver();
		String args[] = {"5", "src/main/resources/latestSwing.csv"};
		d.run(args);
	}

}
