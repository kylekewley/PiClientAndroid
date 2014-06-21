PiClientAndroid
===============

This is the Android version of the client library used to connect to the PiServer on the Raspberry Pi

In order to use the project, you will need to familiarize yourself with two classes: PiClient and PiMessage.

##Example
----------
The PiClient class is used to connect to a PiServer. It is recomended that you implement the PiClientCallbacks interface in one of your MainActivity in order to update the user with server status updates. Here is an example of connecting a PiClient object to a PiServer.

```java

//Create a PiClient that connects to the local machine on port 10002. 
//The third parameter is for the PiClientsCallbacks object
PiClient piClient = new PiClient("localhost", 10002, this);

//Now create a "Ping" object defined by the Google Protocol Buffer Ping class
PingProto.Ping ping = PingProto.Ping.newBuilder().setMessage("Hello World").build();

//Create a pingMessage that will be parsed on the server with the parserID 1
PiMessage pingMessage = new PiMessage(1, ping);

//Use the PiMessageCallbacks abstract class to set the pingMessageCallbacks
message2.setMessageCallbacks(new PiMessageCallbacks(PingProto.Ping.newBuilder()) {
  @Override
  public void serverReturnedData(byte[] data, PiMessage message) {
  
  }

  @Override
  public void serverRepliedWithMessage(MessageLite response, PiMessage sentMessage) {
      PingProto.Ping ping = (PingProto.Ping)response;
      
      System.out.println("Server parsed message: " + ping.getMessage());
      }

  @Override
  public void serverSuccessfullyParsedMessage(PiMessage message) {
    System.out.println("Server parsed message and gave no response");
  }

  @Override
  public void serverReturnedErrorForMessage(ParseErrorProto.ParseError parseError, PiMessage message) {
      System.out.println("Server had trouble parsing message");
  }
});

```

Notice that the PiMessageCallbacks constructor was called with a Protocol Buffer Builder as a parameter. This enables the client to parse the data into a message rather than just returning data. 
