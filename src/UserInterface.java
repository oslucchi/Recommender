

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.sql.rowset.Predicate;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.uncommons.swing.SpringUtilities;

public class UserInterface extends JFrame 
						   implements KeyListener, FocusListener, ActionListener, WindowStateListener
{
    public class MyTableCellRenderer extends DefaultTableCellRenderer 
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
				s = String.valueOf(valueAt.getValue());
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
    Matrix data;
    Matrix pred;
    
    private Object[] sourceObj = new Object[6];
    
	int numRows = 0;
	int numCols = 0;
    JTextField chgItemRow;
    JTextField chgItemCol;
    JTextField chgItemVal = new JTextField("", 1);
    JLabel chgItemRowLab = new JLabel("row");
    JLabel chgItemColLab = new JLabel("col");
    JLabel chgItemValLab = new JLabel("val");
	JTable grid = null;
	Container frameCont = this.getContentPane();
	Toolkit tk = Toolkit.getDefaultToolkit();

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
		int i;
		for(i = 0; i < sourceObj.length; i++)
		{
			if (e.getSource().equals(sourceObj[i]))
				break;
		}
		switch(i)
		{
		case FILEOPEN:
			JFileChooser fc = new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
				    "Recommender dat files", "dat");
			fc.setFileFilter(filter);
			if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				System.out.println(fc.getSelectedFile().getAbsolutePath());
				data = new Matrix(fc.getSelectedFile().getAbsolutePath());
				if (!data.loadFile())
					JOptionPane.showMessageDialog(this, data.getError());
				pred = new Matrix(null);
				pred.setElement(0, 1, 3);
				drawGrid();
			}
			break;
			
		case FILESAVE:
			break;
			
		case FILESAVEAS:
			break;
			
		case FILEEXIT:
			System.out.println("command exit");
			System.exit(0);
			break;
			
		case ABOUT:
			break;
			
		case CHGBTN:
			grid.setValueAt(chgItemVal.getText(), Integer.valueOf(chgItemRow.getText()), Integer.valueOf(chgItemCol.getText()));
			break;
			
		default:
			
			for(i = 0; i < data.getNumOfRows(); i++)
			{
				for (int j = 0; j< data.getNumOfColumns(); j++)
				{
					if (data.getElement(i,  j) == 0)
					{
						pred.setElement(i, j, data.prediction(i,  j));
					}
					else
					{
						pred.setElement(i, j, 0);	
					}
				}
			}

		}	
	}

	@Override
	public void focusLost(FocusEvent arg0) 
	{
		// TODO Auto-generated method stub
		String compName = arg0.getComponent().getName();
		System.out.println(compName + " lost foucs");
		try
		{
			if (compName.compareTo("row") == 0)
			{
				int rows = Integer.parseInt(chgItemRow.getText()); 
				if ((rows < 1) || (rows > numRows))
				{
					JOptionPane.showMessageDialog(this, "Value must be between 1 and " + numRows);
					wipeInputField("row", true);
				}
			}
			else if (compName.compareTo("col") == 0)
			{
				int cols = Integer.parseInt(chgItemCol.getText()); 
				if ((cols < 1) || (cols > numCols))
				{
					JOptionPane.showMessageDialog(this, "Value must be between 1 and " + numCols);
					wipeInputField("col", true);
				}
			}
		}
		catch(NumberFormatException e)
		{
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
		System.out.println("Component '" + arg0.getComponent().getName() + "' char '" + arg0.getKeyChar() + "' code " + arg0.getKeyCode());
		
		if ((arg0.getKeyChar() != KeyEvent.VK_DELETE) &&
			(arg0.getKeyChar() != KeyEvent.VK_BACK_SPACE))
		{
			if ((arg0.getKeyChar() < '0') || (arg0.getKeyChar() > '9'))
			{
			    tk.beep();
			    arg0.consume();
				return;
			}
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
			System.out.println("typed '" + arg0.getKeyChar() + "'");
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyPressed(KeyEvent arg0)
	{
	}

	private void wipeInputField(String name, boolean requestFocus)
	{
		System.out.println("Wiping " + name);
		JTextField tf = null;
		if (name.compareTo("row") == 0)
			tf = chgItemRow;
		else if (name.compareTo("col") == 0)
			tf = chgItemCol;
		else if (name.compareTo("val") == 0)
			tf = chgItemVal;
		tf.setText("");
		if (requestFocus)
			tf.requestFocus();
	}
	
	private void drawGrid()
	{
		if (grid != null)
			frameCont.remove(grid);
		grid = new JTable(data.getNumOfRows() + 1, data.getNumOfColumns() + 1);
		for(int j = 0; j < data.getNumOfColumns() + 1; j++)
		{
			grid.getColumnModel().getColumn(j).setCellRenderer(new MyTableCellRenderer());
		}
		grid.setRowHeight(20);
		grid.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        frameCont.add(grid, BorderLayout.CENTER);

		Rating item = null;
		for(int i = 1; i < data.getNumOfRows()+ 1; i++)
		{
			item = new Rating(Rating.TYPE_LABEL, i);
			grid.setValueAt(item, i, 0);
		}
		TableColumnModel cModel = grid.getColumnModel();
		for(int i = 0; i < data.getNumOfColumns() + 1; i++)
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
		for(int i = 0; i < data.getNumOfRows(); i++)
		{
			for (int j = 0; j< data.getNumOfColumns(); j++)
			{
				if(data.getElement(i,j) != 0)
				{
					// grid.setValueAt(data.getElement(i, j), i, j);
					item = new Rating(Rating.TYPE_RATING, data.getElement(i, j));
					grid.setValueAt(item, i + 1, j + 1);
				}
				else if (pred.getElement(i, j) != 0)
				{
					// grid.setValueAt(pred.getElement(i, j), i, j);
					item = new Rating(Rating.TYPE_PREDICTION, pred.getElement(i, j));					
					grid.setValueAt(item, i + 1, j + 1);
				}
			}
		}
		
	}
	
	public UserInterface()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
		numCols = Matrix.MAX_COLUMNS;
		numRows = Matrix.MAX_ROWS;
	    chgItemRow = new JTextField("", (this.numRows < 10 ? 1 : 2));
	    chgItemCol = new JTextField("", (this.numCols < 10 ? 1 : 2));

        JMenuBar jmb = new JMenuBar();
        JMenu mFile = new JMenu("File");
        mFile.setMnemonic(KeyEvent.VK_F);
        jmb.add(mFile);
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
        JMenu mAbout = new JMenu("About");
        jmb.add(mAbout);
        frameCont.add(jmb, BorderLayout.NORTH);
       // JPanel chgPanel = new JPanel();//(new SpringLayout());
       // chgPanel.add(chgItemRowLab);
       // chgPanel.add(chgItemRow);
       // chgPanel.add(chgItemColLab);
       // chgPanel.add(chgItemCol);
       // chgPanel.add(chgItemValLab);
       // chgPanel.add(chgItemVal);
        
        
        //SpringUtilities.makeCompactGrid(chgPanel, 3, 2, 5, 5, 5, 5);
 
		chgItemRow.addKeyListener(this);
		chgItemCol.addKeyListener(this);
		chgItemVal.addKeyListener(this);

		chgItemRow.addFocusListener(this);
		chgItemCol.addFocusListener(this);
		chgItemVal.addFocusListener(this);
		
        //frameCont.add(chgPanel);
        
        JPanel subPanel = new JPanel();
        //frameCont.add(subPanel, BorderLayout.EAST);
        
        // cambiamenti fatti da noi
        // vedi se va bene :D
        
        frameCont.add(subPanel, BorderLayout.EAST);
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        JButton predBtn = new JButton("Predict");
        predBtn.addActionListener(this);// qui manca l'inserimento della prediction, serve un modo
        								// per trovare la posizione sulla matrice dove l'utente ha cliccato
        								// e l'inserimento della prediction nella casella cliccata dall'utente
        subPanel.add(predBtn);
        JButton chgBtn = new JButton("Change");
		chgBtn.addActionListener(this);
        subPanel.add(chgBtn);
        subPanel.add(chgBtn);
        chgItemRow.setName("row");
        subPanel.add(chgItemRowLab);
        subPanel.add(chgItemRow);
        subPanel.add(chgItemRow);
        chgItemCol.setName("col");
        subPanel.add(chgItemColLab);
        subPanel.add(chgItemCol);
        subPanel.add(chgItemCol);
        chgItemVal.setName("val");
        subPanel.add(chgItemValLab);
        subPanel.add(chgItemVal);
        subPanel.add(chgItemVal);
        
        
        sourceObj[0] = fileOpen;
        sourceObj[1] = fileSave;
        sourceObj[2] = fileSaveAs;
        sourceObj[3] = fileExit;
        sourceObj[4] = mAbout;
        sourceObj[5] = chgBtn;

        setSize(20 * (this.numCols + 1) + 180, 20 * (this.numRows + 3));
        setMinimumSize(new Dimension(200,  135));
		// grid.editCellAt(3, 5);
	}

}
