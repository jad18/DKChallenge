package challenge;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.BiPredicate;


public class DataAnalyzer
{
	/* General Idea:
	 * Pre-process data into groups and analyze max and min values within group
	 * Allows for faster jumping across groups of similar values
	 * Can tune group size to fit data with hyperparameter of group size
	 */
	
	/* General pair searching method
	 * Special params:
	 * 	 indexChange: increment amount (negative to decrement)
	 * 	 groupEdge: where the group edge starts. 0 for beginning of group, 4 for end
	 *   checkThresholdMin: lambda function to check minimum threshold
	 *   checkThresholdMax: lambda function to check maximum threshold
	 *   continueLoop: lambda function for determining whether or not to continue loop
	 *   
	 * Returns:
	 *   Optional Pair of Integers specifying that valid range is from [first, second)
	 */
	public Optional<Pair<Integer,Integer>> searchContinuityPairGeneral(EntryContainer data,
								int indexBegin, int winLength, boolean getFullRange,
								int indexChange, int groupEdge,
								Predicate<Double> checkThresholdMin,
								Predicate<Double> checkThresholdMax,
								Predicate<Integer> continueLoop)							 
	{
		var rangeList = data.getRangeList();
		var dataArr = data.getDataArr();

		if(rangeList.size() <= 0) return Optional.empty();

		int groupSize = rangeList.get(0).size;

		int index = indexBegin;
		int groupIndex = indexBegin / groupSize;
		int runCount = 0;

		while(continueLoop.test(index))
		{
			// if entire group is valid, then jump over it
			if(index % groupSize == groupEdge &&
			   checkThresholdMin.test(rangeList.get(groupIndex).min) &&
			   checkThresholdMax.test(rangeList.get(groupIndex).max))
			{
				runCount += rangeList.get(groupIndex).size;
				index += indexChange * rangeList.get(groupIndex).size;
				groupIndex += indexChange;
			}
			else
			{
				// otherwise, search through each value inside
				double val = dataArr.get(index);
				if(checkThresholdMin.test(val) && checkThresholdMax.test(val))
					runCount++;
				else
				{
					if(getFullRange && runCount >= winLength)
						return Optional.of(new Pair<Integer,Integer>(indexBegin,index));
					runCount = 0;
					indexBegin = index + indexChange;
				}

				index += indexChange;
				if(index % groupSize == groupEdge) groupIndex += indexChange;
			}

			if(!getFullRange && runCount >= winLength)
				return Optional.of(new Pair<Integer,Integer>(indexBegin,index));

		}

		return Optional.empty();
	}
	
	
	public Optional<Pair<Integer,Integer>> searchContinuityAboveValuePair(EntryContainer data,
														int indexBegin, int indexEnd,
														double thresholdLo, int winLength,
														boolean checkFullRange, 
														Predicate<Double> checkThresholdMax)
	{
		Predicate<Double> checkThresholdMin = (value) -> (value >= thresholdLo);
		Predicate<Integer> continueLoopCond = (index) -> (index <= indexEnd);


		return searchContinuityPairGeneral(data, indexBegin, winLength, checkFullRange, 1, 0,
										   checkThresholdMin, checkThresholdMax,
										   continueLoopCond);

	}

	
	public Optional<Integer> searchContinuityAboveValue(EntryContainer data,
			int indexBegin, int indexEnd,
			double threshold, int winLength)
	{
		Predicate<Double> checkThresholdMax = (value) -> (true);
		
		var result = searchContinuityAboveValuePair(data, indexBegin, indexEnd, threshold, winLength,
													false, checkThresholdMax);

		if(result.isEmpty())
			return Optional.empty();

		return Optional.of(result.get().first);
	}


	public Optional<Integer> backSearchContinuityWithinRange(EntryContainer data, int indexBegin,
												int indexEnd, double thresholdLo,
												double thresholdHi, int winLength)
	{
		Predicate<Double> checkThresholdMin = (value) -> (value >= thresholdLo);
		Predicate<Double> checkThresholdMax = (value) ->(value <= thresholdHi);
		Predicate<Integer> continueLoopCond = (index) -> (index >= indexEnd);
		
		
		var result = searchContinuityPairGeneral(data, indexBegin, winLength, false, -1, 4,
												 checkThresholdMin, checkThresholdMax,
												 continueLoopCond);
		if(result.isEmpty())
			return Optional.empty();
		
		return Optional.of(result.get().first - winLength + 1);
	}


	public Optional<Integer> searchContinuityAboveValueTwoSignals(EntryContainer data1,
													EntryContainer data2, int indexBegin,
													int indexEnd, double threshold1,
													double threshold2, int winLength)
	{
		var rangeList1 = data1.getRangeList();
		var rangeList2 = data2.getRangeList();
		
		Predicate<Double> checkThresholdMax = (value) -> (true);
		
		if(rangeList1.size() <= 0 || rangeList2.size() <= 0) return Optional.empty();

		int index = indexBegin;

		while(index <= indexEnd)
		{
			var res1 = searchContinuityAboveValuePair(data1, indexBegin, indexEnd, threshold1,
													  winLength, true, checkThresholdMax);
			
			if(res1.isEmpty()) return Optional.empty();
			
			var res1_opened = res1.get();
			
			// if we find the end of the valid range of the first data set, then
			// see if the second data set has a valid range inside
			var res2 = searchContinuityAboveValue(data2, res1_opened.first, res1_opened.second,
													  threshold2, winLength);
				
			if(res2.isPresent())
				return res2;
		}

		return Optional.empty();
	}


	public ArrayList<Pair<Integer,Integer>> searchMultiContinuityWithinRange(EntryContainer data,
										  						  int indexBegin, int indexEnd,
										  						  double thresholdLo,
										  						  double thresholdHi, int winLength)
	{
		Predicate<Double> checkThresholdMax = (value) -> (value <= thresholdHi);
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
		
		Optional<Pair<Integer,Integer>> pair_res;
		
		do
		{
			pair_res = searchContinuityAboveValuePair(data, indexBegin, indexEnd, thresholdLo,
					  								  winLength, true, checkThresholdMax);
			
			if(pair_res.isPresent())
			{
				result.add(new Pair<Integer,Integer>(pair_res.get().first, pair_res.get().second - 1));
				indexBegin = pair_res.get().second;
			}
		} while(pair_res.isPresent());
		
		return result;
	}
	
	
	
	// Linear methods, for comparison
	
	public Optional<Integer> scav_lin(EntryContainer data, int indexBegin, int indexEnd,
			 			double threshold, int winLength)
	{
		var dataArr = data.getDataArr();
		int count = 0;

		for(int i=indexBegin; i<=indexEnd; i++)
		{

			if(dataArr.get(i) >= threshold)
			{
				count++;
				if(count >= winLength) return Optional.of(indexBegin);
			}
			else
			{
				count = 0;
				indexBegin = i + 1;
			}
		}
		return Optional.empty();
	}
	
	
	// Note: implement linear scans with generics
	// Allows us to pass in a pair of ArrayLists as data and then check the
	// threshold that way --> can implement SCAVTS using this function without
	// more function calls
	public <T> Optional<Integer> scav_lin2(T data, int indexBegin, int indexEnd,
			 			int winLength, BiPredicate<T,Integer> checkThreshold)
	{
		int count = 0;

		for(int i=indexBegin; i<=indexEnd; i++)
		{

			if(checkThreshold.test(data, i))
			{
				count++;
				if(count >= winLength) return Optional.of(indexBegin);
			}
			else
			{
				count = 0;
				indexBegin = i + 1;
			}
		}
		return Optional.empty();
	}

	
	public Optional<Integer> bscwr_lin(EntryContainer data, int indexBegin, int indexEnd,
				 		 double thresholdLo, double thresholdHi, int winLength)
	{
		var dataArr = data.getDataArr();
		int count = 0;

		for(int i=indexBegin; i>=indexEnd; i--)
		{

			if(dataArr.get(i) >= thresholdLo && dataArr.get(i) <= thresholdHi)
			{
				count++;
				if(count >= winLength) return Optional.of(i);
			}
			else
			{
				count = 0;
				indexBegin = i - 1;
			}
		}
		
		return Optional.empty();
	}
	
	public Optional<Integer> scavts_lin(EntryContainer data1, EntryContainer data2, int indexBegin,
						  		int indexEnd, double threshold1, double threshold2, int winLength)
	{
		var dataArr1 = data1.getDataArr();
		int count = 0;
		int i;
		 
		for(i=indexBegin; i<=indexEnd; i++)
		{

			if(dataArr1.get(i) >= threshold1)
				count++;
			else
			{
				if(count >= winLength)
				{
					var result2 = scav_lin(data2, indexBegin, i, threshold2, winLength);
					if(result2.isPresent())
						return result2;
				}
				
				count = 0;
				indexBegin = i + 1;
			}
		}
		
		if(count >= winLength)
		{
			var result2 = scav_lin(data2, indexBegin, i, threshold2, winLength);
			if(result2.isPresent())
				return result2;
		}
		
		return Optional.empty();
	}
	
	public ArrayList<Pair<Integer,Integer>> smcwr_lin(EntryContainer data, int indexBegin, int indexEnd,
			  							       double thresholdLo, double thresholdHi, int winLength)
	{
		var dataArr = data.getDataArr();
		
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
		
		int index = indexBegin;
		int runCount = 0;
		
		while(index <= indexEnd)
		{
			if(dataArr.get(index) >= thresholdLo && dataArr.get(index) <= thresholdHi)
				runCount++;
			else
			{
				if(runCount >= winLength)
					result.add(new Pair<Integer,Integer>(indexBegin, index - 1));
				
				runCount = 0;
				indexBegin = index + 1;
			}
			index++;
		}
		
		if(runCount >= winLength)
			result.add(new Pair<Integer,Integer>(indexBegin, index - 1));
		
		return result;
	}
}

