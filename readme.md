
#Before first time build
##Java Version
I can guarantee that Java 14 works as that is the version I used myself.
A minimum of Java 11 is required for the program to work correctly because HttpClient was used which requires Java 9+
If you want to use a different version of java, you might have to change the version in some configs.

##Maven
Do not forget to download the required maven files if you need to. I use IntelliJ which lets you click a button in the
top right hand corner when you move into the pom.xml file. 


## Potential Build Errors
If you get an error about something not opening a file or package or anything similar, you add/change a couple of lines in module-info.java


#Code Structure
The code loosely adheres to the MVC principles but does not fully implement them

##Components
Components are in /src/java/org/alp/controllers \
Components represent the logic of any view \
Here onClick, onLoad etc events are located.

## Models
Components are in /src/java/org/alp/models
Models are the POJOs. They represent any type of stored data in the program. \
API Responses, Objects in tables etc \


## Services
Services are in /src/java/org/alp/services \
Services are what does the business logic and what communicates with any backend Api such as the crossref API \
CrossRefService communicated with the Crossref Api, CssReader reads Css files for GraphStream, etc

##Views or FXML
Views are located in /src/main/resources/org/alp/fxml \
Views are what is shown on the screen \
Currently only the homepage and searchResults pages exist

# General Usage
1) On the main screen enter in a keyword that you would like to find papers with.
2) After a small delay, you will be shown 20 results from the Crossref API.
3) In the table select any paper that has more than 0 references.
4) Click the button on the right.
5) After the code has finished getting all references up to a depth of 2, a citation graph will be shown.

