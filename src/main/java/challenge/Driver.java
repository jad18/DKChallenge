package challenge;

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
			
		System.out.println(da.searchContinuityAboveValue(dc.getWz(), 582, 888, 1.5, 121));
		System.out.println(da.backSearchContinuityWithinRange(
						   	    dc.getAy(), 100, 500, 0.1, 0.2, groupSize));
		System.out.println(da.searchContinuityAboveValueTwoSignals(
								dc.getAy(), dc.getWx(), 50, 1000, 0.23, 2.2, 5));
		
		var res3 = da.searchMultiContinuityWithinRange(dc.getAz(), 302, 1100, -0.5, 0, 10);
		System.out.println("\n[");
		for(var value : res3)
			System.out.println("  (" + value.first.toString() + " to " + value.second.toString() + ")");
		System.out.println("]");
		
		System.out.println("\nLinear Results, for comparison:");
		System.out.println(da.scav_lin(dc.getWz(), 582, 888, 1.5, 121));
		System.out.println(da.bscwr_lin(dc.getAy(), 100, 500, 0.1, 0.2, groupSize));
		System.out.println(da.scavts_lin(dc.getAy(), dc.getWx(), 50, 1000, 0.23, 2.2, 5));
		
	}

}