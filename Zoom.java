import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;


public class Zoom implements ActionListener {


   private JMenuBar menuBar;
   private JMenu tools;
   private JMenuItem drawingColor, bgColor, clear;
   private JCheckBoxMenuItem zoomMode;
   private JFrame window;
   private JPanel container;
   private ZoomPanel panel;
   private BufferedImage normalImage, zoomedImage, subimage;
   private boolean zoom;
   private int zoomX, zoomY;
   private int prevX, prevY, newX, newY;
   private Graphics gNormal, gZoomed, gSubimage;
   private Color currentColor;
   private boolean dragging;

   public Zoom() {

      menuBar = new JMenuBar();
      tools = new JMenu("Tools");
      drawingColor = new JMenuItem("Drawing Color");
      bgColor = new JMenuItem("Background Color");
      zoomMode = new JCheckBoxMenuItem("Zoom Mode");
      clear = new JMenuItem("Clear");
      window = new JFrame("Zoom");
      container = new MainPanel();
      panel = new ZoomPanel();
      container.add(panel);
      panel.setBounds(0,0,400,400);
      zoom = false;
      currentColor = Color.BLACK;
      menuBar.add(tools);
      tools.add(drawingColor);
      tools.add(zoomMode);
      tools.add(clear);
      tools.add(bgColor);
      zoomMode.addActionListener(this);
      drawingColor.addActionListener(this);
      bgColor.addActionListener(this);
      clear.addActionListener(this);
      window.setContentPane(container);
      window.setJMenuBar(menuBar);
      window.setSize(406,551);
      window.setLocation(100,100);
      window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      window.setResizable(false);
      window.setVisible(true);

   }

   private class MainPanel extends JPanel {

      public MainPanel() {

         super();
         setLayout(null);
         setBackground(Color.GRAY);

      }

      public void paintComponent(Graphics g) {

         super.paintComponent(g);
         if (zoom && subimage != null) g.drawImage(subimage,180,430,null);

      }

   }

   private class ZoomPanel extends JPanel implements MouseListener, MouseMotionListener {

      public ZoomPanel() {

         super();
         setBackground(Color.WHITE);
         addMouseListener(this);
         addMouseMotionListener(this);
         dragging = false;
         normalImage = new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
         gNormal = normalImage.getGraphics();
         gNormal.setColor(Color.WHITE);
         gNormal.fillRect(0,0,400,400);
         gNormal.dispose();

      }

      public void paintComponent(Graphics g) {

         super.paintComponent(g);

         if (zoomMode.isSelected()) {

            if (!zoom) {

               g.drawImage(normalImage,0,0,null);
               g.setColor(Color.RED);
               g.drawRect(zoomX-20,zoomY-20,40,40);

            }

            else g.drawImage(zoomedImage,0,0,null);

         }

         else g.drawImage(normalImage,0,0,null);

      }

      public void mousePressed(MouseEvent e) {

         if (zoomMode.isSelected()) {

            if (!zoom) {

               zoom = true;
               gZoomed = zoomedImage.getGraphics();
               subimage = normalImage.getSubimage(zoomX-20,zoomY-20,40,40);
               updateZoomedImage();
               drawGrid();

            }

            else {

               gZoomed = zoomedImage.getGraphics();
               gZoomed.setColor(currentColor);
               gSubimage = subimage.getGraphics();
               gSubimage.setColor(currentColor);
               prevX = e.getX();
               prevY = e.getY();
               dragging = true;

            }

         }

         else {

            gNormal = normalImage.getGraphics();
            gNormal.setColor(currentColor);
            prevX = e.getX();
            prevY = e.getY();
            dragging = true;

         }

         repaint();
         container.repaint(0,400,400,100);

      }

      public void mouseReleased(MouseEvent e) {

         if (gNormal != null) gNormal.dispose();
         if (gZoomed != null) gZoomed.dispose();
         if (gSubimage != null) gSubimage.dispose();
         dragging = false;
         repaint();
         container.repaint(0,400,400,100);

      }

      public void mouseClicked(MouseEvent e) {}
      public void mouseEntered(MouseEvent e) {}
      public void mouseExited(MouseEvent e) {}

      public void mouseMoved(MouseEvent e) {

         if (zoomMode.isSelected() && !zoom) {

            if (e.getX() > 20 && e.getX() < 380) zoomX = e.getX();
            if (e.getX() <= 20) zoomX = 20;
            if (e.getX() >= 380) zoomX = 380;
            if (e.getY() > 20 && e.getY() < 380) zoomY = e.getY();
            if (e.getY() <= 20) zoomY = 20;
            if (e.getY() >= 380) zoomY = 380;
            repaint();

         }

      }

      public void mouseDragged(MouseEvent e) {

         if (dragging) {

            if (!zoomMode.isSelected()) gNormal.drawLine(prevX,prevY,e.getX(),e.getY());

            else {

               prevX = prevX/10;
               prevY = prevY/10;
               newX = e.getX()/10;
               newY = e.getY()/10;

               if (prevX != newX || prevY != newY) {

                  gSubimage.drawLine(prevX,prevY,newX,newY);
                  updateZoomedImage();
                  drawGrid();

               }

            }

            prevX = e.getX();
            prevY = e.getY();

         }

         repaint();
         container.repaint(0,400,400,100);

      }

   }

   public void actionPerformed(ActionEvent e) {

      Object source = e.getSource();

      if (source == drawingColor) {

         Color color = JColorChooser.showDialog(panel,"Drawing Color",currentColor);
         if (color != null) currentColor = color;

      }

      if (source == bgColor) {

         Color color = JColorChooser.showDialog(panel,"Background Color",panel.getBackground());
         if (color != null) panel.setBackground(color);

      }

      if (source == zoomMode) {

         if (zoomMode.isSelected()) {

            zoomedImage = new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
            gZoomed = zoomedImage.getGraphics();
            gZoomed.setColor(Color.WHITE);
            gZoomed.fillRect(0,0,400,400);
            gZoomed.dispose();

         }

         else {

            gNormal = normalImage.getGraphics();
            gNormal.drawImage(subimage,zoomX-20,zoomY-20,null);
            gNormal.dispose();
            zoom = false;

         }

      }

      if (source == clear) {

         if (zoomMode.isSelected()) zoomMode.doClick();
         normalImage = new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
         gNormal = normalImage.getGraphics();
         gNormal.setColor(Color.WHITE);
         gNormal.fillRect(0,0,400,400);
         gNormal.dispose();

      }

      panel.repaint();
      container.repaint(0,400,400,100);

   }

   public void drawGrid() {

      gZoomed.setColor(Color.LIGHT_GRAY);

      for (int i = 0; i < 400; i += 10) {

         gZoomed.drawLine(i,0,i,400);
         gZoomed.drawLine(0,i,400,i);

      }

   }

   public void updateZoomedImage() {

      for (int i = 0; i < 40; i++) {

         for (int j = 0; j < 40; j++) {

            int rgb = subimage.getRGB(i,j);
            gZoomed.setColor(new Color(rgb));
            gZoomed.fillRect(10*i,10*j,10,10);

         }

      }

   }

   public static void main(String[] args) {

      new Zoom();

   }


}