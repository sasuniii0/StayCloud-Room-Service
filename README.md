# room-service

> **Student Name:** Sasuni Jinethma Wijerathne
> **Student Number:** 241711085
> **Slack Handle:** sasuni wijerathne
> **GCP Project ID:** `staycloud`

## Project Description

Manages the hotel's room catalog: room types, pricing, amenities, and
availability, backed by **MongoDB**. Room images are uploaded straight to a
**Google Cloud Storage bucket** and the resulting public URLs are stored on
the room document — this is the module's Cloud Storage requirement
implementation.

## Technology Stack

- Java 25, Spring Boot 4.1.0
- Spring Data MongoDB
- Spring Cloud Config Client + Eureka Client
- `google-cloud-storage` client library (Application Default Credentials —
  uses the VM's attached Service Account on GCP, or
  `GOOGLE_APPLICATION_CREDENTIALS` locally)
- Bean Validation, Lombok

## API

| Method | Path | Description |
|---|---|---|
| GET | `/api/rooms` | List all rooms (`?available=true` to filter) |
| GET | `/api/rooms/{id}` | Get one room |
| POST | `/api/rooms` | Create a room |
| PUT | `/api/rooms/{id}` | Update a room |
| PATCH | `/api/rooms/{id}/availability` | Toggle availability (`{"available": true\|false}`) |
| DELETE | `/api/rooms/{id}` | Delete a room |
| POST | `/api/rooms/{id}/images` | Upload an image (`multipart/form-data`, field `file`) to Cloud Storage |

## Setup / Getting Started

Start `config-server` and `eureka-server` first (see the platform repo),
have a MongoDB instance available, then:

```bash
export MONGODB_URI=mongodb://localhost:27017/hotel_rooms
export GCS_BUCKET_NAME=hotel-room-images
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account.json   # local only
./mvnw spring-boot:run
```

Runs on **port 8081** by default.

### Build & run the jar (as used by PM2 in production)

```bash
./mvnw clean package -DskipTests
java -jar target/room-service-0.0.1-SNAPSHOT.jar
```

### Key environment variables

| Variable | Purpose | Default |
|---|---|---|
| `CONFIG_SERVER_URL` | Where to pull config from | `http://localhost:8888` |
| `EUREKA_URI` | Eureka `/eureka` endpoint | `http://localhost:8761/eureka` |
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/hotel_rooms` |
| `GCS_BUCKET_NAME` | Cloud Storage bucket for room images | `hotel-room-images` |
| `GCP_PROJECT_ID` | GCP project owning the bucket | *(empty — inferred from ADC)* |
