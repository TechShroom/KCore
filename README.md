A common core for most of TechShroom's programs. This is the branch of KCore used for Java 8.

Please note that Java 8 is incomatible with the Arrays package, and you will need to use the jre8 branch/distribution in order to use it in Java 8.

Get distributed pre-compiled jars over at [TechShroom](http://techshroom.com)

Parts of KCore (distributed):
* Arduino
    * Interfacing with RXTX library
    * Depends: None
* Arrays
    * Different common array functions
    * Depends: Classes, Core, Reflect
* Classes
    * Some common class functions
    * Depends: None
* Core
    * Different 'core' functions, some will be refactored out to other parts
    * Depends: None
* Gui
    * Some nifty Swing based GUI interfacing
    * Console
    * Multi-action listener
    * Special PrintStreams
    * Depends: None
* Jar
    * Jar file enhancements
    * Depends: None
* Jython
    * Jython backend interface
    * Depends: None
* Math
    * Unlimited numbers and expression parsing
    * Depends: Arrays, Core, Gui, Strings
* Netty
    * Networking
    * Depends: Core, Timing
* Reflect
    * Reflection ease of use
    * Depends: Classes, Translate
* Strings
    * Different string functions
    * Depends: None
* Timing
    * Low resolution timer
    * Depends: None
* Translate
    * Translations! Choose different languages, add languages.
    * Depends: None
* XML
    * Easy XML parsing
    * Depends: None
