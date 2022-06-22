
# Prova Finale di Ingegneria del Software 2022

The "Prova Finale di Ingegneria del Software 2022" is the final test of the **Software Engineering** course at Politecnico di Milano.<br>

**Teacher**: Gianpaolo Cugola<br>
**Group**: GC42<br>
**AA.** 2021/22

## Eriantys

<img src="https://cf.geekdo-images.com/DzhJxVjMhGQadReXJmbIaQ__opengraph/img/Oy3Kztkx4fXouT2jpAiXoZRAR4Q=/fit-in/1200x630/filters:strip_icc()/pic6253341.jpg" align="right" width=220px>

The goal of the project is to program a Java version of the board game [Eriantys](https://craniointernational.com/products/eriantys/), by Cranio Creations.

The project includes:

- Initial UML diagram
- Final UML diagram, generated from the code by automated tools
- Working game implementation
- Source code of the implementation
- Source code of unit tests

## Group members
- [Arturo Benedetti](https://github.com/benedart) - 10747545<br>arturo.benedetti@mail.polimi.it
- [Luca Romanò](https://github.com/LucaRomano2) - 10656514<br>luca8.romano@mail.polimi.it
- [Lorenzo Rossi](https://github.com/tpoppo) - 10698834<br>lorenzo17.rossi@mail.polimi.it

## Implemented functionalities

| Functionality  | State |
|:---------------|:-----:|
| Basic rules    |  🟢   |
| Complete rules |  🟢   |
| Socket         |  🟢   |
| CLI            |  🟢   |
| GUI            |  🟢   |
| All Characters |  🟢   |
| Multiple games |  🟢   |
| Persistence    |  🟢   |

### Legend
🟢 Implemented<br>
🟡 In progress<br>
🔴 Not implemented<br>

## Software used
- **AstahUML**: UML Diagrams
- **IntelliJ IDEA Ultimate**: Main IDE
- **SonarQube**: Code analysis

## Quick start guide
There is a unique jar file for both the client and the server. Therefore, you can select which mode you want to use in the arguments.
### For the client
It is possible to set the IP and the port you want to connect to by using the optional argument port and IP.
##### For the cli
In the terminal, run:
```bash
java -jar Eriantys.jar cli [port <port number>] [ip <ip address>]
```
##### For the gui
In the terminal, run:
```bash
java -jar Eriantys.jar gui [port <port number>] [ip <ip address>]
```
<img src=".github/assets/game_gui.png">


### For the sever
It is possible to set the port you want to use.
In the terminal, run:
```bash
java -jar Eriantys.jar server [port <port number>]
```
