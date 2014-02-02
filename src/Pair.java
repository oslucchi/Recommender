
public class Pair implements Comparable<Pair>
{
	private int index = 0;
	private double value = 0;
	
	public Pair(int index, double value)
	{
		this.index = index;
		this.value = value;
	}

	public int getIndex() {
		return index;
	}

	public double getValue() {
		return value;
	}

	public int compareTo(Pair arg0) 
	{
		if (this.value < arg0.value)
			return 1;
		else if (this.value == arg0.value)
			return 0;
		else
			return -1;
	}

}
