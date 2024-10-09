import Demo.Response;
import com.zeroc.Ice.Current;

import Demo.ChatCallbackPrx;
import Demo.ChatRoom;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatRoomI implements ChatRoom{
    private final UserManager userManager;

    public ChatRoomI(){
        this.userManager = new UserManager();
    }

    public String join(String username, ChatCallbackPrx callback, Current current){
        String result = userManager.addChatter(username, callback);
        System.out.println(result);
        return result;
    }

    @Override
    public String listUsernames(Current current){
        return userManager.getChattersNames();
    }

    @Override
    public void sendMessageBC(String sender,String message, Current current) {
        userManager.broadcastMessage(sender, message);
    }

    @Override
    public void sendMessage(String sender, String message, String receptor, Current current) {
        userManager.sendMessageToUser(sender, receptor, message);
    }

    @Override
    public String leave(String username, Current current) {
        String result = userManager.removeChatter(username);
        System.out.println(result);
        return result;
    }

    @Override
    public Response executeCommand(String username, String command, Current current) {
        long startTime = System.currentTimeMillis();
        String result = "";

        if (command.matches("\\d+")) {
            int number = Integer.parseInt(command);
            String fibSeries = fibonacci(number);
            String primeFactors = primeFactors(number);
            result = "Fibonacci series: " + fibSeries + "\nPrime factors: " + primeFactors;
        } else if (command.startsWith("listifs")) {
            result = listInterfaces();
        } else if (command.startsWith("listports")) {
            String[] parts = command.split(" ");
            if (parts.length > 1) {
                String ipAddress = parts[1];
                result = listPortsServices(ipAddress);
            } else {
                result = "Error: No IP address provided.";
            }
        } else if (command.startsWith("!")) {
            String cmd = command.substring(1);
            result = executeCMD(cmd);
        } else {
            result = "Unknown command.";
        }

        long responseTime = System.currentTimeMillis() - startTime;
        return new Response(responseTime, result);
    }

    private String fibonacci(int number) {
        List<BigInteger> series = new ArrayList<>();
        BigInteger a = BigInteger.ZERO, b = BigInteger.ONE;
        while (number-- > 0) {
            series.add(a);
            BigInteger temp = a.add(b);
            a = b;
            b = temp;
        }
        return series.toString();
    }

    private String primeFactors(int number) {
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= number; i++) {
            while (number % i == 0) {
                primes.add(i);
                number /= i;
            }
        }
        return primes.toString();
    }

    private String listInterfaces() {
        StringBuilder sb = new StringBuilder();
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface netint : interfaces) {
                sb.append(String.format("Display name: %s, Name: %s%n",
                        netint.getDisplayName(), netint.getName()));
            }
        } catch (SocketException e) {
            sb.append("Error listing network interfaces: ").append(e.getMessage());
        }
        return sb.toString();
    }

    private String listPortsServices(String ip) {
        StringBuilder ports = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("nmap -p- " + ip);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                ports.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            ports.append("Error executing nmap command: ").append(e.getMessage());
        }
        return ports.toString();
    }

    private String executeCMD(String command) {
        StringBuilder result = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            result.append("Error executing command: ").append(e.getMessage());
        }
        return result.toString();
    }
}