

import java.awt.EventQueue;

public class Reccomender {

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				UserInterface ui = new UserInterface();
				ui.setVisible(true);
			}
		});
	}
}
