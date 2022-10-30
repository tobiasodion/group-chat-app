package ChatServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ChatServerThread extends Thread{
    private Socket _connectionSocket;
    private final int _connectionNo;
    private BufferedReader reader;
    //private BufferedWriter writer;
    private PrintWriter writer;

    public ChatServerThread (Socket socket, int connectionNo){
        this._connectionSocket = socket;
        this._connectionNo = connectionNo;
    }

    @Override
    public void run() {
        try{
            System.out.println("Client" + _connectionNo + " Got Connected");
            reader = new BufferedReader(new InputStreamReader(_connectionSocket.getInputStream()));
            //writer = new BufferedWriter(new OutputStreamWriter(_connectionSocket.getOutputStream()));
            writer = new PrintWriter(_connectionSocket.getOutputStream(), true);

            while(true){
                String clientMessage = reader.readLine();
                System.out.println("Server Received: " + clientMessage);

                if(clientMessage.equals("exit")){
                    break;
                }
                printToAllClients(clientMessage);
            }
            System.out.println("Client" + _connectionNo + " Got Disconnected");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try{
                writer.close();
                reader.close();
                _connectionSocket.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void printToAllClients(String message){
        for(ChatServerThread cs : ChatServer.chatServerThreads) {
            try{
                if(cs._connectionSocket != this._connectionSocket){
                    System.out.println("Server sent "+message+" to : " + cs._connectionSocket);
                    //cs.writer.write(message);
                    cs.writer.println(message);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

class ChatServer{
    static ArrayList<ChatServerThread> chatServerThreads = new ArrayList<>();

    public static void main(String argv[]) throws Exception {
        int connectionNo = 0;
        System.out.println(" Server is Running ");

        try(ServerSocket mysocket = new ServerSocket(5555)){
            while (true) {
                Socket connectionSocket = mysocket.accept();
                connectionNo++;
                ChatServerThread chatServerThread = new ChatServerThread(connectionSocket, connectionNo);
                chatServerThreads.add(chatServerThread);
                chatServerThread.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

