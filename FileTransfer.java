package project3;
import ezprivacy.protocol.IntegrityCheckException;
import ezprivacy.toolkit.CipherUtil;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
//*********************//
//F74016132
//using eclipse
//os: win7
//JDK: 1.8.0_60
//JRE: 1.8.0_60 
//*********************//
public class FileTransfer extends JFrame implements ActionListener {
	//*************GUI objects********************//
	JFrame demo = new JFrame("File Transfer");
	JFrame ServerF = new JFrame("Server");
	JFrame ClientF = new JFrame("Client");
	JButton button1 = new JButton("Server");
    JButton button2 = new JButton("Client"); 
    JButton Servercheck = new JButton("OK");
    JButton Clientcheck = new JButton("OK");
    JButton Filecheck = new JButton("choose");
    JButton Trans = new JButton("GO");
    JLabel port = new JLabel("port : ");
    JLabel IP = new JLabel("IP : ");
    JLabel SelectFile = new JLabel("Choose File");
    JTextField textarea = new JTextField(20);	
    JTextField textarea1 = new JTextField(20);
    JTextField textarea2 = new JTextField(20);
    JTextArea messageshow = new JTextArea( 32, 50 );
    JTextArea messageshow1 = new JTextArea( 32, 50 );
    JTextField t1 = new JTextField(30);
    JFileChooser chooser = new JFileChooser(); 
    byte[] key = "oiEAitVbJHJFdkjW".getBytes();
	byte[] iv = "0101010101010101".getBytes();
	Socket sc;
	int portnum;
	//***************initial ***********************//
	public  FileTransfer(){		
        demo.setSize(400, 300);
        demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        button1.setPreferredSize(new Dimension(200, 300));
        button2.setPreferredSize(new Dimension(200, 300));
        demo.add(button1, BorderLayout.WEST);
        demo.add(button2, BorderLayout.EAST);
        demo.add(t1,BorderLayout.NORTH);
        demo.setVisible(true);
        button1.addActionListener(this);
        button2.addActionListener(this);
        Servercheck.addActionListener(this);  
        Clientcheck.addActionListener(this);
        Filecheck.addActionListener(this);
        Trans.addActionListener(this);
        messageshow.setEditable(false);
        messageshow.setLineWrap(true);
        messageshow.setAutoscrolls( true );
        messageshow1.setEditable(false);
        messageshow1.setLineWrap(true);
        messageshow1.setAutoscrolls( true );
        JScrollPane panel = new JScrollPane(messageshow,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}	
	public static void main(String[] args)
    {
		FileTransfer frame = new FileTransfer();
    }
	
	//******************listen event*******************//
	@Override
	public void actionPerformed(ActionEvent e) {
		//**********press server**********//
		if (e.getSource() == button1) {
			t1.setText("1st button clicked");
			demo.dispose();
			ServerF.setVisible(true);
			ServerF.setSize(400, 300);
	        JPanel p = new JPanel();
	        JPanel p1 = new JPanel();
			ServerF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			p.add(port,BorderLayout.NORTH);
			p.add(textarea,BorderLayout.NORTH);
			p.add(Servercheck,BorderLayout.NORTH);
			p1.add(messageshow,BorderLayout.SOUTH);
			ServerF.add(p,BorderLayout.NORTH);
			ServerF.add(p1,BorderLayout.SOUTH);
			ServerF.pack();			
		}
		//**********press client**********//
		else if (e.getSource() == button2) {
			t1.setText("2st button clicked");
			demo.dispose();
			ClientF.setVisible(true);
			ClientF.setSize(400, 300);
			ClientF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel p = new JPanel();
			JPanel p1 = new JPanel();
			JPanel p2 = new JPanel();
			p.add(port,BorderLayout.NORTH);			
			p.add(textarea,BorderLayout.NORTH);
			p.add(IP,BorderLayout.NORTH);
			p.add(textarea1,BorderLayout.NORTH);
			p.add(Clientcheck,BorderLayout.CENTER);
			p1.add(SelectFile,BorderLayout.SOUTH);
			p1.add(textarea2,BorderLayout.SOUTH);
			p1.add(Filecheck,BorderLayout.SOUTH);
			p1.add(Trans,BorderLayout.SOUTH);
			p2.add(messageshow1,BorderLayout.SOUTH);
			ClientF.add(p,BorderLayout.NORTH);
			ClientF.add(p1,BorderLayout.CENTER);
			ClientF.add(p2,BorderLayout.SOUTH);
			ClientF.pack();
		}
		//*********server on************//
		if (e.getSource() == Servercheck){
			show("Server ready......\n");
			Server(Integer.parseInt(textarea.getText()));
		}
		//*********client connect server ************//
		if (e.getSource() == Clientcheck){
			messageshow1.setText("Client ready......\n");	
			try{
				//connect(port,IP);//
				connect(textarea1.getText(), Integer.parseInt(textarea.getText()));
			}catch( IOException err ){
	            err.printStackTrace();
	        }			
		}
		// ***************select File*************//
		if (e.getSource() == Filecheck){
			try{
				File workingDirectory = new File(System.getProperty("user.dir"));
				chooser.setCurrentDirectory(workingDirectory);
				int ret=chooser.showOpenDialog(null);
		    }catch(Exception er){
			      System.out.println(er.toString());
			}
			textarea2.setText(chooser.getSelectedFile().getName());
		}
		//************file transfer*****************//
		if (e.getSource() == Trans){
			messageshow1.setText("Client Trans......\n");
			try {
				Client(textarea2.getText());
			} catch (IOException e1) {
				 System.out.println(e1.toString());
			}		
		}
	}
	public void connect(String ip, int port) throws IOException{
        sc = new Socket(ip, port);
    } 
	public void show(String s){
		messageshow.setText(s);
	}
	public void Server(int portnum){
		ServerSocket s;	    
	    int bytesRead,size;
	    try{
            s = new ServerSocket(portnum);
            Socket sock = s.accept();
            DataInputStream clientData = new DataInputStream(sock.getInputStream());
            while(true){
                String fileName = clientData.readUTF();
                //***if type close then close the socket****//
                if(fileName.equals("close")){                	
                	break;
                }
                else{
                	OutputStream output = new FileOutputStream(fileName);
                    long datasize = clientData.readLong();                    
                    byte[] buffer = new byte[(int)datasize];
                    bytesRead = clientData.read(buffer, 0, (int)datasize);
                    try {
                    	// *******auth descrypt*******//
                    	byte[] plaintex = CipherUtil.authDecrypt(key, iv, buffer);
                    	//*******write*******//
            			output.write(plaintex);            			
            			messageshow.append("receive "+fileName+" : "+plaintex.length+"bytes\n");
            			output.close();            			            			
            		} catch (IntegrityCheckException e) {
            			e.printStackTrace();
            		}                    
                }
            }
        } catch (IOException e){
        	System.err.println("Client error. Connection closed.");
        }
	}
	public void Client(String filename) throws IOException{
		FileInputStream fis;
	    OutputStream writer;
	    BufferedInputStream bis;
	    OutputStream os;
	    if(filename.equals("close")){
			os = sc.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(filename);            
		}
		else{        				
            try {
				File myFile = new File(filename);
    			byte[] mybytearray = new byte[(int) myFile.length()];
    			fis = new FileInputStream(myFile);
                bis = new BufferedInputStream(fis);
                DataInputStream dis = new DataInputStream(bis);
                dis.readFully(mybytearray, 0, mybytearray.length);
                dis.close();
                os = sc.getOutputStream();
                DataOutputStream dos = new DataOutputStream(os);
                //*****send file name***//
                dos.writeUTF(myFile.getName()); 
                //*****auth encrypt*******//
                byte[] ciphertext = CipherUtil.authEncrypt(key, iv, mybytearray);
                //*****send file size****//
                dos.writeLong(ciphertext.length);
                //*****send file******//
                dos.write(ciphertext, 0, ciphertext.length);
                dos.flush();
                messageshow1.append("Sending " + filename + "(" + mybytearray.length + " bytes)");
			}catch (Exception e){
				messageshow1.setText("File does not exist!");
			}             
		}  
	    	
	}
}
