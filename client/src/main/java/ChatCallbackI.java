import Demo.ChatCallback;
import com.zeroc.Ice.Current;

public class ChatCallbackI implements ChatCallback {
    @Override
    public void receiveMessage(String message, Current current) {
        System.out.println("Received message: " + message);
    }
}
