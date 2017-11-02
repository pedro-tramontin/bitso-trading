# Bitso Trading
JavaFX application that connects to Bitso cryptocurrency exchange, lists the top bids, asks and trades and simulates a contrarian trading strategy.

## Features
* Synchronizes via public REST and WebSocket APIs;
* Runtime configuration of the top number of asks/bids/trades to display;
* Can simulates a contrarian trading strategy;
* Integrated with Spring Boot;

## Requirements
* Gradle 2.10 and above (works on Gradle 3 too)
* Java Developer Kit 8 with at least Update 60

## Running
It's possible to run the application using the gradle wrapper from the Linux or Mac command line:

    ./gradlew run

In Windows:

    gradlew.bat run

## Deploying
To deploy the application you execute:

    ./gradlew build

This command will produce two files in bitso-trading-javafx/build/distributions, a zip and a tar files that can be used to distribute the compiled version of this project.

To run the distributed version, you first decompress the file to a directory and then you can execute bitso-trading-javafx in Linux and Mac or bitso-trading-javafx.bat in Windows.

It will be necessary to have Java Runtime 8u60 at least installed at the machine the project is distributed.

## Logging
By default, the project has INFO log level enabled, with just some logs being shown on the command line.

To enable more detailed logs, you need to change log leve in the application.properties files.

To see the connection details, you can edit the file:

    bitso-trading-core/src/main/resource/config/application.properties

and change the following line to:

    logging.level.br.com.pedront.bitsotrading.core.client.api.bitso=TRACE

To see the application details, like parsing the messages, you can edit the file:

    bitso-trading-javafx/src/main/resources/config/application.properties
    
and change the following line to:

    logging.level.br.com.pedront.bitsotrading=TRACE

Just need to point out that once the TRACE level is enabled, a lot of logging will appear on the command line.

## Improvements
* Create a custom ObservableList that don't need to reset the predicate to filter top N itens;
* Internationalize the string;

## Checklist

| Feature | File name | Method name |
|:-------:|:---------:|:-----------:|
| Schedule the polling of trades over REST.| DashboardController.java | configServices |
| Request a book snapshot over REST. | OrderService.java | orderBook |
| Listen for diff-orders over websocket. | BitsoWebSocketService.java | onMessage |
| Replay diff-orders after book snapshot. | DashboardController.java | applyDiffOrderData |
| Use config option X to request recent trades. | TradeService.java | createTask |
| Use config option X to limit number of ASKs displayed in UI. | DashboardController.java | configLists |
| The loop that causes the trading algorithm to reevaluate. | DashboardController.java | generateSimulatedData |
