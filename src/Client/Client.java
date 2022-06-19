package Client;

import Gui.Client.ClientConfigFrame;
import Gui.Client.ClientMainFrame;
import Messages.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Client class
 */
public class Client {

    private static String ip;
    private static int port;
    private static int id;
    private static int requestCounter = 0;

    private static ServerSocket serverSocket;
    private static final ReentrantLock l = new ReentrantLock();
    private static Condition waitSocket = l.newCondition();

    private static final ClientConfigFrame configGui = new ClientConfigFrame();
    private static final ClientMainFrame mainGui = new ClientMainFrame();

    public static void startClient(String ip, int port, int id) {
        Client.ip = ip;
        Client.port = port;
        Client.id = id;

        try {
            l.lock();
            serverSocket = new ServerSocket(port);
            waitSocket.signal();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            l.unlock();
        }

        configGui.setVisible(false);

        mainGui.setIp(ip);
        mainGui.setPort(port);
        mainGui.setId(id);
        mainGui.setVisible(true);
    }

    public static void stopClient() {
        try {
            l.lock();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            l.unlock();
        }

        mainGui.setVisible(false);
        configGui.setVisible(true);
    }

    public static void sendRequestToLB(String ip, int port, int nbOfIteration, int deadline){
        Request request = new Request(id, requestCounter++, 1, 01, nbOfIteration, 0, deadline, Client.ip, Client.port);

        try {
            Socket lbSocket = new Socket(ip, port);
            ObjectOutputStream oos = new ObjectOutputStream(lbSocket.getOutputStream());
            oos.writeObject(request);
            System.out.println("Request sent to LB !");

            mainGui.addPendingRequest(request);
            //create connection with server
//            Socket socket = serverSocket.accept();
//            TServerHandler thread = new TServerHandler(socket);
//            System.out.println("connection made with server!");
//            thread.start();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     * Tasks to be done by a client
     */
    public static void main(String[] args) {

        //creates requests
        Request request = new Request(1, 5, 1, 01, 2, 0, 2, "127.0.0.1", 5055);
        LinkedList<Request> listRequest = new LinkedList<>();
        listRequest.add(request);
        listRequest.add(request);

        configGui.setVisible(true);
        configGui.setStartCallback(Client::startClient);
        mainGui.setSendCallback(Client::sendRequestToLB);
        mainGui.setStopCallback(Client::stopClient);

        while (true) {
            l.lock();
            while (serverSocket == null || serverSocket.isClosed()) {
                System.out.println("Waiting for main gui");
                waitSocket.awaitUninterruptibly();
            }
            l.unlock();

            try {
                Socket socket = serverSocket.accept();
                if (socket != null) (new TServerHandler(socket, mainGui)).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
