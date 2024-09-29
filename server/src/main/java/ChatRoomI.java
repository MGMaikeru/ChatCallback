import com.zeroc.Ice.Current;

import Demo.ChatCallbackPrx;
import Demo.ChatRoom;

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
}