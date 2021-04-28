package com.DiamondKinetics.DKChallenge;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import challenge.DataAnalyzer;
import challenge.DataContainer;

@SpringBootTest
class DkChallengeApplicationTests {

	@Test
	void runTest() {
		System.out.println("\n-----Tests-----\n");
		String args[] = {"5", "src/main/resources/latestSwing.csv"};
		
		if(args.length != 2)
		{
			System.err.println("Invalid number of arguments");
			System.exit(1);
		}
		
		int groupSize = Integer.parseInt(args[0]);
		if(groupSize <= 0)
		{
			System.err.println("Invalid group size. Must be a positive integer");
			System.exit(2);
		}
		
		DataContainer dc = new DataContainer(groupSize, args[1]);
		DataAnalyzer da = new DataAnalyzer();
		
		
		var res1 = da.searchContinuityAboveValue(dc.getWz(), 582, 888, 1.5, 121);
		if(res1.isPresent())
			System.out.println(res1.get());
		else
			System.out.println("Not found!");
			
		
		var res2 = da.backSearchContinuityWithinRange(dc.getAy(), 500, 100, 0.1, 0.2, 10);
		if(res2.isPresent())
			System.out.println(res2.get());
		else
			System.out.println("Not found!");
			
		
		var res3 = da.searchContinuityAboveValueTwoSignals(dc.getAy(), dc.getWx(), 50, 1000,
													   	   0.23, 2.2, 5);
		if(res3.isPresent())
			System.out.println(res3.get());
		else
			System.out.println("Not found!");
		
		
		var res4 = da.searchMultiContinuityWithinRange(dc.getAz(), 302, 1100, -0.5, 0, 10);
		System.out.println("\n[");
		for(var value : res4)
			System.out.println("  (" + value.first.toString() + " to " + value.second.toString() + ")");
		System.out.println("]");
		
			
		var res1_lin = da.scav_lin(dc.getWz(), 582, 888, 1.5, 121);
		assert((res1.isEmpty() && res1_lin.isEmpty()) ||
				res1.get().equals(res1_lin.get()));
			
			
		var res2_lin = da.bscwr_lin(dc.getAy(), 500, 100, 0.1, 0.2, 10);
		assert((res2.isEmpty() && res2_lin.isEmpty()) ||
				res2.get().equals(res2_lin.get()));
			
			
		var res3_lin = da.scavts_lin(dc.getAy(), dc.getWx(), 50, 1000, 0.23, 2.2, 5);
		assert((res3.isEmpty() && res3_lin.isEmpty()) ||
				res3.get().equals(res3_lin.get()));
			
			
		var res4_lin = da.smcwr_lin(dc.getAz(), 302, 1100, -0.5, 0, 10);
		boolean res4_are_equal = true;
		if(res4.size() != res4_lin.size()) res4_are_equal = false;
		else
		{
			for(int i = 0; i < res4.size(); i++)
			{
				if(!res4.get(i).first.equals(res4_lin.get(i).first) ||
				   !res4.get(i).second.equals(res4_lin.get(i).second))
				{
					res4_are_equal = false;
					break;
				}
			}
		}
		
		assert(res4_are_equal);
		
		System.out.println("\nPasses all comparisons with linear scans");
		
		
	}

}
