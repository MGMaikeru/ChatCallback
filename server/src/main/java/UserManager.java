import java.awt.desktop.UserSessionEvent;
import java.util.Collection;

import Demo.ChatCallbackPrx;

import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private final ConcurrentHashMap<String, ChatCallbackPrx> chatters;

    public UserManager(){
        this.chatters = new ConcurrentHashMap<String, ChatCallbackPrx>();
    }

    public String addChatter(String username, ChatCallbackPrx callback){
        if(!chatters.containsKey(username)){
            chatters.put(username, callback);
            return "User added:" + username + " With proxy: " + callback;
        }
        return "Username already taken:" + username;
    }

    public String removeChatter(String username){
        if(chatters.containsKey(username)){
            chatters.remove(username);
            return "User removed:" + username;
        }
        return "Username not found:" + username;
    }

    public String getChattersNames(){
        String usernames = "";
        for (String username : chatters.keySet()){
            usernames = username + "\n";
        }
        return usernames;
    }

    public void broadcastMessage(String sender, String message){
        Collection<ChatCallbackPrx> callbacks = chatters.values();
        for (ChatCallbackPrx callback : callbacks){
            try {
                callback.receiveMessage(sender + ": " + message);
            } catch (Exception e) {
                System.err.println("Failed to send message to one of the users" + e.getMessage());
            }
        }
    }

    public void sendMessageToUser(String sender, String receptor, String message){
        ChatCallbackPrx callback = chatters.get(receptor);
        if (callback != null) {
            callback.receiveMessage(sender + " [PRIVATE]: " + message);
        } else {
            System.out.println("User " + receptor + " not found!");
        }
    }
}