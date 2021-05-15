# Android App

This App was designed to be used on the CAT S60. It will capture the required frames using the FLIR-ONE-SDK and send them to one of the servers afterwards.

## How to use

### Requirements

#### FLIR-ONE-SDK

You need to install the FLIR-ONE-SDK according to [this](https://developer.flir.com/getting-started/android-platform-guide-flir-one-cat-s60/) guide.
To get the SDK you have to register at the FLIR developer program.
Keep in mind that not all versions support the CAT S60.
So you will need to use an older version.

#### Client-Library

This app relies on the client library provided by the Java-Sever project which includes the code for compressing and sending frames.
Therefore you have to build this project before you can compile the app.
Make sure the package will be installed correctly to your local `maven` repository in order that gradle can find it.

### Run

To install the app enable the developer-mode on the smartphone and connect it to your pc.
Then install the app via android-studio.

After starting the app you have to configure the ip address and the port of the server application.

By default some times in the processing pipeline are measured when debugging is enabled in `BuildConfig`.