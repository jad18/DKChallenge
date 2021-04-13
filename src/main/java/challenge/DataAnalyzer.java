package challenge;

import java.util.ArrayList;
import java.util.NoSuchElementException;


public class DataAnalyzer
{
	/* General Idea:
	 * Pre-process data into groups and analyze max and min values within group
	 * Allows for faster jumping across groups of similar values
	 * Can tune group size to fit data with hyperparameter of group size
	 */
	public int searchContinuityAboveValue(EntryContainer data, int indexBegin, int indexEnd,
			  							  double threshold, int winLength)
										 throws NoSuchElementException
	{
		var rangeList = data.getRangeList();
		var dataArr = data.getDataArr();
		
		if(rangeList.size() <= 0) return -1;
			int groupSize = rangeList.get(0).size;

		int index = indexBegin;
		int groupIndex = indexBegin / groupSize;
		int runCount = 0;

		while(index <= indexEnd)
		{
			// if entire group is valid, then jump over it
			if(index % groupSize == 0 &&
			   rangeList.get(groupIndex).min >= threshold)
			{
				runCount += rangeList.get(groupIndex).size;
				index += rangeList.get(groupIndex).size;
				groupIndex++;
			}
			else
			{
				// otherwise, search through each value inside
				double val = dataArr.get(index);
				if(val >= threshold) runCount++;
				else
				{
					runCount = 0;
					indexBegin = index + 1;
				}

				index++;
				if(index % groupSize == 0) groupIndex++;
			}

			if(runCount >= winLength) return indexBegin;

		}

		throw new NoSuchElementException();
	}



	public int backSearchContinuityWithinRange(EntryContainer data, int indexBegin, int indexEnd,
				   							   double thresholdLo, double thresholdHi, int winLength)
				   							  throws NoSuchElementException
	{
		var rangeList = data.getRangeList();
		var dataArr = data.getDataArr();
		
		if(rangeList.size() <= 0) return -1;
		int groupSize = rangeList.get(0).size;

		int index = indexBegin;
		int groupIndex = indexBegin / groupSize;
		int runCount = 0;

		while(index >= indexEnd)
		{
			// if entire group is valid, then jump over it
			// note that we check if we're at the end of a group, not the front
			if(index % groupSize == 4 &&
			   rangeList.get(groupIndex).min >= thresholdLo &&
			   rangeList.get(groupIndex).max <= thresholdHi)
			{
				runCount += rangeList.get(groupIndex).size;
				index -= rangeList.get(groupIndex).size;
				groupIndex--;
			}
			else
			{
				// otherwise, search through each value inside
				double val = dataArr.get(index);
				if(val >= thresholdLo && val <= thresholdHi) runCount++;
				else
				{
					runCount = 0;
					indexBegin = index - 1;
				}

				index--;
				if(index % groupSize == 4) groupIndex--;
			}

			if(runCount >= winLength) return (indexBegin - winLength + 1);

		}

		throw new NoSuchElementException();
	}


	public int searchContinuityAboveValueTwoSignals(EntryContainer data1, EntryContainer data2,
													int indexBegin, int indexEnd,
													double threshold1, double threshold2,
													int winLength)
												   throws NoSuchElementException
	{
		var rangeList1 = data1.getRangeList();
		var dataArr1 = data1.getDataArr();
		var rangeList2 = data2.getRangeList();
		
		if(rangeList1.size() <= 0 || rangeList2.size() <= 0) return -1;
		
		int groupSize1 = rangeList1.get(0).size;

		int index = indexBegin;
		int groupIndex = indexBegin / groupSize1;
		int runCount = 0;

		while(index <= indexEnd)
		{
			// if entire group is valid, then jump over it
			if(index % groupSize1 == 0 &&
			   rangeList1.get(groupIndex).min >= threshold1)
			{
				runCount += rangeList1.get(groupIndex).size;
				index += rangeList1.get(groupIndex).size;
				groupIndex++;
			}
			else
			{
				// otherwise, search through each value inside
				double val = dataArr1.get(index);
				if(val >= threshold1) runCount++;
				else
				{
					// if we find the end of the valid range of the first data set, then
					// see if the second data set has a valid range inside
					if(runCount >= winLength)
					{
						int result2 = searchContinuityAboveValue(data2, indexBegin, index,
																 threshold2, winLength);
						if(result2 > 0) return result2;
					}
					
					runCount = 0;
					indexBegin = index + 1;
				}

				index++;
				if(index % groupSize1 == 0) groupIndex++;
			}

		}
		
		if(runCount >= winLength)
		{
			try
			{
				int result2 = searchContinuityAboveValue(data2, indexBegin, index,
													 	 threshold2, winLength);
				return result2;
			}
			catch(NoSuchElementException e) {}
		}

		throw new NoSuchElementException();
	}


	ArrayList<Pair<Integer,Integer>> searchMultiContinuityWithinRange(EntryContainer data,
										  						  int indexBegin, int indexEnd,
										  						  double thresholdLo,
										  						  double thresholdHi, int winLength)
	{
		var rangeList = data.getRangeList();
		var dataArr = data.getDataArr();
		
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
		if(rangeList.size() <= 0) return result;
		int groupSize = rangeList.get(0).size;

		int index = indexBegin;
		int groupIndex = indexBegin / groupSize;
		int runCount = 0;

		while(index <= indexEnd)
		{
			// if entire group is valid, then jump over it
			if(index % groupSize == 0 &&
			   rangeList.get(groupIndex).min >= thresholdLo &&
			   rangeList.get(groupIndex).max <= thresholdHi)
			{
				runCount += rangeList.get(groupIndex).size;
				index += rangeList.get(groupIndex).size;
				groupIndex++;
			}
			else
			{
				// otherwise, search through each value inside
				double val = dataArr.get(index);
				if(val >= thresholdLo && val <= thresholdHi) runCount++;
				else
				{
					// if we get to the end of the run and it can be included, then
					// add this to the array list
					if(runCount >= winLength)
						result.add(new Pair<Integer,Integer>(indexBegin, index - 1));

					runCount = 0;
					indexBegin = index + 1;
				}

				index++;
				if(index % groupSize == 0) groupIndex++;
			}
		}

		if(runCount >= winLength)
			result.add(new Pair<Integer,Integer>(indexBegin, index - 1));

		return result;
	}
	
	
	// Linear methods, for comparison
	
	public int scav_lin(EntryContainer data, int indexBegin, int indexEnd,
			 			double threshold, int winLength)
			 		   throws NoSuchElementException
	{
		var dataArr = data.getDataArr();
		int count = 0;

		for(int i=indexBegin; i<=indexEnd; i++)
		{

			if(dataArr.get(i) >= threshold)
			{
				count++;
				if(count >= winLength) return indexBegin;
			}
			else
			{
				count = 0;
				indexBegin = i + 1;
			}
		}
		throw new NoSuchElementException();
	}
	
	public int bscwr_lin(EntryContainer data, int indexBegin, int indexEnd,
				 		 double thresholdLo, double thresholdHi, int winLength)
						throws NoSuchElementException
	{
		var dataArr = data.getDataArr();
		int count = 0;

		for(int i=indexBegin; i>=indexEnd; i--)
		{

			if(dataArr.get(i) >= thresholdLo && dataArr.get(i) <= thresholdHi)
			{
				count++;
				if(count >= winLength) return i;
			}
			else
			{
				count = 0;
				indexBegin = i - 1;
			}
		}
		throw new NoSuchElementException();
	}
	
	public int scavts_lin(EntryContainer data1, EntryContainer data2, int indexBegin,
						  int indexEnd, double threshold1, double threshold2, int winLength)
						 throws NoSuchElementException
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
					int result2 = scav_lin(data2, indexBegin, i, threshold2, winLength);
					if(result2 > 0) return result2;
				}
				
				count = 0;
				indexBegin = i + 1;
			}
		}
		
		if(count >= winLength)
		{
			try
			{
				int result2 = scav_lin(data2, indexBegin, i, threshold2, winLength);
				return result2;
			}
			catch(NoSuchElementException e) {}
		}
		
		throw new NoSuchElementException();
	}
}

