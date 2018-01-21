# Android application
This application is the client tool for this CPS2 project.

## Sections
### Objects
This section shows the list of the objects that are registered in MongoDB.
You can click on an item to display its details: you will see its general information such as its location and when it was last updated. There are also the description of its configuration parameters and its fields that you can access by using the dropdown menu.

### Query
This section allows you to change or query a configuration parameter or a field.
You can chose precisely your targets, i.e. what group of objects you want to change or request the value. Be careful, for the moment the system does not check the consistency of the group. As an example, if you select "Meteo Station" for the type and "EF" for the building, if there is no Meteo Station in the building EF your message will be lost since no object has subscribed to this kind of topic. In the future the application could query MongoDB after each choice in a dropdown menu so that it sends only messages for which there is a subscriber.

### Scenario
This section allows you to activate a scenario stored in MongoDB.
You can select the name of a scenario in the dropdown menu. This will display the description of the scenario so that you can check what it will do. Then just press the button "Activate" to send your command. The message will be received by the server which will get the corresponding scenario in MongoDB and send its new state to each object of the scenario.

### Help
Shows this help page.
