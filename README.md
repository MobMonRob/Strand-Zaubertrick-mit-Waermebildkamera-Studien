# Strand-Zaubertrick-mit-Waermebildkamera-Studien

This project consists of 4 subprojects representing individual components of the system.
The system is based on a client-server architecture where the client sends some frames, and the server tries to remove all people based on their temperature.   

## [Android App](https://github.com/MobMonRob/Strand-Zaubertrick-mit-Waermebildkamera-Studien/tree/main/app)

This App was designed to be used on the CAT S60.
It will capture the required frames using the FLIR-ONE-SDK and send them to one of the servers afterwards.

More information on how to use it can be taken form the README.md inside the subproject.

## [Java-Server](https://github.com/MobMonRob/Strand-Zaubertrick-mit-Waermebildkamera-Studien/tree/main/server)

This server-implementation provides 2 ways of image-inpainting:

1. Copying data from a previous frame
2. Using algorithms provided by opencv

It also acts as library containing the required code to compress and send the frames from a client to the server.

More information on how to use it can be taken form the README.md inside the subproject.

## [Python-Server](https://github.com/MobMonRob/Strand-Zaubertrick-mit-Waermebildkamera-Studien/tree/main/python-server)

This server-implementation was designed as python-package containing all the required code to receive frames from the client.
To implement a new server you have to create a custom `ImageProcessor` implementing the corresponding interface.

It also provides a reference implementation using opencv which works just like the java-version.

More information on how to use it can be taken form the README.md inside the subproject.

## [Python-AI-Server](https://github.com/MobMonRob/Strand-Zaubertrick-mit-Waermebildkamera-Studien/tree/main/python-ai-server)

This server is an implementation of the `Python-Server` using an ai-based approach for the `ImageProcessor`.

The code of the ai was taken from the project [Generative Image Inpainting](https://github.com/JiahuiYu/generative_inpainting).
As this project was not designed to be used as python-package it was easier to copy the code.
Therefore the Licence for this part will differ from the rest of the project.

More information on how to use it can be taken form the README.md inside the subproject.