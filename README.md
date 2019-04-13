# BACKEND

## Services

### service-eureka
Keeps status of every service. All services must register itself here as well as ping eureka to notify it's alive. 

It stores all names of services so we do not need to know their addresses.

### service-zuul
Proxy, gateway. It's the layer between user and all other services.

It transfers request to other services using names from eureka service.

### service-text
Service returning char sequence.

## Tech Stack

- Spring Boot
- Netflix Zuul
- Netflix Eureka