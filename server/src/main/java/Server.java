import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import com.zeroc.Ice.Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public class Server{
    public static void main(String[] args) {
        int status = 0;
        try(Communicator communicator = Util.initialize(args, "server.cfg")){
            ObjectAdapter adapter = communicator.createObjectAdapter("ChatRoom");
            Object object = new ChatRoomI();
            adapter.add(object, Util.stringToIdentity("Server"));
            adapter.activate();
            System.out.println("Server started...");
            communicator.waitForShutdown();
        }catch(Exception e){

        }
    }
}