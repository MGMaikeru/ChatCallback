import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.zeroc.Ice.Current;

import Demo.ChatCallbackPrx;
import Demo.ChatRoom;

public class ChatRoomI implements ChatRoom{
    private final UserManager userManager;
    private final ExecutorService taskExecutor = Executors.newFixedThreadPool(10);

    public ChatRoomI(){
        this.userManager = new UserManager();

    }

    public String join(String username, ChatCallbackPrx callback, Current current){
        String result = userManager.addChatter(username, callback);
        System.out.println(result);
        return result;
    }

    @Override
    public void listUsernames(ChatCallbackPrx callback, Current current){
        CompletableFuture.runAsync(() -> {
            String response = userManager.getChattersNames();
            callback.receiveMessage(response);
        }, taskExecutor);
    }

    @Override
    public void sendMessageBC(String sender,String message, Current current) {
        CompletableFuture.runAsync(() -> {
            userManager.broadcastMessage(sender, message);
        }, taskExecutor);
    }

    @Override
    public void sendMessage(String sender, String message, String receptor, Current current) {
        CompletableFuture.runAsync(() -> {
            userManager.sendMessageToUser(sender, receptor, message);
        }, taskExecutor);
    }

    @Override
    public String leave(String username, Current current) {
        String result = userManager.removeChatter(username);
        System.out.println(result);
        return result;
    }

    public void shutdown(){
        taskExecutor.shutdown();
    }
}