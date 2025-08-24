# youcaryourway


A WebSocket real-time chat Proof of Concept for *Your Car Your Way*, using Angular and Spring Boot.

### Clone the project :
```bash
git clone https://github.com/adThomDev/youcaryourway.git
```
## Angular frontend

Navigate to the front/chat_frontend folder :
```bash
cd front/chat_frontend
```

Install dependencies :
```bash
npm install
```

Launch the frontend :
```bash
npm run start
```

The app should then be available at `http://localhost:4200/`

## Spring Boot backend
Navigate to the back folder :
```bash
cd back
```

Install dependencies :
```bash
mvn clean install
```

Setup the database (there is a MySQL script in the docs folder) and its credentials (see application.properties). 

Launch the Backend :
```bash
mvn spring-boot:run
```