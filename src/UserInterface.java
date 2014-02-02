

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

public class UserInterface extends JFrame 
						   implements KeyListener, FocusListener, ActionListener, WindowStateListener, 
						   			  MouseListener, PopupMenuListener
{
    public class RecommenderCellRenderer extends DefaultTableCellRenderer 
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int col) {

			Component c = super.getTableCellRendererComponent(table, value,
										isSelected, hasFocus, row, col);
			Rating valueAt = (Rating) table.getModel().getValueAt(row, col);
			String s = "";
			if (valueAt != null) 
			{
				if (valueAt.getValue() == 0)
				{
					s = "";
				}
				else
				{
					s = String.valueOf(valueAt.getValue());
				}
				if (valueAt.getType() == Rating.TYPE_RATING)
				{
					c.setForeground(Color.BLACK);
				}
				else if (valueAt.getType() == Rating.TYPE_LABEL)
				{
					c.setForeground(Color.BLUE);
				}
				else
				{
					c.setForeground(Color.RED);
				}
				setText(s);
			}
			return c;
		}
    }
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    static private final int FILEOPEN = 0;
    static private final int FILESAVE = 1;
    static private final int FILESAVEAS = 2;
    static private final int FILEEXIT = 3;
    static private final int ABOUT = 4;
    static private final int CHGBTN = 5;
    static private final int PREDBTN = 6;
    static private final int GRID = 7;
    static private final int TOPRECON = 8;
    static private final int PEARCORR = 9;
    
    private String srcPath = null;
    
    private Matrix userData;
    private Matrix predictions;
    
    private Object[] sourceObj = new Object[10];
    
	private JTextField chgItemRow = new JTextField("", 2);;
	private JTextField chgItemCol = new JTextField("", 2);;
	private JTextField chgItemVal = new JTextField("", 1);
	private JLabel chgItemRowLab = new JLabel("row");
	private JLabel chgItemColLab = new JLabel("col");
	private JLabel chgItemValLab = new JLabel("val");
    private JButton predBtn = new JButton("Predict");
    private JButton chgBtn = new JButton("Change");
	private JTable grid = null;
	private JPopupMenu popUp = null; 
	private JLabel popupMenuName = new JLabel();

	private Container frameCont = this.getContentPane();

	private int statUserIndx = -1;

	@Override
	public void windowStateChanged(WindowEvent e) 
	{
		if (e.getNewState() == WindowEvent.WINDOW_CLOSED)
			System.exit(0);
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Pair[] rank = null;
		String displayMsg = "";
		Comparator<Pair> c = null;
		JFileChooser fc = null;
		FileNameExtensionFilter filter = null;

		// All objects having set an actionListener have been added to the sourceObj array
		// we compare the source of the ActionEvent received with each element of the array
		// to decide which action is needed
		int sourceIndex;
		for(sourceIndex = 0; sourceIndex < sourceObj.length; sourceIndex++)
		{
			if (e.getSource().equals(sourceObj[sourceIndex]))
				break;
		}

		switch(sourceIndex)
		{
		case FILEOPEN:
			// Menu File->Open
			fc = new JFileChooser(".");
			filter = new FileNameExtensionFilter("Recommender dat files", "dat");
			fc.setFileFilter(filter);
			boolean enableComponents = false;
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				userData = new Matrix(fc.getSelectedFile().getAbsolutePath());
				if (!userData.loadFile())
					JOptionPane.showMessageDialog(this, userData.getError());
				predictions = new Matrix(null);
				// Data loaded. We can now display recommendations and enable action components
				drawGrid();
				enableComponents = true;
			}
	        chgBtn.setEnabled(enableComponents);
	        predBtn.setEnabled(enableComponents);
	        chgItemCol.setEnabled(enableComponents);
	        chgItemRow.setEnabled(enableComponents);
	        chgItemVal.setEnabled(enableComponents);
			break;
			
		case FILESAVE:
			// Menu File->Save
			// Same path used for open will be used to save
			if (!userData.saveToFile(predictions))
			{
				JOptionPane.showMessageDialog(this, userData.getError());
			}
			break;
			
		case FILESAVEAS:
			// Menu File->Save As
			fc = new JFileChooser(".");
			filter = new FileNameExtensionFilter("Recommender dat files", "dat");
			fc.setFileFilter(filter);
			if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				userData.saveToFile(fc.getSelectedFile().getAbsolutePath(), predictions);
				// While saving, predictions became recommendations. We need to re-draw the
				// grid because the color shoudl change to black
				drawGrid();
			}
			break;
			
		case FILEEXIT:
			// Menu File->Exit
			System.exit(0);
			break;
			
		case ABOUT:
			// Menu About
			// Credit goes to Piotr Gwiazda
			// reference http://stackoverflow.com/questions/7483421/how-to-get-source-file-name-line-number-from-a-java-lang-class-object
			ProjectSummary ps = new ProjectSummary(srcPath);
			displayMsg = "Name:    Recommender\n" +
						 "Authors: Lorenzo Lucchini, Andrea ,,,,\n" + 
						 "\nProject summary:\n" +
						 "- Total number of classes " + ps.getNumberOfClasses() + "\n" +
						 "- Total number of methods " + ps.getNumberOfMethods() + "\n" +
						 "- Total number of lines   " + ps.getNumberOfLines() + "\n" +
						 "\nPer class summary:\n";
			for(int i = 0; i < ps.getNumberOfClasses(); i++)
			{
				displayMsg += "- Class '" + ps.getClassSummary(i).getName() + "'\n" +
							  "        Total number of methods    : " + ps.getClassSummary(i).getNumOfMethods() + "\n" +
							  "        Total number of Variables  : " + ps.getClassSummary(i).getNumOfFields() + "\n" +
							  "        Number of source code lines: " + ps.getClassSummary(i).getNumOfLines() + "\n";
			}
			JOptionPane.showMessageDialog(this, displayMsg);
			break;
			
		case CHGBTN:
			// Change button pressed
			int value = Integer.valueOf(chgItemVal.getText());
			int row = Integer.valueOf(chgItemRow.getText());
			int col = Integer.valueOf(chgItemCol.getText());
			// Get the new Rating item from user input and set the grid accordingly
			Rating item = new Rating(Rating.TYPE_RATING, value);
			grid.setValueAt(item, row, col);
			// Data inputed by user is rating, hence populate the userData matrix with the 
			// value and wipe the predictions matrix at the same location
			userData.setElement(row - 1, col - 1, value);
			predictions.setElement(row - 1, col - 1, 0);
			break;
			
		case PREDBTN:
			// Predictions button pressed
			// Scan the userData matrix and get predictions where 0 are found
			for(int i = 0; i < userData.getNumOfRows(); i++)
			{
				for (int j = 0; j< userData.getNumOfColumns(); j++)
				{
					if (userData.getElement(i,  j) == 0)
					{
						predictions.setElement(i, j, userData.predict(i, j));
					}
					else
					{
						// make sure that predictions is 0 where a valid recommendation is present
						predictions.setElement(i, j, 0);	
					}
				}
			}
			drawGrid();
			break;
			
		case GRID:
			break;

		case TOPRECON:
			break;

		case PEARCORR:
			break;
		}	
	}

	@Override
	public void focusLost(FocusEvent arg0) 
	{
		// Get the name of the component for which focus has been lost
		String compName = arg0.getComponent().getName();
		
		// 3 cases:
		//   a. user input is correct
		//   b. user input is out of bounds
		//   c. user input is not in right format (integer)
		// we check the b. and we rely on the exception thrown by Integer.parseInt for c.
		try
		{
			if (compName.compareTo("row") == 0)
			{
				// Check user input and report error. In case of data non integer an exception is thrown
				int rows = Integer.parseInt(chgItemRow.getText());
				//handle case b.
				if ((rows < 1) || (rows > userData.getNumOfRows()))
				{
					JOptionPane.showMessageDialog(this, "Value must be between 1 and " + userData.getNumOfRows());
					// Wipe out the inputed value
					wipeInputField("row", true);
				}
			}
			else if (compName.compareTo("col") == 0)
			{
				int cols = Integer.parseInt(chgItemCol.getText()); 
				if ((cols < 1) || (cols > userData.getNumOfColumns()))
				{
					JOptionPane.showMessageDialog(this, "Value must be between 1 and " + userData.getNumOfColumns());
					wipeInputField("col", true);
				}
			}
		}
		catch(NumberFormatException e)
		{
			// handle case c.
			if (compName.compareTo("row") == 0)
			{
				if (chgItemRow.getText().compareTo("") != 0)
				{
					JOptionPane.showMessageDialog(this, "Input data must be an integer");
					wipeInputField("row", true);
				}
			}
			else
			{
				if (chgItemCol.getText().compareTo("") != 0)
				{
					JOptionPane.showMessageDialog(this, "Input data must be an integer");
					wipeInputField("col", true);
				}
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) 
	{
		// Only digits, Delete and Backspace are allowed in TextFields
		
		if ((arg0.getKeyChar() != KeyEvent.VK_DELETE) &&
			(arg0.getKeyChar() != KeyEvent.VK_BACK_SPACE))
		{
			if ((arg0.getKeyChar() < '0') || (arg0.getKeyChar() > '9'))
			{
				JOptionPane.showMessageDialog(this, "Only digits are allowed in this field");
			    arg0.consume();
				return;
			}
			
			// if the field for which we received keyTyped is the Value one, we limit
			// the input length to 1 character only and values from 0 to 5
			if (arg0.getComponent().getName().compareTo("val") == 0) 
			{
				if (chgItemVal.getText().length() > 0)
				{
				    arg0.consume();
				}
				else if (arg0.getKeyChar() > '5')
				{
				    arg0.consume();
					JOptionPane.showMessageDialog(this, "Value must be >= 0 and <= 5");
					chgItemVal.requestFocus();
				}
				return;
			}
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) 
	{
	}
	
	@Override
	public void keyPressed(KeyEvent arg0)
	{
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
	}

	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		if (arg0.isPopupTrigger())
		{
			statUserIndx = grid.rowAtPoint(arg0.getPoint());
			grid.setRowSelectionInterval(statUserIndx, statUserIndx);
			((JLabel) popUp.getComponent(popUp.getComponentIndex(popupMenuName)))
				.setText("Statis menu for user " + statUserIndx);
			popUp.show(arg0.getComponent(), arg0.getX(), arg0.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) 
	{
		grid.clearSelection();
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) 
	{
		grid.clearSelection();
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// TODO Auto-generated method stub
		
	}	

	private void wipeInputField(String name, boolean requestFocus)
	{
		JTextField tf = null;
		if (name.compareTo("row") == 0)
			tf = chgItemRow;
		else if (name.compareTo("col") == 0)
			tf = chgItemCol;
		else if (name.compareTo("val") == 0)
			tf = chgItemVal;
		
		// effectively wipe the text and return the focus to the same component
		tf.setText("");
		if (requestFocus)
			tf.requestFocus();
	}
	
	private void drawGrid()
	{
		// this method is used both to re-draw after a save or to draw a new table
		// after a load. Because the newly loaded matrix could be of different size
		// compared to the previously loaded, if the application had already instantiated 
		// a grid it has to be removed from the panel before we create the new one.
		if (grid != null)
			frameCont.remove(grid);

		// Create the new grid, set the renderer for cell coloring and add to the container
		grid = new JTable(userData.getNumOfRows() + 1, userData.getNumOfColumns() + 1);
		for(int j = 0; j < userData.getNumOfColumns() + 1; j++)
		{
			grid.getColumnModel().getColumn(j).setCellRenderer(new RecommenderCellRenderer());
		}
		grid.setRowHeight(20);
		grid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		grid.addMouseListener(this);
		grid.setEnabled(false);
        frameCont.add(grid, BorderLayout.CENTER);

        // Fill grid with items. Elements of the grid will be Ratings. This class has a type
        // and a value. The coloring is decided based on the type
		Rating item = null;
		
		// Set rows and columns labels rendered in blue color
		for(int i = 1; i < userData.getNumOfRows()+ 1; i++)
		{
			item = new Rating(Rating.TYPE_LABEL, i);
			grid.setValueAt(item, i, 0);
		}
		TableColumnModel cModel = grid.getColumnModel();
		for(int i = 0; i < userData.getNumOfColumns() + 1; i++)
		{
			cModel.getColumn(i).setMinWidth(20);
			cModel.getColumn(i).setMaxWidth(20);
			cModel.getColumn(i).setPreferredWidth(20);
			if (i > 0)
			{
				item = new Rating(Rating.TYPE_LABEL, i);
				grid.setValueAt(item, 0, i);
			}
		}
		
		// Populate matrix with data. The Rating type is assigned to each item based
		// on the value source
		for(int i = 0; i < userData.getNumOfRows(); i++)
		{
			for (int j = 0; j< userData.getNumOfColumns(); j++)
			{
				if(userData.getElement(i, j) != 0)
				{
					item = new Rating(Rating.TYPE_RATING, userData.getElement(i, j));
					grid.setValueAt(item, i + 1, j + 1);
				}
				else if (predictions.getElement(i, j) != 0)
				{
					item = new Rating(Rating.TYPE_PREDICTION, predictions.getElement(i, j));					
					grid.setValueAt(item, i + 1, j + 1);
				}
			}
		}
		
	}
	
	public UserInterface(String srcPath)
	{
		this.srcPath = srcPath;
		
		setTitle("Recommender");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
	    // Creating menu bar and related menu items and subitems.
        JMenu mFile = new JMenu("File");
        mFile.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileOpen = new JMenuItem("Open");
        fileOpen.setMnemonic(KeyEvent.VK_O);
        fileOpen.setName("open");
        fileOpen.addActionListener(this);
        JMenuItem fileSave = new JMenuItem("Save");
        fileSave.setMnemonic(KeyEvent.VK_S);
        fileSave.setName("save");
        fileSave.addActionListener(this);
        JMenuItem fileSaveAs = new JMenuItem("Save As");
        fileSaveAs.setMnemonic(KeyEvent.VK_A);
        fileSaveAs.setName("saveas");
        fileSaveAs.addActionListener(this);
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.setMnemonic(KeyEvent.VK_E);
        fileExit.setName("exit");
        fileExit.addActionListener(this);
        mFile.add(fileOpen);
        mFile.add(fileSave);
        mFile.add(fileSaveAs);
        mFile.add(fileExit);
        JMenuItem mAbout = new JMenuItem("About");
        mAbout.addActionListener(this);
        JMenuItem mDummy = new JMenuItem("");
        mDummy.setEnabled(false);
        JMenuBar jmb = new JMenuBar();
        jmb.add(mFile);
        jmb.add(mAbout);
        jmb.add(mDummy);
        frameCont.add(jmb, BorderLayout.NORTH);

        popUp = new JPopupMenu();
		popUp.add(popupMenuName);
		popUp.add(new JLabel(" "));
		JMenuItem topRecom = new JMenuItem("Top 4 Recommandation");
		topRecom.setMnemonic(KeyEvent.VK_T);
		topRecom.addActionListener(this);
		popUp.add(topRecom);
		JMenuItem pearCorr = new JMenuItem("Pearson correlation");
		pearCorr.setMnemonic(KeyEvent.VK_P);
		pearCorr.addActionListener(this);
		popUp.add(pearCorr);
		popUp.addPopupMenuListener(this);
 
        JPanel subPanel = new JPanel();
        
        frameCont.add(subPanel, BorderLayout.EAST);
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        
        // Adding buttons for user action. The current class implements actionListener
        // an actionPerformed method is provided to handle the button pressed event
        subPanel.add(predBtn);
        subPanel.add(chgBtn);
        
        // Add text fields for user input
        subPanel.add(chgItemRowLab);
        subPanel.add(chgItemRow);
        subPanel.add(chgItemColLab);
        subPanel.add(chgItemCol);
        subPanel.add(chgItemValLab);
        subPanel.add(chgItemVal);

        // Add text input a name to identify which control is sending the event in 
        // the even listener above
        chgItemRow.setName("row");
        chgItemCol.setName("col");
        chgItemVal.setName("val");

        // Add key and focus listeners to input text fields to control user input 
        chgItemRow.addKeyListener(this);
		chgItemCol.addKeyListener(this);
		chgItemVal.addKeyListener(this);

		chgItemRow.addFocusListener(this);
		chgItemCol.addFocusListener(this);
		chgItemVal.addFocusListener(this);
		       
        predBtn.addActionListener(this);
		chgBtn.addActionListener(this);

		// disable action and input components by default.
		// they will be enable once the first file is loaded
        chgBtn.setEnabled(false);
        predBtn.setEnabled(false);
        chgItemCol.setEnabled(false);
        chgItemRow.setEnabled(false);
        chgItemVal.setEnabled(false);
        
        // All objects having actionPerformed event associated are listed in an 
        // array in order to determine which is the source of the current handled action
        // in the actionPerformed method above
        sourceObj[FILEOPEN] = fileOpen;
        sourceObj[FILESAVE] = fileSave;
        sourceObj[FILESAVEAS] = fileSaveAs;
        sourceObj[FILEEXIT] = fileExit;
        sourceObj[ABOUT] = mAbout;
        sourceObj[CHGBTN] = chgBtn;
        sourceObj[PREDBTN] = predBtn;
        sourceObj[TOPRECON] = topRecom;
        sourceObj[PEARCORR] = pearCorr;

        setSize(20 * (Matrix.MAX_COLUMNS + 1) + 180, 20 * (Matrix.MAX_ROWS + 1));
        setMinimumSize(new Dimension(200,  135));
		// grid.editCellAt(3, 5);
	}
}
