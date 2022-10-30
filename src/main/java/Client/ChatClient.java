package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

class ChatClientThread extends Thread{
    private JTextField tx;
    private JTextArea ta, ta1;
    private String login;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Socket socketClient;

    public ChatClientThread(String l){
        this.login=l;

        JFrame f=new JFrame("my chat - " + login);
        f.setSize(400,400);        
        
        JPanel p1=new JPanel();
        p1.setLayout(new BorderLayout());

        tx=new JTextField();
        p1.add(tx, BorderLayout.CENTER);

        JButton b1=new JButton("Send");
        p1.add(b1, BorderLayout.EAST);

        JPanel leftPanel=new JPanel();
        leftPanel.setLayout(new BorderLayout());
        ta=new JTextArea();
        ta.setEditable(false);
        ta.setLineWrap(true);
        leftPanel.add(ta, BorderLayout.CENTER);

        JPanel rightPanel=new JPanel();
        rightPanel.setLayout(new BorderLayout());
        ta1=new JTextArea();
        ta1.setEditable(false);
        ta1.setLineWrap(true);
        rightPanel.add(ta1, BorderLayout.CENTER);

        JPanel chatPanel = new JPanel(new GridLayout(1,2));
        chatPanel.add(leftPanel);
        chatPanel.add(rightPanel);

        JPanel p2=new JPanel();
        p2.setLayout(new BorderLayout());

        p2.add(chatPanel, BorderLayout.CENTER);
        p2.add(p1, BorderLayout.SOUTH);
        
        f.setContentPane(p2);
        
        try{
            socketClient = new Socket("localhost",5555);
            writer = new BufferedWriter(new OutputStreamWriter(socketClient.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Server not running");
            System.exit(1);
        }
    
        b1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                String clientMessage=login+": "+tx.getText();
                ta.append("Me: " + tx.getText() + "\n");
                ta1.append("" + "\n");
                tx.setText("");
                System.out.println(login + " sent " + clientMessage);
                try{
                    writer.write(clientMessage);
                    writer.write("\r\n");
                    writer.flush(); 
                    }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
          }
        );

        f.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                try{
                    writer.write("exit");
                    writer.write("\r\n");
                    writer.flush();
                    System.out.println(login + " closed chat client");
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
                finally {
                    try {
                        writer.close();
                        reader.close();
                        socketClient.close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        f.setVisible(true);
    }

    @Override
    public void run(){
        System.out.println(login + " opened chat client");
        try{
            while(true){
                    String serverMessage = reader.readLine();
                    ta1.append(serverMessage + "\n");
                    ta.append("" + "\n");
                    System.out.println(login + " received " + serverMessage);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

class ChatClient {
    public static void main(String argv[]) {
        ChatClientThread myClientChat1 = new ChatClientThread("tobias");
        myClientChat1.start();

        ChatClientThread myClientChat2 = new ChatClientThread("jesse");
        myClientChat2.start();

        ChatClientThread myClientChat3 = new ChatClientThread("nath");
        myClientChat3.start();

        ChatClientThread myClientChat4 = new ChatClientThread("chris");
        myClientChat4.start();
    }
}
