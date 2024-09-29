import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public class Server{
    public static void main(String[] args) {
        int status = 0;
        ChatRoomI object = null;
        try(Communicator communicator = Util.initialize(args, "server.cfg")){
            ObjectAdapter adapter = communicator.createObjectAdapter("ChatRoom");
            object = new ChatRoomI();
            adapter.add(object, Util.stringToIdentity("Server"));
            adapter.activate();
            System.out.println("Server started...");
            communicator.waitForShutdown();
        }catch(Exception e){
            e.printStackTrace();
            status = 1;
        } finally {
            if(object != null) object.shutdown();
        } System.exit(status);
    }
}