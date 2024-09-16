# Selenium with Google Calendar API 🚀

This project demonstrates how to integrate Selenium with Google Calendar API in Java. It is extended for personal use to automate the process of logging into a web portal to acquire upcoming shifts and creating Google Calendar events based on the scraped shift information.

## Features

- Log in to the QSS web portal to acquire upcoming shifts.
- Scrape information about the shifts and create Google Calendar events using Google APIs.

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/your-username/your-repository.git
    ```
2. Navigate to the project directory:
    ```bash
    cd your-repository
    ```
3. Install the required dependencies. You can use Maven or Gradle:
    ```bash
    mvn install
    ```
    or
    ```bash
    gradle build
    ```

4. Add your `credentials.json` file to the root directory. You can obtain this file from the Google Developers Console.

5. Configure your `config.properties` file with the necessary details like the website URL, username, and password.

## Usage

To run the project, execute the main class:
```bash
java -cp target/your-project.jar com.yourpackage.CalendarQuickstart
```

or

```bash
gradle clean
gradle build
gradle run
```