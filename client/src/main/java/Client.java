import java.net.InetAddress;
import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import Demo.ChatCallbackPrx;
import Demo.ChatRoomPrx;

public class Client{
    public static void main(String[] args) {
        try(Communicator communicator = Util.initialize(args, "client.cfg")){
            ChatRoomPrx service = ChatRoomPrx.checkedCast(communicator.propertyToProxy("Printer.Proxy"));
            if (service == null) throw new Error("Invalid proxy");

            ObjectAdapter adapter = communicator.createObjectAdapter("Client");
            ChatCallbackI callbackI = new ChatCallbackI();
            ObjectPrx callbackBase = adapter.add(callbackI, Util.stringToIdentity("callback"));
            adapter.activate();

            ChatCallbackPrx callbackPrx = ChatCallbackPrx.uncheckedCast(callbackBase);

            Scanner scanner = new Scanner(System.in);
            //String username = System.getProperty("user.name");
            String hostname = InetAddress.getLocalHost().getHostName();
            String username = getUsername(callbackPrx, service);
            while (true) {
                System.out.print("Ingrese el comando: ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")){
                    service.leave(username);
                    break;
                } 
                if (input.startsWith("to ")) {
                    String[] splitMessage = input.split(":", 2);
                    String userHost = splitMessage[0];
                    String message = splitMessage[1];
                    String[] splitUsername = userHost.split(" ", 2);
                    String receptor = splitUsername[1];
                    service.sendMessage(message, receptor);
                }
                if (input.startsWith("BC")) {
                    String[] splitMessage = input.split(":", 2);
                    String message = splitMessage[1];
                    service.sendMessageBC(message);
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUsername(ChatCallbackPrx callbackPrx, ChatRoomPrx service){
        Scanner scanner = new Scanner(System.in);
        String input = "";
        while (true) { 
            System.out.println("Type username: ");
            input = scanner.nextLine();
            String result = service.join(input, callbackPrx);
            if(result.startsWith("User added:")){
                System.out.println(result);
                break;
            } 
            System.out.println(result + ". Try again!");
        }
        return input;
    }
}