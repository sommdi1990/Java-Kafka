# Java Kafka Microservices

---
**🚨 پیش‌فرض‌های ورود و رمزها (Credentials):**

### 🟢 ادمین و سرویس‌ها
- **Spring Boot Admin / Gateway UI**
  - **Username:** `admin`
  - **Password:** `admin123`
- **User (نمونه):**
  - **Username:** `user`
  - **Password:** `user123`

### 🔒 دیتابیس MySQL (برای هر سرویس)**
| Service              | Database    | Username        | Password        |
|----------------------|-------------|-----------------|-----------------|
| spring-boot-admin    | admin_db    | admin_user      | admin_password  |
| cbi-service          | cbi_db      | cbi_user        | cbi_password    |
| schedule-service     | schedule_db | schedule_user   | schedule_password|
| workflow-service     | workflow_db | workflow_user   | workflow_password|
| gateway-service      | gateway_db  | gateway_user    | gateway_password|
| kafka-manager        | kafka_db    | kafka_user      | kafka_password  |

### 📊 مانیتورینگ و مشاهده (Monitoring)**

- **Grafana**
    - **Username:** `admin`
    - **Password:** `admin123`
    - **URL:** http://localhost:9091
- **Prometheus**
    - **URL:** http://localhost:9090
    - **دسترسی:** بدون نیاز به ورود

### ⚠️ نکات امنیتی:
- رمز عبورها بعد از راه‌اندازی حتما تغییر داده شود.
- برای محیط واقعی از رمزهای قوی و یکتا استفاده کنید!
- برای JWT و تنظیمات امنیتی، متغیرهای محیطی را به صورت اختصاصی مقداردهی نمایید.

---

## 🚀 Quick Start

```bash
# Clone the repository
git clone <repository-url>
cd Java-Kafka

# Start all services with Docker
docker-compose up -d

# Access the application
open http://localhost:3000
```

## 📋 Features

- **7 Microservices** with GraalVM Native Image support
- **Spring Boot Admin** for centralized monitoring
- **CBI Service** with WSDL and external API integration
- **Schedule Service** with Spring Batch and virtual threads
- **Workflow Service** for process orchestration
- **Gateway Service** with Spring Security authentication
- **Kafka Manager** for cluster monitoring
- **TypeScript UI** with drag-and-drop workflow designer
- **AOP Logging** across all services
- **Multi-database** MySQL setup with Flyway migrations
- **Prometheus** for metrics collection
- **Grafana** for monitoring dashboards
- **Infrastructure add-ons** including Kafka UI, Prometheus/Grafana stack, and an Nginx load balancer on port `80`

## 📁 Repository Structure

| Path                                                                                                                 | Description                                                                                          |
|----------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------|
| `docker-compose.yml`                                                                                                 | One-click orchestration of databases, Kafka stack, microservices, UI, monitoring, and load balancer  |
| `database/init/`                                                                                                     | Ordered SQL scripts that provision databases, users, and seed data for quick bootstrap               |
| `spring-boot-admin/`, `cbi-service/`, `schedule-service/`, `workflow-service/`, `gateway-service/`, `kafka-manager/` | Individual Spring Boot services (Java 21 + GraalVM) with dedicated Dockerfiles and Flyway migrations |
| `shared-lib/`                                                                                                        | Common DTOs, utilities, and shared security/kafka configurations consumed by all backend services    |
| `ui-frontend/`                                                                                                       | React + TypeScript SPA (Vite) that surfaces workflow designer, dashboards, and admin tools           |
| `monitoring/`                                                                                                        | Prometheus scrape configs plus Grafana dashboards/datasources used out of the box                    |
| `Java-Kafka.wiki/`                                                                                                   | Source-of-truth documentation (English + Persian) synchronized with this README                      |
| `load-balancer/`                                                                                                     | Nginx configuration that exposes an aggregated entrypoint on port `80`                               |

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Frontend   │    │  Gateway Service│    │ Spring Boot Admin│
│   (React/TS)    │◄──►│  (Port: 8084)   │◄──►│   (Port: 8080)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Workflow Service│    │   CBI Service   │    │ Schedule Service │
│   (Port: 8083)   │    │   (Port: 8081)   │    │   (Port: 8082)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Kafka Manager  │    │      Kafka      │    │      MySQL      │
│   (Port: 8085)   │◄──►│   (Port: 9092)  │    │   (Port: 3306)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Technology Stack

### Backend
- **Java 21** with GraalVM Native Image
- **Spring Boot 3.2.0**
- **Spring Security** & **JWT**
- **Spring Kafka** & **Spring Batch**
- **MySQL 8.0** with **Flyway**
- **Docker** & **Docker Compose**

### Frontend
- **React 18** with **TypeScript**
- **Vite** & **Ant Design**
- **React Flow** for workflow designer
- **Zustand** for state management

## 📚 Documentation

- [Wiki Home & FAQ](Java-Kafka.wiki/Home.md) — بهترین نقطه شروع (FA/EN)
- [Architecture Overview](Java-Kafka.wiki/Architecture.md)
- [Installation Guide](Java-Kafka.wiki/Installation.md)
- [API Documentation](Java-Kafka.wiki/API-Documentation.md)
- [DB & Flyway Guide](Java-Kafka.wiki/DB-Migrations.md) / [FA](Java-Kafka.wiki/FA-راهنمای-دیتابیس-و-مایگریشن.md)
- [Development Workflow](Java-Kafka.wiki/Development-Workflow.md) / [FA](Java-Kafka.wiki/FA-روند-توسعه.md)
- [Security & Auth](Java-Kafka.wiki/Security-and-Auth.md) / [FA](Java-Kafka.wiki/FA-امنیت-و-احراز-هویت.md)
- [Kafka Operations](Java-Kafka.wiki/Kafka-Operations-and-Scenarios.md) / [FA](Java-Kafka.wiki/FA-عملیات-کافکا-و-سناریوها.md)
- [Monitoring & Observability](Java-Kafka.wiki/Monitoring.md) / [FA](Java-Kafka.wiki/FA-مانیتورینگ.md)
- [Debugging, Logs & Troubleshooting](Java-Kafka.wiki/Debugging-Logs-and-Troubleshooting.md) / [FA](Java-Kafka.wiki/FA-دیباگ-لاگ-و-عیب‌یابی.md)
- Changelog & Roadmap: [Changelog](Java-Kafka.wiki/Changelog.md)
- [CI/CD Pipeline Guide](docs/CI-CD.md)

> **Tip:** فایل‌های ویکی به‌صورت کامل در این ریپو نگه‌داری می‌شوند؛ پس از هر تغییر کد، برگه مربوطه را همگام کنید تا
> مستندات و کد از هم جدا نشوند.

## 🔧 Services

| Service           | Port | Description                                    |
|-------------------|------|------------------------------------------------|
| Load Balancer     | 80   | Nginx entrypoint routing to UI/Admin/Gateway   |
| UI Frontend       | 3000 | React TypeScript UI with workflow designer     |
| Spring Boot Admin | 8080 | Centralized monitoring and log management      |
| CBI Service       | 8081 | Central Business Integration with WSDL support |
| Schedule Service  | 8082 | Scheduled data processing with Spring Batch    |
| Workflow Service  | 8083 | Workflow management and orchestration          |
| Gateway Service   | 8084 | API Gateway with Spring Security               |
| Kafka Manager     | 8085 | Kafka cluster management and monitoring        |
| Kafka UI          | 8086 | Web-based Kafka management interface           |
| Prometheus        | 9090 | Metrics collection and storage                 |
| Grafana           | 9091 | Monitoring dashboards and visualization        |

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 21 (for local development)
- Maven 3.8+ (for local development)
- Node.js 18+ (for frontend development)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Java-Kafka
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Verify installation**
   ```bash
   docker-compose ps
   ```

4. **Access the application**
   - UI Frontend: http://localhost:3000
   - Spring Boot Admin: http://localhost:8080
   - Gateway Service: http://localhost:8084
   - Kafka UI: http://localhost:8086
   - Prometheus: http://localhost:9090
   - Grafana: http://localhost:9091 (admin / admin123)

### Default Credentials
- **Username**: `admin`
- **Password**: `admin123`

## 🔐 Authentication

All API endpoints require JWT authentication:

```bash
# Login
curl -X POST http://localhost:8084/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Use token in subsequent requests
curl -H "Authorization: Bearer <token>" \
  http://localhost:8084/api/workflow/definitions
```

## 📊 Monitoring

### Grafana

- Real-time metrics visualization
- Custom dashboards for microservices
- Alert management
- Performance analysis
- **Access:** http://localhost:9091 (admin / admin123)

### Prometheus

- Metrics collection and storage
- Query language (PromQL)
- Service discovery
- Long-term data storage
- **Access:** http://localhost:9090

### Spring Boot Admin
- Real-time service monitoring
- Health check dashboard
- Performance metrics
- Log aggregation

### Kafka Monitoring
- Topic management
- Consumer group monitoring
- Message throughput
- Cluster health

### AOP Logging
- Method execution tracking
- Performance monitoring
- Error tracking
- Database persistence

### Monitoring Metrics

Each service exposes metrics at `/actuator/prometheus`:

- **JVM Metrics:** Memory, CPU, Threads, GC
- **HTTP Metrics:** Request count, response time, error rate
- **Kafka Metrics:** Consumer lag, message rate
- **Database Metrics:** Connection pool, query performance

## 🏗️ Development

### Local Development Setup

1. **Start infrastructure**
   ```bash
   docker-compose up mysql kafka zookeeper -d
   ```

2. **Build shared library**
   ```bash
   cd shared-lib
   mvn clean install
   ```

3. **Run services locally**
   ```bash
   cd spring-boot-admin
   mvn spring-boot:run
   ```

4. **Run frontend**
   ```bash
   cd ui-frontend
   npm install
   npm run dev
   ```

### Building Native Images

```bash
# Build native image for a service
cd spring-boot-admin
mvn clean package -Pnative

# Or use Docker
docker build -t spring-boot-admin .
```

## 🐳 Docker

### Build and Run
```bash
# Build all services
docker-compose build

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Individual Services
```bash
# Start specific service
docker-compose up spring-boot-admin -d

# Scale service
docker-compose up --scale cbi-service=3 -d
```

## 📈 Performance

### GraalVM Native Image Benefits
- **Faster startup**: 10-50x faster than JVM
- **Lower memory**: 50-80% less memory usage
- **Better performance**: Optimized for cloud deployment
- **Smaller containers**: Reduced image sizes

### Monitoring Metrics
- Service response times
- Memory usage
- CPU utilization
- Database connection pools
- Kafka message throughput

## 🔧 Configuration

### Environment Variables
```yaml
# Database
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/service_db
SPRING_DATASOURCE_USERNAME: service_user
SPRING_DATASOURCE_PASSWORD: service_password

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092

# Security
JWT_SECRET: your-secret-key
JWT_EXPIRATION: 86400000
```

### Application Properties
Each service has its own `application.yml` with service-specific configuration.

## 🧪 Testing

### Unit Tests
```bash
# Run tests for specific service
cd spring-boot-admin
mvn test

# Run all tests
mvn test
```

### Integration Tests
```bash
# Run integration tests
mvn verify
```

### API Testing
```bash
# Test authentication
curl -X POST http://localhost:8084/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Test workflow execution
curl -X POST http://localhost:8083/api/workflow/execute/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"param1":"value1"}'
```

## 🚀 Deployment

### Production Deployment
```bash
# Use production environment
docker-compose --env-file .env.production up -d
```

### Kubernetes Deployment
```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods
kubectl get services
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the documentation in the wiki folder
- Contact the development team

## 🔄 Updates

### Recent Updates
- Added GraalVM Native Image support
- Implemented AOP logging across all services
- Created comprehensive API documentation
- Added workflow designer UI
- Implemented Spring Batch for data processing

### Roadmap
- [ ] Add more external service integrations
- [ ] Implement advanced workflow features
- [ ] Add more monitoring dashboards
- [ ] Implement distributed tracing
- [ ] Add more authentication providers

---

**Built with ❤️ using Java, Spring Boot, and modern microservices architecture**
