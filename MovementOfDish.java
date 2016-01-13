/*This program is used to identify the error of misalignment in polar beam 
 * Auther: Tom Liu
 * 
 * Aug 6th
 * 
 * 
 * 
 */ 


//import library
import java.math.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.WindowAdapter;
import javax.swing.*;
import java.io.*;




public class MovementOfDish extends Applet implements ActionListener, KeyListener{

  //vairables
    double polarAngle=48;
    double tilt=42;
    double R=8;
    double idealNS;
    double idealEW;
    double beamPivot;
    double rotation=90;
    double S;
    double idealHeight;    
    double beam=10;
    double errorPA;
    double errorRA;
    final double REDUCE=0.06;
    
  //used to write txt file
    FileWriter output;
    BufferedWriter out;
    
  // polar beam vairable 
    
    double beam1X=0;
    double beam1X2=-1*Math.cos(Math.toRadians(polarAngle))*beam; //The coodinates of the ideal polar beam, and they are fixed,  
   
    double beam1Y=0;
    double beam1Y2=Math.sin(Math.toRadians(polarAngle))*beam; 
    
    double beam1Z=0;
    double beam1Z2=0;    

    double errorBeam1X2;
    double errorBeam1Y2;
    double errorBeam1Z2;
   

    
    //GUI vairables
    Timer timer;
    JButton go = new JButton("Go");
    JButton pause= new JButton("Pause");
    TransformGroup objTrans;
    JTextField tiltInput;
    JTextField errorPolarAngle ;
    JTextField errorRotation;
    
    JTextField distanceNS ;
    JTextField distanceEW ;
    JTextField distanceHeight ;
    JTextField distanceTotal;

        //java 3D vairable 
    BranchGroup objRoot;
    Vector3f tempVec = new Vector3f();
    Vector3f crossVec = new Vector3f();
    final Vector3f YAXIS = new Vector3f(0, 1, 0); //y axis vector
    
    Transform3D beam2RY = new Transform3D();
    
    Transform3D beam2Location = new Transform3D();
    

    AxisAngle4f beam2AY = new AxisAngle4f();


    TransformGroup beam2Trans=new TransformGroup();
    
    Transform3D errorBeam2RY = new Transform3D();
    
    Transform3D errorBeam2Location = new Transform3D();
    

    AxisAngle4f errorBeam2AY = new AxisAngle4f();


    TransformGroup errorBeam2Trans=new TransformGroup();    
    
    
    
    
    
   public BranchGroup createSceneGraph() { //graph initail function

      
     objRoot = new BranchGroup(); //branch group to contain 3d Graph 
     objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND); 

// ideal dish movement initial postion set up 
// draw polar beam (beam1) part
   tempVec.set((float)beam1X2, (float)beam1Y2,(float) beam1Z2);
   
   // Find axis of rotation

   tempVec.normalize();
   // Find the angle between Y axis and polar beam
   crossVec.cross(YAXIS, tempVec);
   // Find amount of rotation and put into matrix
   AxisAngle4f tempAY = new AxisAngle4f();
   tempAY.set(crossVec, (float)Math.acos(YAXIS.dot(tempVec)));
   
   Transform3D tempRotation = new Transform3D();
   tempRotation.set(tempAY);   

   // Transform to midpoint between two nodes
   Transform3D tempLocation = new Transform3D();
   tempLocation.setIdentity();
   tempLocation.setTranslation(new Vector3f((float)(REDUCE*beam1X2/2), (float)(REDUCE*beam1Y2/2), (float)(REDUCE*beam1Z2/2)));
   tempLocation.mul(tempRotation);    
   
   TransformGroup beam1Trans = new TransformGroup();  
   beam1Trans.setTransform(tempLocation);
   Cylinder beam1 = new Cylinder(0.005f,(float)(REDUCE*beam));
   beam1Trans.addChild(beam1);
   
   objRoot.addChild(beam1Trans);

// rotation beam set up
   
   // get the coodinates of the rotation beam
   double beam2X=beam1X2;
   double beam2X2=beam2X+getNS( polarAngle, tilt,R,rotation);
   
   double beam2Y=beam1Y2;
   double beam2Y2=beam2Y+getHeight( polarAngle, tilt,R,rotation);
   
   double beam2Z=beam1Z2;
   double beam2Z2=-(beam2Z+getEW( polarAngle, tilt,R,rotation));
   
   double dx=beam2X2-beam2X;
   double dy=beam2Y2-beam2Y;
   double dz=beam2Z2-beam2Z;
   
   //draw 3D graph part of ideal rotation beam
   
   tempVec=new Vector3f();
   tempVec.set((float) dx, (float) dy,(float) dz);
   
   tempVec.normalize();
   
   crossVec=new Vector3f();    
   crossVec.cross(YAXIS, tempVec); 
   
   beam2AY.set(crossVec, (float)Math.acos(YAXIS.dot(tempVec)));
   beam2RY.set(beam2AY);
   beam2Location.setIdentity();
   beam2Location.setTranslation(new Vector3f((float)(REDUCE*(beam2X+beam2X2)*0.5), (float)(REDUCE*(beam2Y+beam2Y2)*0.5), (float)(REDUCE*(beam2Z+beam2Z2)*0.5)));
   
   beam2Location.mul(beam2RY);
   
   beam2Trans = new TransformGroup();
   beam2Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
   beam2Trans.setTransform(beam2Location);
   
   Cylinder beam2 = new Cylinder(0.005f,(float)(REDUCE*R));
   
   beam2Trans.addChild(beam2);
   

   
   objRoot.addChild(beam2Trans); 
   

   
   
   

   
// light   
   
   BoundingSphere bounds =   new BoundingSphere(new Point3d(0.0,0.0,0.0), 100.0);

   Color3f light1Color = new Color3f(1.0f, 1.4f, 0.2f);

   Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);

   DirectionalLight light1= new DirectionalLight(light1Color, light1Direction);

   light1.setInfluencingBounds(bounds);

   objRoot.addChild(light1); 
   
   // Set up the ambient light

   Color3f ambientColor = new Color3f(1.0f, 1.0f, 1.0f);

   AmbientLight ambientLightNode = new AmbientLight(ambientColor);

   ambientLightNode.setInfluencingBounds(bounds);

   objRoot.addChild(ambientLightNode);




      
      
  
      return objRoot;
    }

   //constructor of MovementOfDish , and it will be called aotumatically when create MovementOfDish Object
    public MovementOfDish(){
      //layout of GUI
      setLayout(new BorderLayout());

      GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
      
      //3D Canvas
      Canvas3D c = new Canvas3D(config);

      add("Center", c);

      c.addKeyListener(this);

      timer = new Timer(200,this);

      Panel p =new Panel();
      
      p.setLayout(new GridLayout(4,10,5,5));
      
      //add listener to the button 
      
      go.addActionListener(this);
      go.addKeyListener(this);
      pause.addActionListener(this);
      pause.addKeyListener(this);
      
      //add labels
      
      JLabel l1=new JLabel("Tilt");
      JLabel l2=new JLabel("Error of Polar Angle");
      JLabel l3=new JLabel("Error of Rotation Angle");
      JLabel l4=new JLabel("Error of NS");
      JLabel l5=new JLabel("Error of EW");
      JLabel l6=new JLabel("Error of Height");
      JLabel l7=new JLabel("Total Error");
      tiltInput= new JTextField(5);
      errorPolarAngle = new JTextField(5);
      errorRotation = new JTextField(5);

      
      //add listeners on textfield
      tiltInput.addActionListener(this);
      errorPolarAngle.addActionListener(this);
      errorRotation.addActionListener(this);
      
      //uneditablet ext fields
      distanceNS = new JTextField("", 5);
      distanceNS.setEditable(false);
      distanceEW = new JTextField("", 5);
      distanceEW.setEditable(false);
      distanceHeight = new JTextField("", 5);
      distanceHeight.setEditable(false);      
      distanceTotal = new JTextField("", 5);
      distanceTotal.setEditable(false);
      
      distanceNS.addActionListener(this);
      distanceEW.addActionListener(this);
      distanceHeight.addActionListener(this);
      distanceTotal.addActionListener(this);
      
      
      p.add(l1);
      p.add(tiltInput);
      p.add(l2);
      p.add(errorPolarAngle);
      p.add(l3);
      p.add(errorRotation);
      p.add(l4);
      p.add(distanceNS);
      p.add(l5);
      p.add(distanceEW);
      p.add(l6);
      p.add(distanceHeight);
      p.add(l7);
      p.add(distanceTotal);
      p.add(go);
      p.add(pause);

      add("North",p);
      
      
   // Create a simple scene and attach it to the virtual universe

      BranchGroup scene = createSceneGraph();

      SimpleUniverse u = new SimpleUniverse(c);

      u.getViewingPlatform().setNominalViewingTransform();
   
      u.addBranchGraph(scene);

      
      
      

  
    }

  
  
  
  
  
  //some functions should be extended from parent class
  public void keyPressed(KeyEvent e) {


  }

  public void keyReleased(KeyEvent e){

  }

  public void keyTyped(KeyEvent e){


  }

  
  
  
  
  
  
  
  
  
  
  //action program will feedback when listener is called
  public void actionPerformed(ActionEvent e ) {
   
    //press the go button
    if (e.getSource()==go){
      
      // when timer is not runing
      if (!timer.isRunning()) {
        
        //get input from text field
        tilt=Double.parseDouble(tiltInput.getText()); //tile degree
        errorPA=Double.parseDouble(errorPolarAngle.getText()); //polar angle error
        errorRA=Double.parseDouble(errorRotation.getText()); //polar beam east west error
        
        
        
        //create a error dish
        //initialize the error polar beam
        errorBeam1X2=-1*Math.cos(Math.toRadians(errorRA))*Math.cos(Math.toRadians(polarAngle+errorPA))*beam;
        errorBeam1Y2=Math.sin(Math.toRadians(polarAngle+errorPA))*beam;
        errorBeam1Z2=-1*Math.sin(Math.toRadians(errorRA))*Math.cos(Math.toRadians(polarAngle+errorPA))*beam;
        
        
        tempVec.set((float)errorBeam1X2, (float)errorBeam1Y2,(float)errorBeam1Z2);
   
           // Find axis of rotation

        tempVec.normalize();

        crossVec.cross(YAXIS, tempVec);
           // Find amount of rotation and put into matrix
        AxisAngle4f tempAY = new AxisAngle4f();
        tempAY.set(crossVec, (float)Math.acos(YAXIS.dot(tempVec)));
   
        Transform3D tempRotation = new Transform3D();
        tempRotation.set(tempAY);   
           
        Transform3D tempRotation2 = new Transform3D();
           tempRotation2.rotY(errorRA*Math.PI/360);
           
           // Transform to midpoint between two nodes
           Transform3D tempLocation = new Transform3D();
           tempLocation.setIdentity();
           tempLocation.setTranslation(new Vector3f((float)(REDUCE*errorBeam1X2/2), (float)(REDUCE*errorBeam1Y2/2), (float)(REDUCE*errorBeam1Z2/2)));
           tempLocation.mul(tempRotation);
          // tempLocation.mul(tempRotation2);
       
           TransformGroup errorBeam1Trans = new TransformGroup();  
           errorBeam1Trans.setTransform(tempLocation);
           Cylinder errorBeam1 = new Cylinder(0.005f,(float)(REDUCE*beam));
           Appearance ap = new Appearance(); 
           
           //color red
           Color3f col = new Color3f(0.7f, 0.15f, 0.15f); 
           ColoringAttributes ca = new ColoringAttributes(col, ColoringAttributes.NICEST); 
           ap.setColoringAttributes(ca);
           errorBeam1Trans.addChild(errorBeam1);           
           errorBeam1.setAppearance(ap); 

           BranchGroup error=new BranchGroup();           
           error.addChild(errorBeam1Trans);
           
           objRoot.addChild(error);
           
          
         //initialize the ideal rotation beam
      
      double beam2X=beam1X2;
      double beam2X2=beam2X+getNS( polarAngle, tilt,R,rotation);
   
      double beam2Y=beam1Y2;
      double beam2Y2=beam2Y+getHeight( polarAngle, tilt,R,rotation);
   
      double beam2Z=beam1Z2;
      double beam2Z2=-(getEW( polarAngle, tilt,R,rotation)); 
     
      double dx=beam2X2-beam2X;  
      double dy=beam2Y2-beam2Y;
      double dz=beam2Z2-beam2Z;
  
      
      
      tempVec.set((float) dx, (float) dy,(float) dz);
      
      tempVec.normalize();
      crossVec.cross(YAXIS, tempVec); 
   
      beam2AY.set(crossVec, (float)Math.acos(YAXIS.dot(tempVec)));
      beam2RY.set(beam2AY);
      beam2Location.setIdentity();
      beam2Location.setTranslation(new Vector3f((float)(REDUCE*(beam2X+beam2X2)*0.5), (float)(REDUCE*(beam2Y+beam2Y2)*0.5), (float)(REDUCE*(beam2Z+beam2Z2)*0.5)));
   
      beam2Location.mul(beam2RY);
   
      beam2Trans.setTransform(beam2Location);

         //initialize the error rotation beam

         double errorBeam2X=-1*Math.cos(Math.toRadians(polarAngle+errorPA))*beam;
         double errorBeam2X2=errorBeam2X+getNS( polarAngle+errorPA, tilt,R,rotation);
         
         double errorBeam2Y=Math.sin(Math.toRadians(polarAngle+errorPA))*beam; 
         double errorBeam2Y2=errorBeam2Y+getHeight( polarAngle+errorPA, tilt,R,rotation);
         
         double errorBeam2Z=errorBeam1Z2;
         double errorBeam2Z2=-1*(getEW( polarAngle+errorPA, tilt,R,rotation));
         
         
         
         //get the section where the dish is
         int section=0;
         if(errorBeam2X2>0&&errorBeam2Z2>0){
           section=1;
           
         }
         else if(errorBeam2X2<0&&errorBeam2Z2>0){
           section=2;
           
         }
         else if(errorBeam2X2<0&&errorBeam2Z2<0){
           section=3;
         }
         else if(errorBeam2X2>0&&errorBeam2Z2<0){
           section=4;
         }
        
         

         double tempR=Math.sqrt(errorBeam2X2*errorBeam2X2+errorBeam2Z2*errorBeam2Z2);
         double tempAngle=Math.atan(errorBeam2X2/errorBeam2Z2);
         
         //get actual error angle the polar rotate 
         switch (section){
           case 1: tempAngle=tempAngle;break;
           case 2: tempAngle=tempAngle;break;
           case 3: tempAngle=tempAngle+Math.PI;break;        
           case 4: tempAngle=tempAngle+Math.PI;break;             
             
         
         }
         
         errorBeam2X=errorBeam1X2; //use the actual coodinates of the error polar beam
         errorBeam2Y=errorBeam1Y2;     
         errorBeam2Z=errorBeam1Z2;
         
         
         tempAngle=tempAngle-errorRA*2*Math.PI/360; //rotate it in a error east west angle       
         errorBeam2X2=Math.sin(tempAngle)*tempR;
         errorBeam2Z2=Math.cos(tempAngle)*tempR;
         dx=errorBeam2X2-errorBeam2X;
         dy=errorBeam2Y2-errorBeam2Y;
         dz=errorBeam2Z2-errorBeam2Z;

         
        // draw the error rotation beam and add it to branch group
           tempVec=new Vector3f();
           tempVec.set((float) dx, (float) dy,(float) dz);
           
           tempVec.normalize();
           
           crossVec=new Vector3f();    
           crossVec.cross(YAXIS, tempVec); 
           
           errorBeam2AY.set(crossVec, (float)Math.acos(YAXIS.dot(tempVec)));
           errorBeam2RY.set(errorBeam2AY);
           errorBeam2Location.setIdentity();
           errorBeam2Location.setTranslation(new Vector3f((float)(REDUCE*(errorBeam2X+errorBeam2X2)*0.5), (float)(REDUCE*(errorBeam2Y+errorBeam2Y2)*0.5), (float)(REDUCE*(errorBeam2Z+errorBeam2Z2)*0.5)));
           
           errorBeam2Location.mul(errorBeam2RY);
           
           errorBeam2Trans = new TransformGroup();
           errorBeam2Trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
           errorBeam2Trans.setTransform(errorBeam2Location);
           
           Cylinder errorBeam2 = new Cylinder(0.005f,(float)(REDUCE*R));
 
           errorBeam2.setAppearance(ap); 
           
           
           errorBeam2Trans.addChild(errorBeam2);
           
           BranchGroup error2= new BranchGroup();
           
           error2.addChild(errorBeam2Trans); 
           
           objRoot.addChild(error2);           
           
           //write it into text file
           
           
           try {
             out = new BufferedWriter(new FileWriter("output.txt"));
             out.write("N/S\tE/W\tH\tTotal");
             out.newLine();             

           } catch (IOException ex) {
           }

           //open a output.txt file to write the error misalignment result

         timer.start();
//start the timer
      }
      
      }
   //pause button is pressed 
   else if(e.getSource()==pause){
     
     if(timer.isRunning()){  //stop timer
     timer.stop();   
     pause.setText("continue");
     } 
     else{ //start timer
     timer.start();
     pause.setText("pause");
     }
   }   
   
   
   
   //timer part
    else   {
      //dish rotate one degree each time
      rotation=rotation+1;
      
    //update the position of the ideal rotation beam  
      double beam2X=beam1X2;
      double beam2X2=beam2X+getNS( polarAngle, tilt,R,rotation);
   
      double beam2Y=beam1Y2;
      double beam2Y2=beam2Y+getHeight( polarAngle, tilt,R,rotation);
   
      double beam2Z=beam1Z2;
      double beam2Z2=-(getEW( polarAngle, tilt,R,rotation)); 
      double dx=beam2X2-beam2X;
      double dy=beam2Y2-beam2Y;
      double dz=beam2Z2-beam2Z;
  
      
      
      tempVec.set((float) dx, (float) dy,(float) dz);
      
      tempVec.normalize();
      crossVec.cross(YAXIS, tempVec); 
   
      beam2AY.set(crossVec, (float)Math.acos(YAXIS.dot(tempVec)));
      beam2RY.set(beam2AY);
      beam2Location.setIdentity();
      beam2Location.setTranslation(new Vector3f((float)(REDUCE*(beam2X+beam2X2)*0.5), (float)(REDUCE*(beam2Y+beam2Y2)*0.5), (float)(REDUCE*(beam2Z+beam2Z2)*0.5)));
   
      beam2Location.mul(beam2RY);
   
      beam2Trans.setTransform(beam2Location);

   //  update the postion of the error rotation beam

         double errorBeam2X=-1*Math.cos(Math.toRadians(polarAngle+errorPA))*beam;
         double errorBeam2X2=errorBeam2X+getNS( polarAngle+errorPA, tilt,R,rotation);
         
         double errorBeam2Y=Math.sin(Math.toRadians(polarAngle+errorPA))*beam; 
         double errorBeam2Y2=errorBeam2Y+getHeight( polarAngle+errorPA, tilt,R,rotation);
         
         double errorBeam2Z=errorBeam1Z2;
         double errorBeam2Z2=-1*(getEW( polarAngle+errorPA, tilt,R,rotation));
         
         
         
         //get section
         int section=0;
         if(errorBeam2X2>0&&errorBeam2Z2>0){
           section=1;           
         }
         else if(errorBeam2X2<0&&errorBeam2Z2>0){
           section=2;
         }
         else if(errorBeam2X2<0&&errorBeam2Z2<0){
           section=3;
         }
         else if(errorBeam2X2>0&&errorBeam2Z2<0){
           section=4;
         }
        
         
         
         double tempR=Math.sqrt(errorBeam2X2*errorBeam2X2+errorBeam2Z2*errorBeam2Z2);
         double tempAngle=Math.atan(errorBeam2X2/errorBeam2Z2);
         
         //get the actual angle rotated to y axis
         switch (section){
           case 1: tempAngle=tempAngle;break;
           case 2: tempAngle=tempAngle;break;
           case 3: tempAngle=tempAngle+Math.PI;break;        
           case 4: tempAngle=tempAngle+Math.PI;break;             
             
         
         }
         
         errorBeam2X=errorBeam1X2; //use the actual coodinates of the error polar beam
         errorBeam2Y=errorBeam1Y2;     
         errorBeam2Z=errorBeam1Z2;
         
         
         tempAngle=tempAngle-errorRA*2*Math.PI/360; //rotate it in a error east west angle       
         errorBeam2X2=Math.sin(tempAngle)*tempR;
         errorBeam2Z2=Math.cos(tempAngle)*tempR;
         dx=errorBeam2X2-errorBeam2X;
         dy=errorBeam2Y2-errorBeam2Y;
         dz=errorBeam2Z2-errorBeam2Z;
         
         
         tempVec=new Vector3f();
         tempVec.set((float) dx, (float) dy,(float) dz);
         
         tempVec.normalize();
         
         crossVec=new Vector3f();    
         crossVec.cross(YAXIS, tempVec); 
         
         errorBeam2AY.set(crossVec, (float)Math.acos(YAXIS.dot(tempVec)));
         errorBeam2RY.set(errorBeam2AY);
         errorBeam2Location.setIdentity();
         
         Transform3D tempRotation2 = new Transform3D();
         tempRotation2.rotY(errorRA*Math.PI/360);
         
         
         errorBeam2Location.setTranslation(new Vector3f((float)(REDUCE*(errorBeam2X+errorBeam2X2)*0.5), (float)(REDUCE*(errorBeam2Y+errorBeam2Y2)*0.5), (float)(REDUCE*(errorBeam2Z+errorBeam2Z2)*0.5)));
         
         errorBeam2Location.mul(errorBeam2RY);
         errorBeam2Trans.setTransform(errorBeam2Location);
         
         double dNS=30.48*Math.abs(errorBeam2X2-beam2X2); //north south error
         double dEW=30.48*Math.abs(errorBeam2Z2-beam2Z2); //east west error
         double dH=30.48*Math.abs(errorBeam2Y2-beam2Y2);  
         double dTotal=Math.sqrt(dNS*dNS+dEW*dEW+dH*dH); //total error

         distanceNS.setText(String.format("%.2f",dNS));         
         distanceEW.setText(String.format("%.2f",dEW));
         distanceHeight.setText(String.format("%.2f",dH));    
         distanceTotal.setText(String.format("%.2f",dTotal));    
         
         try{
         out.write(String.format("%.2f",dNS)+"\t"+String.format("%.2f",dEW)+"\t"+String.format("%.2f",dH)+" \t"+String.format("%.2f",dTotal));
         out.newLine();
         }
         catch(IOException ex){}
         

    // when the rotation angle more than 270 stop the movement of the dish and close the output file     
         if(rotation>=270){
           timer.stop();
         
         try{
         out.close();         
         }
         catch(IOException ex){}

         }
      }
    
    
      
    
    
       
       

  }

 
    //function to get North South Location Location of the ideal dish  
    public static double getNS(double polarAngle,double tilt,double R,double rotation){
          double beamPivot=Math.cos(Math.toRadians(polarAngle+tilt))*R;
          double deltaNS=Math.cos(Math.toRadians(polarAngle))*beamPivot;
          double deltaH=-1*Math.sin(Math.toRadians(polarAngle))*beamPivot;
          double primeR=Math.sin(Math.toRadians(polarAngle+tilt))*R;
          double NS=primeR*Math.cos(Math.toRadians(90-polarAngle))*(-1)*Math.cos(Math.toRadians(rotation))+deltaNS;
          return NS;
    }
    
    //function to get East West Location of the ideal dish
    
    public static double getEW(double polarAngle,double tilt,double R,double rotation){

          double primeR=Math.sin(Math.toRadians(polarAngle+tilt))*R;
          double EW=primeR*Math.sin(Math.toRadians(rotation));
          return EW;
    }
    
    
    //function to get Height of the ideal dish
    public static double getHeight(double polarAngle,double tilt,double R,double rotation){
          double beamPivot=Math.cos(Math.toRadians(polarAngle+tilt))*R;
          double deltaNS=Math.cos(Math.toRadians(polarAngle))*beamPivot;
          double deltaH=-1*Math.sin(Math.toRadians(polarAngle))*beamPivot;
          double primeR=Math.sin(Math.toRadians(polarAngle+tilt))*R;
          double Height=deltaH-(primeR*Math.tan(Math.toRadians(90-polarAngle))*Math.cos(Math.toRadians(rotation))*Math.cos(Math.toRadians(90-polarAngle)));
          return Height;
    
    }
  
  
  
  
  //main function
  public static void main(String args[]){
    MovementOfDish dish=new MovementOfDish(); //create the MovementOfDish object 
    dish.addKeyListener(dish);  //add listener
    MainFrame mf = new MainFrame(dish, 720, 720);    //set size and add it to the window panel 
    
    

    
    
  }  
    

    


    
    
    
    
    
    
    
  
  
}