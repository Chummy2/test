import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class FoyerGUI {
    JButton addImageButton,removeImageButton;
    JFrame frame;
    String currentDir,rmImgDir,tempDir,mode,fileName;
    JLabel l1, imgLBL,conLBL,numLBL;
    JButton previewBTN,addBTN,leftBTN,rightBTN,deleteBTN,okBTN,cancelBTN;
    JTextField ipTXT;  
    JTextArea consoleTXT; 
    JScrollPane scroll;
    File f;
    String[] rPiIps={"192.168.68.57"}; //facing the door from the inside: front left, front right, rear
    String[] pathnames;
    int currentIter=0;
    static FoyerGUI gui = new FoyerGUI();

    public static void main(String[] args){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }
        
        gui.startProgram();
    }
    public void startProgram(){
        currentDir = System.getProperty("user.dir");
        tempDir = currentDir+"\\temp\\";
        mode = "";
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // this line makes the program quit as soon as you close the window (if you leave this out it will just sit there on the screen forever)
        frame.setVisible(true); // this makes the frame visible. if you forget this step, you won't see anything when you run this code
        // piIp="10.60.4.10";
        // l1 = new JLabel("RPi ip: ");
        // l1.setBounds(30,50,100,30);

        conLBL = new JLabel("Connected to: 192.168.68.57");
        conLBL.setBounds(5,1,235,100);
          
        // ipTXT = new JTextField("");
        // ipTXT.setBounds(100,50, 100,30); //x axis, y axis, width, height   

        consoleTXT = new JTextArea();
        // consoleTXT.setSize(200, 200);
        consoleTXT.setEditable(false);
        consoleTXT.setLineWrap(true);
        consoleTXT.setVisible(true);

        scroll = new JScrollPane(consoleTXT);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(50,400,275,200);
        
        previewBTN=new JButton("PREVIEW");
        previewBTN.setBounds(100,100,100, 40);//x axis, y axis, width, height  
        previewBTN.addActionListener(new PreviewImage());

        addBTN=new JButton("CHOOSE IMAGE");//creating instance of JButton  
        addBTN.setBounds(90,150,120, 40);//x axis, y axis, width, height  
        addBTN.addActionListener(new AddImage());
          
        leftBTN=new JButton("<-");
        leftBTN.setBounds(500,375,50, 40);//x axis, y axis, width, height  
        leftBTN.setEnabled(false);
        leftBTN.setVisible(false);
        leftBTN.addActionListener(new CycleLeft());

        rightBTN=new JButton("->");//creating instance of JButton  
        rightBTN.setBounds(550,375,50, 40);//x axis, y axis, width, height  
        rightBTN.setEnabled(false);
        rightBTN.setVisible(false);
        rightBTN.addActionListener(new CycleRight());

        deleteBTN=new JButton("DELETE");
        deleteBTN.setBounds(500,425,100, 40);//x axis, y axis, width, height  
        deleteBTN.addActionListener(new RemoveImage());
        deleteBTN.setEnabled(false);
        deleteBTN.setVisible(false);

        cancelBTN=new JButton("CANCEL");
        cancelBTN.setBounds(500,425,100, 40);//x axis, y axis, width, height  
        cancelBTN.addActionListener(new ResetProgram());
        cancelBTN.setEnabled(false);
        cancelBTN.setVisible(false);

        okBTN=new JButton("CONFIRM");//creating instance of JButton  
        okBTN.setBounds(500,475,100, 40);//x axis, y axis, width, height  
        okBTN.setEnabled(false);
        okBTN.setVisible(false);
        okBTN.addActionListener(new ConfirmAction());

        numLBL = new JLabel("0 / 0");
        numLBL.setBounds(550,50,235,20);

        String spaces="                                                                    ";
        Border blackline = BorderFactory.createLineBorder(Color.black);
        imgLBL = new JLabel(spaces+"Image Preview");
        imgLBL.setBounds(300,75,500,300);
        imgLBL.setBorder(blackline);

        frame.add(previewBTN);frame.add(addBTN);
        frame.add(leftBTN);frame.add(rightBTN);frame.add(deleteBTN);frame.add(okBTN);frame.add(cancelBTN);frame.add(numLBL);
        //frame.add(l1);
        frame.add(imgLBL);frame.add(conLBL);
        // frame.add(ipTXT);
        frame.add(scroll);
        frame.setSize(900,800);//400 width and 500 height  
        frame.setLayout(null);//using no layout managers   
    }

    class AddImage implements ActionListener {
        public void actionPerformed(ActionEvent event){
            
            // addBTN.setVisible(false);
            // addBTN.setEnabled(false);
            JFileChooser chooser = new JFileChooser(System.getProperty("user.home")+System.getProperty("file.separator")+"Downloads");
            int status = chooser.showOpenDialog(null);
            if (status == JFileChooser.APPROVE_OPTION) {
                okBTN.setEnabled(true);
                okBTN.setVisible(true);
                previewBTN.setEnabled(false);
                previewBTN.setVisible(false);
                cancelBTN.setEnabled(true);
                cancelBTN.setVisible(true);
                okBTN.setText("ADD IMAGE");
                mode="add";
                numLBL.setText("1 / 1");
                fileName = chooser.getSelectedFile().getAbsolutePath();
                imgLBL.setIcon(resize(fileName));
                // System.out.println(fileName);
            }
        }
    }
    class RemoveImage implements ActionListener{
        public void actionPerformed(ActionEvent event)  {
            // String currentPath = tempDir+pathnames[currentIter];
            String currentImage = pathnames[currentIter];
            // piIp=ipTXT.getText();
            // FoyerGUI gui = new FoyerGUI();
            // if (ipTXT.equals("")){
            //     consoleTXT.append("Please type an ip into the 'RPi ip' field!\n");
            // }else{
            if((infoBox("Are you sure you want to delete this picture?", "Warning"))==0){
                for(String i: rPiIps){
                    if(i.equals(rPiIps[0])){
                        consoleTXT.append("Removing image...\n");
                        ArrayList<String> listy = new ArrayList<String>(Arrays.asList(pathnames));
                        listy.remove(currentImage);
                        pathnames = listy.toArray(new String[0]);
                        rightBTN.doClick();
                    }
                    try{
                        gui.removeImage(currentImage,i);
                    }
                    catch(IOException e){
                        System.err.println("IOException");
                        consoleTXT.append("Something went wrong!");
                    }
                }
            }
        }
    }

    class ResetProgram implements ActionListener{
        public void actionPerformed(ActionEvent event){
            mode="";
            deleteBTN.setEnabled(false);
            deleteBTN.setVisible(false);
            leftBTN.setEnabled(false);
            leftBTN.setVisible(false);
            rightBTN.setEnabled(false);
            rightBTN.setVisible(false);
            addBTN.setEnabled(true);
            addBTN.setVisible(true);
            previewBTN.setEnabled(true);
            previewBTN.setVisible(true);
            okBTN.setEnabled(false);
            okBTN.setVisible(false);
            imgLBL.setIcon(null);
            cancelBTN.setEnabled(false);
            cancelBTN.setVisible(false);
            numLBL.setText("0 / 0");
        }
    }

    class ConfirmAction implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (mode.equals("delete")){
                mode="";
                try {
                    consoleTXT.append("Removing temp data...\n");
                    removeTemp();
                } catch (IOException e) {
                    e.printStackTrace();
                    consoleTXT.append("Something went wrong!");
                }
                deleteBTN.setEnabled(false);
                deleteBTN.setVisible(false);
                leftBTN.setEnabled(false);
                leftBTN.setVisible(false);
                rightBTN.setEnabled(false);
                rightBTN.setVisible(false);
                addBTN.setEnabled(true);
                addBTN.setVisible(true);
                okBTN.setEnabled(false);
                okBTN.setVisible(false);
                imgLBL.setIcon(null);
                numLBL.setText("0 / 0");
                
            }else if (mode.equals("add")){
                // piIp=ipTXT.getText();
                // if (piIp.equals("")){
                //     consoleTXT.append("Please type an ip into the 'RPi ip' field!\n");
                // }else{
                for(String i: rPiIps){
                    if(i.equals(rPiIps[0])){
                        okBTN.setEnabled(false);
                        okBTN.setVisible(false);
                        previewBTN.setEnabled(true);
                        previewBTN.setVisible(true);
                        cancelBTN.setEnabled(false);
                        cancelBTN.setVisible(false);
                        mode="";
                        consoleTXT.append("Adding image...\n");
                        imgLBL.setIcon(null);
                        numLBL.setText("0 / 0");
                    }
                    // FoyerGUI gui = new FoyerGUI();
                    try{
                        gui.addImage(fileName,i);
                    }
                    catch(IOException e){
                        System.err.println("IOException");
                        consoleTXT.append("Something went wrong!");
                    }
                }
            }else{
                System.out.println("No action selected!");
                consoleTXT.append("No action selected!");
            }
        }
    }

    class PreviewImage implements ActionListener{
        public void actionPerformed(ActionEvent event){
            // piIp=ipTXT.getText();
            // if (piIp.equals("")){
            //     consoleTXT.append("Please type an ip into the 'RPi ip' field!\n");
            // }else{
            deleteBTN.setEnabled(true);
            deleteBTN.setVisible(true);
            okBTN.setEnabled(true);
            okBTN.setVisible(true);
            okBTN.setText("FINISH");
            mode="delete";
            leftBTN.setEnabled(true);
            leftBTN.setVisible(true);
            rightBTN.setEnabled(true);
            rightBTN.setVisible(true);
            addBTN.setEnabled(false);
            addBTN.setVisible(false);
            currentIter=0;
            System.out.println("Checking for/Removing existing temp folder...");
            try{
                removeTemp();
            } catch (Exception e) {System.out.println("No existing temp folder found.");}
            try {
                consoleTXT.append("Acquiring images...\n");
                getTempImages(rPiIps[0]);
            } catch (IOException e) {
                e.printStackTrace();
                consoleTXT.append("Something went wrong!");
            }

            f = new File(tempDir);
            pathnames = f.list();
            imgLBL.setIcon(resize(tempDir+pathnames[currentIter]));
            numLBL.setText((currentIter+1)+" / "+pathnames.length);
            // }
        }
    }

    class CycleRight implements ActionListener{
        public void actionPerformed(ActionEvent event){
            try{
                currentIter+=1;
                imgLBL.setIcon(resize(tempDir+pathnames[currentIter]));
            }catch(Exception e){
                currentIter=0;
                imgLBL.setIcon(resize(tempDir+pathnames[currentIter]));
            }
            numLBL.setText((currentIter+1)+" / "+pathnames.length);
        }
    }
    class CycleLeft implements ActionListener{
        public void actionPerformed(ActionEvent event){
            try{
                currentIter-=1;
                imgLBL.setIcon(resize(tempDir+pathnames[currentIter]));
            }catch(Exception e){
                currentIter=pathnames.length-1;
                imgLBL.setIcon(resize(tempDir+pathnames[currentIter]));
            }
            numLBL.setText((currentIter+1)+" / "+pathnames.length);
        }
    }

    //https://stackhowto.com/how-to-use-jfilechooser-to-display-image-in-a-jframe/
    public ImageIcon resize(String imgPath){
      ImageIcon path = new ImageIcon(imgPath);
      Image img = path.getImage();
      Image newImg = img.getScaledInstance(imgLBL.getWidth(), imgLBL.getHeight(), Image.SCALE_SMOOTH);
      ImageIcon image = new ImageIcon(newImg);
      return image;
    }

    public void addImage(String fileName, String piIp) throws IOException{
        // String[] commands = {"powershell.exe", "Set-Variable", "-Name \"fileName\" -Value \""+fileName+"\";", "C:\\Users\\padawan\\Documents\\Foyer\\javaVersion\\"+scriptName};
        // Process proc = runtime.exec("powershell C:\\Users\\padawan\\Documents\\foyerSlideShow\\javaVersion\\addImageToPiScript.ps1");
        // System.out.println("Adding image...");
        
        String command = "powershell scp.exe \'"+fileName+"\' \"pi@"+piIp+":Documents/FoyerScreens/\"";
        Process process = Runtime.getRuntime().exec(command);
        process.getInputStream().close();
        String line;
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
            process.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
        }
        stderr.close();
        // System.out.println("Image added successfully.");
        consoleTXT.append("Image added successfully.\n");
    }
    public void removeImage(String fileName, String piIp) throws IOException{
        // System.out.println("Removing image...");
        
        String command = "powershell ssh.exe \"pi@"+piIp+"\" \"rm Documents/FoyerScreens/"+fileName+"\"";
        Process process = Runtime.getRuntime().exec(command);
        process.getInputStream().close();
        String line;
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
            process.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
            break;
        }
        stderr.close();
        // System.out.println("Removed image successfully.");
        consoleTXT.append("Removed image successfully.\n");
    }
    public void getTempImages(String piIp) throws IOException{
        // System.out.println("Acquiring images...");
        
        setWaitCursor(frame);
         //String command = "powershell.exe  your command";
         //String command = "powershell.exe  \"C:\\Pathtofile\\script.ps\" ";
         //String[] commands = {"powershell.exe", "Set-Variable", "-Name \"fileName\" -Value \""+fileName+"\";", "C:\\Users\\padawan\\Documents\\Foyer\\javaVersion\\"+scriptName};
        String command = "powershell New-Item -Path 'temp' -ItemType Directory;scp.exe \"pi@"+piIp+":Documents/FoyerScreens/*\" \"temp\"";
        Process process = Runtime.getRuntime().exec(command);
        //  Process proc = runtime.exec(commands);
        process.getInputStream().close();
        String line;
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
            process.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
        }
        stderr.close();
        setDefaultCursor(frame);
        // System.out.println("Acquired images.");
        consoleTXT.append("Acquired images.\n");
    }
    public void removeTemp() throws IOException{
        // System.out.println("Removing temp data...");
        
        String command = "powershell Remove-Item -path \"temp\" -recurse";
        Process process = Runtime.getRuntime().exec(command);
        process.getInputStream().close();
        String line;
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
            process.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
        }
        stderr.close();
        // System.out.println("Removed temp data successfully.");
        consoleTXT.append("Removed temp data successfully.\n");
    }
    public static void setWaitCursor(JFrame frame) {
        if (frame != null) {
            RootPaneContainer root = (RootPaneContainer) frame.getRootPane().getTopLevelAncestor();
            root.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            root.getGlassPane().setVisible(true);
        }
    }
    public static void setDefaultCursor(JFrame frame) {
        if (frame != null) {
            RootPaneContainer root = (RootPaneContainer) frame.getRootPane().getTopLevelAncestor();
            root.getGlassPane().setCursor(Cursor.getDefaultCursor());
        }
    }
    public static int infoBox(String infoMessage, String titleBar)
    {
        return JOptionPane.showConfirmDialog(null, infoMessage, titleBar, JOptionPane.OK_CANCEL_OPTION);
    }
}