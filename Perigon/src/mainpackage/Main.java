package mainpackage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import java.io.File;
 
public class Main {
	private static final int PANEL_W = 1500;
    private static final int PANEL_H = 900;
	public Main() {
		JFrame f = new JFrame("Perigon");
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JFrame.setDefaultLookAndFeelDecorated(true);
	    f.add(new MainPanel());
	    f.pack();
	    f.setLocationRelativeTo(null);
	    f.setVisible(true);
	    
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main();
        });
    }
}

class MainPanel extends JPanel{
    /**
	 * 
	 */
	private static int PANEL_W = 1500;
    private static final int PANEL_H = 900;
	private static final long serialVersionUID = 1L;
	private DrawPanel drawPanel = new DrawPanel(PANEL_W,PANEL_H);
    String sampleTitle = "Sample Data";

    public MainPanel() {
    	JPanel textPanel = new JPanel(new BorderLayout());;
    	JLabel jtl = new JLabel(sampleTitle, SwingConstants.CENTER);
    	ScatterData data = new ScatterData();
    	jtl.setFont(new Font ("Ariel", Font.BOLD, 48));
    	jtl.setForeground(Color.white);
    	textPanel.add(jtl);
    	textPanel.setBackground(new Color(100,0,0));
 
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(buttonPanel, BorderLayout.PAGE_START);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(100, 10, 0, 10));
        rightPanel.setBackground(Color.black);
        
        JButton loadButton = new JButton("Load Sample Data");
        buttonPanel.add(loadButton);
        loadButton.addActionListener(new ActionListener()
        {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("LAS","las","las");
                chooser.setFileFilter(filter);
        		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = chooser.showOpenDialog(loadButton);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // user selects a file
                }
                File selectedFile = chooser.getSelectedFile();
                 
                data.parseData(selectedFile);
                jtl.setText(selectedFile.getName() + " (" + data.getDate() + ")");
                drawPanel.repaint();
        	}
        });
        
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(clearButton);
        clearButton.addActionListener(new ActionListener()
        {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		data.resetCurves();
                jtl.setText(sampleTitle);
                drawPanel.repaint();
        	}
        });
        
        setLayout(new BorderLayout());
        add(textPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.WEST);
    }
}

class DrawPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private static final int graphOffset = 60;
    private double xScale = 1.5;   
    private int yScale = 1000;
    private static int width=0;
    private static int height=0;
    private static final int xOffset = 0;
    private static final int yOffset = 6000;
    
    ScatterData data = new ScatterData();

    public DrawPanel(int width, int height) {
    	this.width = width;
    	this.height = height;
    }

    @Override
    public Dimension getPreferredSize() { 
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();            
        } else {
            return new Dimension(width, height);
        }    
    }
    
    @Override
    protected void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        g2.setColor(Color.white);
        g2.fillRect(graphOffset,0, this.getWidth(), this.getHeight()-graphOffset);
        
        // graph lines
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
        g2.setStroke(dashed);
        g2.setColor(Color.black);
        
        // horizontal lines
        for(int i=0;i<10;i++) {
        	int lineHeight = (int) ((i+6)*yScale-yOffset)/10;
        	g2.drawLine(graphOffset, lineHeight-graphOffset, this.getWidth(), lineHeight-graphOffset);
        	g2.drawString(String.valueOf(6.0+((double)(9-i)/10)), graphOffset-30, lineHeight-graphOffset);
        }
        
        // vertical lines
        for(int i=0;i<=9;i++) {
        	int lineWidth = (int) (i*100*xScale-xOffset+graphOffset);
        	g2.drawLine(lineWidth, this.getHeight()-graphOffset, lineWidth, 0);
        	g2.drawString(Integer.toString(i*100), lineWidth, this.getHeight()-graphOffset+30);
        }
        
        double x; 
        double y;

        for(String cur : data.getCurves()) {
        	String[] parts = cur.split(",");
        	x = Double.parseDouble(parts[0]);
        	y = Double.parseDouble(parts[1]);
        	
        	int pointX = (int) (x*xScale+xOffset+graphOffset);
        	int pointY = (int) (this.getHeight()-(y*yScale)+yOffset-graphOffset);
        	
        	float alpha = 0.6f;
        	g2.setComposite(makeComposite(alpha));
        	g2.setPaint(Color.red);
        	g2.fillOval((pointX)-10,(pointY),20,20);
        }
        
    }
    
    private AlphaComposite makeComposite(float alpha) {
    	  int type = AlphaComposite.SRC_OVER;
    	  return(AlphaComposite.getInstance(type, alpha));
    	 }
}