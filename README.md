# Conway's Game of Life

[Conway's Game of Life](https://en.wikipedia.org/wiki/Conway's_Game_of_Life) is a famous zero-player game.

My implementation includes all of the basic rules. The crux of this project is implementing functionalities such as Save/Load, Copy/Paste, etc.
This project is build in Java Swing. It uses a series of "wrappers", with a Runnable wrapping a Board that wraps a Model.
This project was worked on over my winter break in freshman year of college.

### Planned Functionalities
- Save to File w/ Multiple Save Files
- Save between Open/Close of program
- Load to clipboard from file
- Load to clipboard from text entry in "." / "0" formatting
- Implement transform features such as rotate and reverse
- ~~Adding ability to change rules~~ **Done**

### Planned Changes
- Rework Manual/Copy/Paste modes to one mode that interprets mouse behavior
- Implement keybinds for Start/Stop
- Automatic stability detection
