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
        void sendMessageBC(string s);
        void sendMessage(string s, string receptor);
        string leave(string username);
    }
}
