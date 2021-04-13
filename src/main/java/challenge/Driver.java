package challenge;

import java.util.NoSuchElementException;

public class Driver
{
	
	public void run(String[] args)
	{
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
		
		try
		{
			int res1 = da.searchContinuityAboveValue(dc.getWz(), 582, 888, 1.5, 121);
			System.out.println(res1);
		
			int res2 = da.backSearchContinuityWithinRange(dc.getAy(), 500, 100, 0.1, 0.2, 10);
			System.out.println(res2);
		
			int res3 = da.searchContinuityAboveValueTwoSignals(dc.getAy(), dc.getWx(), 50, 1000,
														   	   0.23, 2.2, 5);
			System.out.println(res3);
		
			var res4 = da.searchMultiContinuityWithinRange(dc.getAz(), 302, 1100, -0.5, 0, 10);
			System.out.println("\n[");
			for(var value : res4)
				System.out.println("  (" + value.first.toString() + " to " + value.second.toString() + ")");
			System.out.println("]");
		
			System.out.println("\nMatches with linear scan results:");
			System.out.println(res1 == da.scav_lin(dc.getWz(), 582, 888, 1.5, 121));
			System.out.println(res2 == da.bscwr_lin(dc.getAy(), 500, 100, 0.1, 0.2, 10));
			System.out.println(res3 == da.scavts_lin(dc.getAy(), dc.getWx(), 50, 1000, 0.23, 2.2, 5));
			
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
			
			System.out.println(res4_are_equal);
			
		}
		catch(NoSuchElementException e)
		{
			System.err.println("Error: index cannot be found");
		}
		
	}

}