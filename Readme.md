# Basic GRPC Chat Example

## Project Structure

**contract** - defines contract, this is where .proto file should be added
**sender** - defines a client of the chat service that subscribes to messages and sends some test messages, this is where to add your client code
**receiver** - chat server that receives and broadcasts chat messages, this is where to add your service code

## Building

The gradle protobuf plugin is used to generate the various stubs and services. Once you have added the .proto, to generate the code execute

```shell
./gradlew generateProto
```

## Running

To run the server, define your main class as SenderMain (or update the class name in build.gradle) in the receiver module. This starts a server on localhost at port 8980.

To run the client, define your main class as SenderMain in the sender module (or update the class name in build.gradle). This conects to the server on localhost:8980 and sends several chat 
messages as well as printing the responses.
