
# 🚦 Smart City Traffic System

A self-built navigation system that offers route suggestions, calculates distances, and estimates travel time based on traffic data — inspired by the functionality of Google Maps.

## 📌 Project Motivation

Why build a navigation app when Google Maps exists?

Because the real challenge wasn’t to compete—it was to learn, build, and optimize something complex from scratch. With limited access to premium APIs, this project became a deep dive into the world of geolocation, route algorithms, and performance tuning.

## 📌 Project Overview

The Smart City Traffic System integrates various components to monitor and control traffic dynamics:

- **📍Custom route suggestions between two points**

- **🌐 Free APIs used for geolocation and routing**

- **⛽ Traffic-aware ETA calculation**

- **📏 **Distance calculation using the Haversine formula**

- **⚡ Optimized performance with multithreading, reducing response time from 30s to ~10s**


## 🛠️ Technologies Used

- **Java**: Core programming language.
- **Spring Boot**: Framework for building the backend services.
- **Spring MVC**: For handling HTTP requests and responses.
- **Spring Data JPA**: For database interactions.
- **PostgreSQL**: DataBase 
- **Maven**: Build and dependency management tool.
- **APIs Used**:
```
Geolocation API (for latitude/longitude)

Routing API (for step-by-step path)

Traffic Data API (for live traffic-based ETA)
```
## ⚙️ Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- Maven 3.6 or higher

### Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/rishabhrawat05/SmartCity_TrafficSystem.git
   cd SmartCity_TrafficSystem
   ```

2. **Build the project**:

   ```bash
   mvn clean install
   ```

3. **Run the application**:

   ```bash
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`.


## 📁 Project Structure

```
SmartCity_TrafficSystem/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── smartcity/
│   │   │           └── trafficsystem/
│   │   │               ├── controller/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   └── test/
│       └── java/
├── pom.xml
└── README.md
```

## 📊 Future Enhancements

- **Integration with Real-time Data Sources**: Incorporate live traffic data from APIs.
- **Machine Learning for Predictive Analysis**: Predict traffic patterns and suggest proactive measures.
- **Mobile Application**: Develop a companion app for commuters.

## 🤝 Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any enhancements or bug fixes.


**Made with dedication to smarter urban mobility.**
