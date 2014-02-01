
public class Rating
{
	static final int TYPE_RATING = 1;
	static final int TYPE_PREDICTION = 2;
	static final int TYPE_LABEL = 3;
	private int type = 0;
	private int value;
	
	public Rating(int type, int value)
	{
		this.type = type;
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public int getValue() {
		return value;
	}
	
	
}
