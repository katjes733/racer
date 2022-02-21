package com.katjes.racer;
/**
 * @(#)racer.java
 * my first racing game with Java3D
 *
 *
 * @Martin Macecek 
 * @version 1.00 2007/6/25
 */
// basic java classes
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Racer extends JFrame implements ActionListener{
	
    private JButton visitButton; 
    private boolean fullscreen;  //fullscreen
    	 
    /**
     * Creates a new instance of <code>racer</code>.
     *
     */
    public Racer(String[] args) {
    	//text in application bar, visible if not in fullscreen
    	super("Racer v1.0");
    	Container c = getContentPane(); 
    	c.setLayout(new BorderLayout()); //sets layout of Container
    	
    	if (args.length > 0 && args[0].equals("-window") && hasJ3D())  // go to windowmode
			fullscreen = false;
		else if (hasJ3D())  // go to fullscreen
			fullscreen = true; // no menubar, borders
		else
			//System.out.println("some kind of error occured, please contact your programming specialist");
			reportProb(c);
		
		fullscreen = false;
		
		setUndecorated(fullscreen);	//sets window/fullscreenmode
		WrapRacer w3d = new WrapRacer(this,fullscreen); //new instance of WrapRacer, the main 3D Object
		c.add(w3d,BorderLayout.CENTER);	//adds new WrapRacer Object to container	

		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); //clicking on x closes window
		pack();
		setResizable(false); // fixed size display
		// center this window
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize(); // get actual screen resolution
		Dimension winDim = this.getSize(); //get actual application window size
		//positions application window in center of screen
		this.setLocation( (screenDim.width-winDim.width)/2,(screenDim.height-winDim.height)/2); 		
		setVisible(true); //make it visible
		
    	
    }
    
    private boolean hasJ3D()
  	// check if Java 3D is available
  	{
    	try {   // test for an essential Java 3D class
      		Class.forName("com.sun.j3d.utils.universe.SimpleUniverse");
      		return true;
    	}
    	catch(ClassNotFoundException e) {
      		System.err.println("Java 3D not installed");
      		return false;
    	}
  	} // end of hasJ3D()


  	private void reportProb(Container c)
  	/* Report the absence of Java 3D.
     	Use a label, and a non-active button to show the Java 3D URL.
     	(I'll make the URL active in a later version of this appl.)
  	*/
  	{
    	JPanel reportPanel = new JPanel();
    	reportPanel.setLayout( new BoxLayout(reportPanel, BoxLayout.Y_AXIS));  // vertical
    	c.add(reportPanel, BorderLayout.CENTER);

    	String msgText = "<html><font size=+2>" +
                     "Java 3D <font color=red>not</font> installed" +
                     "</font></html>";
    	JLabel msgLabel = new JLabel(msgText, SwingConstants.CENTER);
    	reportPanel.add(msgLabel);

    	String visitText = "<html><font size=+2>" +
                       "Visit https://java3d.dev.java.net/" +
                       "</font></html>";
    	visitButton = new JButton(visitText);
    	visitButton.addActionListener(this);
    	reportPanel.add(visitButton);

  	}  // end of reportProb()


  	public void actionPerformed(ActionEvent e)
  	{
    	if (e.getSource() == visitButton)
      	System.out.println("Visit https://java3d.dev.java.net/");
  	}  // end of actionPerformed
    
    public static void main(String[] args) {
        // new instance of racer will be created
        new Racer(args);
    }
}
