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
    public void sendMessageBC(String message, Current current) {
        userManager.broadcastMessage(message);
    }

    @Override
    public void sendMessage(String message, String receptor, Current current) {
        userManager.sendMessageToUser(receptor, message);
    }

    @Override
    public String leave(String username, Current current) {
        String result = userManager.removeChatter(username);
        System.out.println(result);
        return result;
    }
}