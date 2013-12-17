package Bluetooth;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * This class creates a frame that represents a console. There's an input field
 * with support for history navigation. I.e. arrow up and arrow down allow to go
 * to previous entries to the console.
 * 
 * Normal flow of usage: create object, add listener for input, enable input
 * field and use {@link ConsoleFrame#println(String)} to display stuff on
 * screen.
 * 
 * @author Victor
 */
public class ConsoleFrame extends JFrame {

	private static final long serialVersionUID = -4298357970493681249L;

	private ArrayList<InputListener> listeners;
	private ArrayList<String> history;
	
	private JScrollPane scrollPane;
	private JPanel contentPane;
	private JTextField inputField;
	private JTextArea outputArea;

	/**
	 * Create a frame.
	 */
	public ConsoleFrame() {
		listeners = new ArrayList<InputListener>();
		history = new ArrayList<String>();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setResizable(false);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		inputField = new JTextField();
		inputField.setEditable(false);
		inputField.setEnabled(false);
		sl_contentPane.putConstraint(SpringLayout.WEST, inputField, 0, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, inputField, 0, SpringLayout.EAST, contentPane);
		inputField.addKeyListener(new KeyAdapter() {
			private int historyPosition = 0;
			
			@Override
			public void keyPressed(KeyEvent event) {
				final JTextField source = (JTextField) event.getSource();
				
				if (event.getKeyCode() == KeyEvent.VK_ENTER) {
					final String text = source.getText();
					
					if (text.equals("")) return;

					history.add(text);
					historyPosition = ConsoleFrame.this.history.size();
					
					((JTextField) event.getSource()).setText("");
					
					for (final InputListener listener : listeners) {
						// Start this on a different thread to make sure it doesn't block the Swing thread
						(new Thread() {
							@Override
							public void run() {
								listener.handleInput(text);
							}
						}).start();
					}
				} else if (event.getKeyCode() == KeyEvent.VK_UP) {
					if (historyPosition == 0) return;
					
					historyPosition--;
					
					source.setText(ConsoleFrame.this.history.get(historyPosition));
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							source.setCaretPosition(source.getText().length());
						}
					});
				} else if (event.getKeyCode() == KeyEvent.VK_DOWN) {
					if (historyPosition == ConsoleFrame.this.history.size()) return;
					
					if (historyPosition == ConsoleFrame.this.history.size() - 1) {
						source.setText("");
						historyPosition++;	// historyPosition is now history.size() in value
						return;
					}
					
					historyPosition++;
					
					source.setText(ConsoleFrame.this.history.get(historyPosition));
				} else {	// Any other key should reset historyPosition
					historyPosition = ConsoleFrame.this.history.size();
				}
			}
		});
		sl_contentPane.putConstraint(SpringLayout.SOUTH, inputField, 0, SpringLayout.SOUTH, contentPane);
		contentPane.add(inputField);
		inputField.setColumns(10);
		
		outputArea = new JTextArea();
		outputArea.setEditable(false);
		
		scrollPane = new JScrollPane(outputArea);
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 5, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -5, SpringLayout.NORTH, inputField);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane);
		
		outputArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				if (event.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu menu = new JPopupMenu();
					JMenuItem clearItem = new JMenuItem();
					
					clearItem.setText("Clear");
					clearItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							ConsoleFrame.this.outputArea.setText("");
						}
						
					});
					
					menu.add(clearItem);
					menu.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});
	}
	
	/**
	 * Enables the input field for the console
	 */
	public void enableInputField() {
		inputField.setEditable(true);
		inputField.setEnabled(true);
		inputField.requestFocusInWindow();
	}
	
	/**
	 * Add an object that works as listener for every line that's typed into the
	 * console.
	 * 
	 * @param listener
	 *            Listener object
	 */
	public void addInputListener(InputListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove an InputListener from the listeners.
	 * 
	 * @param listener
	 *            Listener to be removed.
	 */
	public void removeInputListener(InputListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Display a line in the output area of the console.
	 * 
	 * @param line
	 *            String to be output
	 */
	public void println(String line) {
		outputArea.append(line.trim() + '\n');
		
		// Smart scrolling
		JScrollBar bar = scrollPane.getVerticalScrollBar();
		// -10 is because sometimes the equation isn't exact
		if (bar.getValue() >= bar.getMaximum() - bar.getVisibleAmount() - 20)
			outputArea.setCaretPosition(outputArea.getDocument().getLength());
	}
	
	/**
	 * Display an error in the output area of the console.
	 * 
	 * @param line
	 *            Error to be printed
	 */
	public void err(String line) {
		println("ERROR: " + line);
	}
	
	/**
	 * Interface for classes that work as listener for this console.
	 */
	public interface InputListener {
		public void handleInput(String input);
		
		/**
		 * Gets called when the ConsoleFrame is disposed, to properly shut down
		 * the stuff that's running behind the frame.
		 */
		public void dispose();
	}
	
	@Override
	public void dispose() {
		// Do nothing
	}
}
