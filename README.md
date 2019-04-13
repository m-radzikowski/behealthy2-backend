# BACKEND

Backend for mood-sentiment measure plugin.

## Setup

Using Google Speech-To-Text requires credentials - json is required to pass to app.

To start up start services:
- Eureka
- Zuul
- Text, Audio, WitAI

## Services

### service-eureka
Keeps status of every service. All services must register itself here as well as ping eureka to notify it's alive. 

It stores all names of services so we do not need to know their addresses.

### service-zuul
Proxy, gateway. It's the layer between user and all other services.

It transfers request to other services using names from eureka service.

### service-text
Service returning value (positive, negative) for specified string input. 

To obtain results it communicates with wit.ai.

### service-audio
Service returning value (positive, negative) for specified audio input. 

To obtain results first it translates audio to text using Google Cloud Speech-Text API, then it sends string data to text-service and returns results.

## Tech Stack

- Spring Boot
- Netflix Zuul
- Netflix Eureka
- Google Cloud (Speech-To-Text)
- Python