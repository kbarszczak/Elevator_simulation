![banner](https://github.com/kbarszczak/Elevator_simulation/assets/72699445/65a8e6f7-bb03-4945-b55f-f6ab9d452a80)

The simulation of the elevator system that can handle up to 16 elevators.

## Build status

The system is implemented and tested. Currently, the queuing algorithm is the SCAN algorithm described [here](https://en.wikipedia.org/wiki/Elevator_algorithm)
The main idea of this algorithm is to travel in the current direction as long as possible without making a turn. The direction is changed when the last request in the current direction has been served. 

## Screenshots

The below screenshot presents the application layout

![elev_sim](https://github.com/kbarszczak/Elevator_simulation/assets/72699445/12a888d1-7eef-4ad8-8176-9669b968fbde)

The UML class diagram is presented below

![elevator_simulation_uml](https://github.com/kbarszczak/Elevator_simulation/assets/72699445/aaad7440-0f87-45b5-adcb-6ac43a7dde84)

## Tech/Framework used

Project uses:
- Java 17
- Spring Boot 3.1.0
- JavaScript
- HTML
- CSS

## Features

Main features of the application
- The application architecture allows for changing the queueing algorithm easily and during the runtime without any issues. 
- The elevator count is limitless if the fake limitation is deleted.

## Installation

1. The first step is to either clone the project
```
mkdir simulation
cd simulation
git clone https://github.com/kbarszczak/Elevator_simulation .
```
or use the git .bundle
```
// kamil_barszczak.bundle has to be in the same directory
git clone kamil_barszczak.bundle
cd kamil_barszczak
git checkout main
```
2. The next step is to build jar by executing the following commands
```
cd ElevatorSimulationBackend
mvn clean install
```
3. Then prepare Docker image
```
docker build -t elevator-simulation .
```
4. After this run created image (since now the backend service is accessible [here](http://localhost:10000/simulation/elevator))
```
docker run -d -p 10000:10000 --name elevator-simulation elevator-simulation
```
5. Now we have to set up frontend service
```
cd ../ElevatorSimulationFrontend
npm install http-server -g
http-server -p 10001
```
6. From now on everything is set up and should work properly [here](http://localhost:10001/)



