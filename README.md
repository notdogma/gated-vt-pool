# Gated VT Pool

A high-performance, event-driven polling service framework built with Java 21.

## Overview

Gated VT Pool provides a configurable polling service that can be used to monitor and process events from various sources. The service is designed to be highly configurable and scalable, making it suitable for a variety of use cases that require periodic checking or event processing.

## Features

- **Event-driven architecture** for efficient task processing
- **Configurable polling intervals** to control resource usage
- **Thread-safe implementation** for concurrent operations
- **Extensible design** allowing custom task implementations
- **Built with Java 21** for modern language features and performance

## Prerequisites

- Java 21 or later
- Maven 3.6.0 or later

## Getting Started

### Building the Project

```bash
mvn clean install
```

### Running Tests

```bash
mvn test
```

## Project Structure

```
src/
├── main/java/com/esp/poller/
│   ├── executor/     # Task execution components
│   ├── logger/       # Logging utilities
│   ├── model/        # Core data models
│   ├── ruleCache/    # Caching mechanisms
│   ├── tasks/        # Task implementations
│   ├── PollerServiceSim.java  # Main polling service implementation
└── test/             # Test files
```

## Usage

```java
// Create a new poller service with a 10-second interval
PollerServiceSim pollerServiceSim = new PollerServiceSim(10000);

// Start the poller
pollerServiceSim.start();

// When done, stop the poller
pollerServiceSim.stop();
```

## Dependencies

- **Lombok** - For reducing boilerplate code
- **JUnit 5** - For unit testing

## License

This project is licensed under the terms of the [LICENSE](LICENSE) file.
