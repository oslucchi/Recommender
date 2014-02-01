

import java.awt.EventQueue;

public class Recommender {

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
