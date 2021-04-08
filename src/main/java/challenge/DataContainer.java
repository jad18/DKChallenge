package challenge;

import java.io.*;
import java.util.ArrayList;
import lombok.Getter;

public class DataContainer
{
	@Getter
	private EntryContainer timestamp;
	
	@Getter
	private EntryContainer ax;
	
	@Getter
	private EntryContainer ay;
	
	@Getter
	private EntryContainer az;
	
	@Getter
	private EntryContainer wx;
	
	@Getter
	private EntryContainer wy;
	
	@Getter
	private EntryContainer wz;
	
	public DataContainer(int groupSize, String filename)
	{
		int numEntries = 7;
		
		ArrayList<ArrayList<Double>> dataList = new ArrayList<ArrayList<Double>>(numEntries);
		for(int i=0; i<numEntries; i++)
			dataList.add(new ArrayList<Double>());
		
		// read data from .csv file
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while((line = br.readLine()) != null)
			{
				String[] dataLine = line.split(",");
				
				for(int i=0; i<numEntries; i++)
				{
					dataList.get(i).add(Double.parseDouble(dataLine[i]));
				}
			}
			
			br.close();
			
			// set arrays of data to data fields
			this.timestamp = new EntryContainer(dataList.get(0), groupSize);
			this.ax = new EntryContainer(dataList.get(1), groupSize);
			this.ay = new EntryContainer(dataList.get(2), groupSize);
			this.az = new EntryContainer(dataList.get(3), groupSize);
			this.wx = new EntryContainer(dataList.get(4), groupSize);
			this.wy = new EntryContainer(dataList.get(5), groupSize);
			this.wz = new EntryContainer(dataList.get(6), groupSize);
		}
		catch (IOException e)
		{
			System.err.println("Error: file does not exist");
		}
	}
	
}