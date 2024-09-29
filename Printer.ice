module Demo
{
    class Response{
        long responseTime;
        string value;
    }
    interface ChatCallback{
            void receiveMessage(string message);
    }
    interface ChatRoom
    {
        string join(string username, ChatCallback* callback);
        string listUsernames();
        void sendMessageBC(string sender, string s);
        void sendMessage(string sender, string s, string receptor);
        string leave(string username);
    }
}
