# Tradify — Backend

**AI-Powered E-Commerce & Product Management Platform**

Tradify is an AI-integrated e-commerce MVP built with Java, Spring Boot, PostgreSQL, and AWS. The backend provides RESTful APIs for product management, image uploads, and checkout operations, while integrating generative AI to automatically generate product titles and descriptions from uploaded product images.

## Tech Stack

| Category | Technologies |
|---|---|
| Language | Java |
| Backend | Spring Boot, Spring Data JPA |
| Database | PostgreSQL, AWS RDS |
| Database Migrations | Flyway |
| Infrastructure | AWS ECS, Docker |
| Image Storage | Cloudinary |
| AI | Generative AI Integration |
| Performance Testing | k6 |
| Frontend | Next.js, TypeScript |

## Key Features

- **Product Management:** RESTful CRUD APIs for creating, retrieving, updating, and deleting products.
- **AI-Powered Product Generation:** Automatically generates product titles and descriptions from uploaded product images using generative AI.
- **Image Uploads:** Cloudinary integration for uploading and storing product images.
- **Checkout:** Backend checkout functionality designed for concurrent users.
- **Database Management:** PostgreSQL persistence with version-controlled Flyway migrations.
- **Cloud Deployment:** Containerized backend services deployed using AWS ECS and RDS.
- **Performance:** Achieved 250 ms p95 response latency under k6 load testing with 20+ concurrent users.

## Architecture

```text
                  Next.js Frontend
                         |
                         v
                    REST API
                         |
                         v
                  Spring Boot Backend
                         |
          +--------------+--------------+
          |              |              |
          v              v              v
    Product Service   AI Integration   Checkout
          |              |              |
          v              v              |
     PostgreSQL     Generative AI       |
      AWS RDS        Integration        |
          |                             |
          +-----------------------------+

    Product Image Upload
             |
             v
         Cloudinary
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/v1/products` | Retrieve products |
| POST | `/api/v1/products` | Create a product |
| PUT | `/api/v1/products/{id}` | Update a product |
| DELETE | `/api/v1/products/{id}` | Delete a product |
| POST | `/api/v1/products/upload` | Upload a product image |

## Performance

The backend was tested using **k6** to evaluate response latency and concurrent request handling.

| Metric | Result |
|---|---|
| p95 Response Latency | 250 ms |
| Concurrent Users | 20+ |
| Target Request Volume | 10,000 requests/day |
| Load Testing | k6 |

## Getting Started

### Prerequisites

- Java JDK
- Docker & Docker Compose
- PostgreSQL
- Maven
- Cloudinary account

### Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO.git
cd YOUR_REPO
```

### Configure Environment

Create the required environment variables for database connectivity and external integrations.

```env
DB_URL=jdbc:postgresql://localhost:5432/jfs
DB_USERNAME=postgres
DB_PASSWORD=your_password

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

Adjust the variable names to match your Spring Boot configuration. Never commit real credentials.

### Run the Application

Start PostgreSQL using your Docker Compose configuration:

```bash
docker compose up -d
```

Start the Spring Boot backend:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Flyway automatically applies database migrations during application startup when configured.

## Project Structure

The backend follows a layered architecture separating API controllers, business logic, persistence, and integrations.

```text
src/main/
├── java/
│   └── .../
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       └── config/
└── resources/
    ├── application.properties
    └── db/
        └── migration/
```

*Directory names are illustrative and should be aligned with the repository's actual structure.*

## Roadmap

- [x] Product management REST APIs
- [x] PostgreSQL persistence
- [x] Flyway database migrations
- [x] Cloudinary image uploads
- [x] Checkout MVP
- [x] Backend load testing with k6
- [ ] Expand AI-assisted product generation
- [ ] Improve monitoring and observability
- [ ] Expand automated integration testing

## Related Repository

**Tradify Frontend** — Next.js, TypeScript, React, and Tailwind CSS.

## Author

**Saim Naseem**

Software Engineer | Backend & Cloud | AI Systems

[Portfolio](https://builtbysaim.com) · [GitHub](https://github.com/SaimNaseem) · [LinkedIn](https://www.linkedin.com/in/saim-naseem/)
