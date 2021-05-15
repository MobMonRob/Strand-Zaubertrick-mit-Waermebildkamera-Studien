# Java-Server

This server-implementation provides 2 ways of image-inpainting:

1. Copying data from a previous frame
2. Using algorithms provided by opencv

It also acts as library containing the required code to compress and send the frames from a client to the server.

# How to use

### Library

To use this project as library e.g. for the android app you have to compile it with maven.
Simply run `mvn clean install` to install the package to you local `maven` repository.

### Run server

#### Prepare 

You need to install opencv on your system.
Make sure the installed version includes the java bindings to use it.

Then you have to add the option `-Djava.library.path` of the JVM to the installation path of the opencv java bindings.
In intelliJ you could set these options in the run configurations.

#### Run

The easiest way to start a server is to execute the `main`-method of the implementation you want directly in the IDE.

#### Configuration 

Most configuration is done directly in the code by using some `private static final` variables.

To enable capturing the raw data of each frame you could add `record <folder/prefix>` as arguments.
This will create a file you could use as input for the `ReplayClient` for debugging.
To create some custom frames for debugging you could adjust the code of in the `Client` class. 
