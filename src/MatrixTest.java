import static org.junit.Assert.*;

import org.junit.Test;


public class MatrixTest {

	@Test
	public void newInstanceOfMatrix()
	{
		Matrix matrix = new Matrix("data/good.dat");
		assertTrue(matrix != null);
	}
	
	@Test
	public void testLoadGoodData()
	{
		Matrix matrix = new Matrix("data/good.dat");
		assertTrue(matrix.loadFile());
	}

	@Test
	public void testLoadBadData()
	{
		Matrix matrix = new Matrix("data/bad.dat");
		assertFalse(matrix.loadFile());
		assertTrue(matrix.getError().compareTo("Too many rows") == 0);
	}
	
	@Test
	public void testBadInputItem()
	{
		Matrix matrix = new Matrix("data/badItem.data");
		assertFalse(matrix.loadFile());
		assertTrue(matrix.getError().compareTo("number out of range") == 0);
		
	}
	
	@Test
	public void testRaccomandationAverage()
	{
		Matrix matrix = new Matrix("data/good.dat");
		assertTrue(matrix.loadFile());
		assertTrue(matrix.evalR(2)  == 3.0) ;
		
	}
	
	@Test
	public void testEvalSetI()
	{
		Matrix matrix = new Matrix("data/good.dat");
		assertTrue(matrix.loadFile());
		int[] I = matrix.evalI(0,1);
		assertTrue(I[0] == 2);
		assertTrue(I[1] == 10);
		assertTrue(I[2] == -1);
	}
	
	@Test
	public void testEvalW()
	{
		Matrix matrix = new Matrix("data/good.dat");
		assertTrue(matrix.loadFile());
		assertEquals(matrix.evalW(0, 1), -0.96090, 0.0001);
	}
	
	@Test
	public void testEvalU()
	{
		Matrix matrix = new Matrix("data/good.dat");
		assertTrue(matrix.loadFile());
		Integer[] U = matrix.evalU(9, 2);
		assertTrue(U[0] == 1);
		assertTrue(U.length == 1);
		U = matrix.evalU(3, 1);
		assertTrue(U[0] == 2);
		assertTrue(U[1] == 4);
		assertTrue(U.length == 2);
	}
		
}
