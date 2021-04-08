package challenge;

import java.util.*;


public class EntryContainer {
	
	private ArrayList<EntryTuple> rangeList;
	private ArrayList<Double> dataArr;

	public EntryContainer(ArrayList<Double> data, int groupSize)
	{
		this.dataArr = data; 
		this.rangeList = new ArrayList<EntryTuple>();
		
		// pre-process the data in groups of size groupSize to find min
		// and max for faster searching. groupSize is a hyperparameter
		for(int index = 0; index < data.size(); /* update index below */ )
		{
			double min = data.get(index);
			double max = min;
			
			int i = 1;
			
			while(i < groupSize && index < data.size())
			{
				double val = data.get(index);
				if(val < min) min = val;
				else if(val > max) max = val;
				
				index++;
				i++;
			}
			
			rangeList.add(new EntryTuple(min, max, i));
			
		}
	} 
	
	ArrayList<EntryTuple> getRangeList()
	{
		return this.rangeList;
	}
	
	ArrayList<Double> getDataArr()
	{
		return this.dataArr;
	}
}
