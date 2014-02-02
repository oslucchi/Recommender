import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;


public class Matrix 
{
	final static int MAX_COLUMNS = 15;
	final static int MAX_ROWS = 20;
	private String path;
	private int[][] matrix;
	private String error = "";
	private int numOfColumns = 0;
	private int numOfRows = 0;
	
	public Matrix(String path)
	{
		this.path = path;
		matrix = new int[MAX_ROWS][MAX_COLUMNS];
	}

	protected double evalR(int i) 
	{
		int counter = 0;
		double sum = 0.0;
		for( int e = 0; e < MAX_COLUMNS; e++)
		{
			if(matrix[i][e] != 0)
			{
				counter ++;
				sum += matrix[i][e];
			}
		}
		return ((sum/counter)* 1.0);
	}
	
	protected int[] evalI(int u, int v)
	{
		int[] I = new int[MAX_COLUMNS];
		for(int i = 0; i< I.length; i++)
		{
			I[i] = -1;
		}
		if (u!=v)
		{
			int count = 0;
			for(int i = 0; i<I.length; i++)
			{
				if(matrix[u][i] != 0 && matrix[v][i] !=0)
					I[count++] = i;
			}
		}
		return I;
	}
	
	protected Integer[] evalU(int i, int u)
	{
		Vector<Integer> U = new Vector<Integer>();
		for (int j = 0; j < MAX_ROWS; j++)
		{
			if(matrix[j][i] != 0 && j != u && evalW(u, j) != 0.0)
			{
				U.add(j);	
			}
		}
		return U.toArray(new Integer[U.size()]);
		
	}
	
	private double evalK(int u, int i)
	{
		Integer[] U = evalU(i, u);
		double w = 0.0;
		for(int j = 0; j < U.length; j++)
		{
			w += Math.abs(evalW(u, U[j]));
		}
		return 1/w;
	}
	
	public int predict(int u, int i)
	{
		Integer[] U = evalU(i, u);
		double temp = 0.0;
		for(int j = 0; j < U.length; j++)
		{
			temp += evalW(u, U[j]) * (matrix[U[j]][i] - evalR(U[j]));
		}
		temp = temp * evalK(u, i);
		temp += evalR(u);
		return (int) Math.round(temp);
	}

	public double evalW(int u, int v)
	{
		int[] I = evalI(u, v);
		double Ru = evalR(u);
		double Rv = evalR(v);
		double Wnum = 0.0;
		double WdenU = 0.0;
		double WdenV = 0.0;
		
		for(int i = 0; I[i] != -1; i++ )
		{
			int j = I[i];
			Wnum += (matrix[u][j] - Ru) * (matrix[v][j] - Rv);
			WdenU += Math.pow((matrix[u][j] - Ru), 2.0);
			WdenV += Math.pow((matrix[v][j] - Rv), 2.0);
		}
		
		return (Wnum / Math.sqrt(WdenU*WdenV));
	}

	public int getNumOfColumns()
	{
		return numOfColumns;
	}

	public int getNumOfRows() {
		return numOfRows;
	}

	public int getElement(int i, int j) 
	{
		return matrix[i][j];
	}
	
	public void setElement(int i, int j, int value) 
	{
		matrix[i][j] = value;
	}
	
	public String getError() 
	{
		return error;
	}


	@SuppressWarnings("resource")
	public boolean loadFile()
	{
		Scanner input = null;
		try 
		{
			input = new Scanner(new File(path));
		}
		catch (FileNotFoundException e)
		{
			error = "file " + path + "not found";
			return false;
		}
		
		String val = null;
		int m = 0;
		int n = 0;
		try
		{
			String newLine =  input.nextLine().trim();
			while(newLine.length() > 0)
			{
				StringTokenizer st = new StringTokenizer(newLine);
	
				while(true)
				{
					try
					{
						val = st.nextToken();	
					}
					catch(NoSuchElementException e)
					{
						break;
					}
					try
					{
						matrix[m][n] = Integer.parseInt(val);
					}
					catch(NumberFormatException e)
					{
						error = "Number read not an integer";
						return false;
					}
					if(matrix[m][n] > 5 || matrix[m][n] < 0)
					{
						error = "number out of range";
						return false;
					}		
					n++;
					if (n >= MAX_COLUMNS)
					{
						error = "Too many columns";
						return false;
					}
				}
				m++;
				numOfRows = m;
				if (n > numOfColumns)
					numOfColumns = n;
				n = 0;
				
				if (m >= MAX_ROWS)
				{
					error = "Too many rows";
					return false;
				}
				newLine =  input.nextLine().trim();
			}
		}
		catch (NoSuchElementException e)
		{
			return true;
		}

		return true;
	}

	private boolean saveToFile(FileOutputStream ofStream, Matrix predictions)
	{
		boolean retVal = true;
		BufferedWriter bw = null;;
		DataOutputStream out = new DataOutputStream(ofStream);
		bw = new BufferedWriter(new OutputStreamWriter(out));
		try 
		{
			for(int i = 0; i < numOfRows; i++)
			{
				for(int j = 0; j < numOfColumns; j++)
				{
					if (matrix[i][j] == 0)
					{
						matrix[i][j] = predictions.getElement(i, j); 
					}
					bw.write(String.valueOf(matrix[i][j]) + " ");
					predictions.setElement(i, j, 0);
				}
				bw.write("\r\n");
			}
		}
		catch (IOException e) 
		{
			error = "I/O error opening file for write";
			retVal = false;
		}
		try 
		{
			bw.close();
			ofStream.close();
			out.close();
		}
		catch (IOException e1) 
		{
			;
		}
		return retVal;
	}
	
	public boolean saveToFile(String path, Matrix predictions)
	{
		FileOutputStream ofStream = null;
		try 
		{
			ofStream = new FileOutputStream(path);
		}
		catch (FileNotFoundException e)
		{
			error = "Specified path for file is invalid ";
			return false;
		}
		catch (SecurityException e) 
		{
			error = "User has no permission to write to '" + path + "'";
			return false;
		}
		return saveToFile(ofStream, predictions);
	}

	public boolean saveToFile(Matrix predictions)
	{
		FileOutputStream ofStream = null;
		try 
		{
			ofStream = new FileOutputStream(path);
		}
		catch (FileNotFoundException e) 
		{
			error = "File path you load from is not valid anymore";
			return false;
		}
		catch (SecurityException e) 
		{
			error = "File permissions have changed. User has no permission to write anymore";
			return false;
		}
		return saveToFile(ofStream, predictions);
	}
	
	public Throwable getThrowable()
	{
		return new Throwable();
	}

}
