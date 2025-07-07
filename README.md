# 🚀 Galaxy Trucker

### Final Project (Software Engineering), Academic Year 2024–2025 @ polimi

This project is a Java-based video game developed as the final project for the Software Engineering course at Polimi. The goal was to implement a digital version of the board game Galaxy Trucker!
The project structure is based on the Model-View-Controller (MVC) design pattern, and the graphical interface is built using JavaFX.
Maven has been a game-changer for us, allowing us to focus on development without worrying about dependency management.
The repository contains the full source code (including unit tests), JavaDoc documentation, a pre-compiled JAR file, and some diagrams illustrating how the client-server communication works!

**Project graded: 30L/30L** ✨

---

## ✅ Features Implemented

| Feature                       | Status |
|-------------------------------|--------|
| Complete game rules           | ✅      |
| TUI (Text-based UI)           | ✅      |
| GUI (Graphical UI)            | ✅      |
| Socket support                | ✅      |
| RMI support                   | ✅      |
| Trial flight                  | ✅      |
| Server handles multiple games at once| ✅      |
| Disconnection resilience      | ✅      |
| Server-side persistence       | ❌      |

---

## 🚀 How to Run the JAR Files

All compiled `.jar` files can be found in the directory:
```
GalaxyTrucker/deliverables/final/jar
```

### 🛰️ Launching the Server

To start the server:

1. Open a terminal.
2. Navigate to the folder containing `GalaxyTrucker-Server.jar`.
3. Run:
```bash
$ java -jar GalaxyTrucker-Server.jar
```

### 🧑‍🚀 Launching the Client

To start a client, use the same procedure, but with two additional arguments:

- The first specifies the connection type:
  - `--socket` for TCP socket communication.
  - `--rmi` for RMI (Remote Method Invocation).

- The second specifies the interface type:
  - `--tui` for the terminal interface.
  - `--gui` for the graphical interface.

#### Example:

If you want to launch a client using RMI and the text interface:
```bash
$ java -jar GalaxyTrucker-Client.jar --rmi --tui
```

---

## 🎨 Graphic resources

Due to copyright restrictions, we are unable to provide the original graphic resources. However, you can still enjoy a fully functional experience using the pre-compiled JAR.

If you happen to obtain the graphical assets, you can place them in the appropriate directory after opening the project with IntelliJ IDEA. Simply insert the resources at
```
GalaxyTrucker/src/main/resources/it/polimi/ingsw
```
and enjoy your game!

---


