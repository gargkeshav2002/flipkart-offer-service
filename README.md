# 🧾 PiePay Offer Service

A Spring Boot backend service that parses Flipkart-style offer APIs, stores them in a relational database, and provides endpoints to retrieve the best offer based on bank and payment method.

---

## 📦 Features

- 🧠 Parse Flipkart-like offer JSON responses
- 💾 Store offers with categories: discount, cashback, no-cost EMI, deferred payment
- 🔍 Query highest applicable discount for any bank and payment instrument
- 🌐 RESTful APIs with Swagger documentation
- 🗃️ In-memory H2 database (easy to inspect)
- 🏷️ Organized by versioned APIs (v1, v2)

---

## 🚀 How to Run the Project

### ✅ Prerequisites

- Java 17+
- Maven 3.x+
- Internet access (for dependency download)

---

### ⚙️ Setup Instructions

1. **Clone the repository**

   ```bash
   git clone https://github.com/<your-username>/piepay-offer-service.git
   cd piepay-offer-service
   ```

2. **Build the project**

   ```bash
   ./mvnw clean install
   ```

3. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

---

## 📘 API Documentation (Swagger)

Access the Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

All endpoints are tagged and versioned.

---

## 🗃️ H2 Database Console

🔗 [Open H2 Console](http://localhost:8080/h2-console)

| Parameter       | Value                 |
|----------------|------------------------|
| JDBC URL        | `jdbc:h2:mem:offerdb` |
| Username        | `sa`                  |
| Password        | *(leave blank)*       |

👉 Once open, run:
```sql
SELECT * FROM offer;
```
to view stored offers.

---

## 🧠 Assumptions Made

- The Flipkart API response structure is not fixed, so we recursively scan for promotion messages and special widgets like EMI and Pay Later.
- Offers are extracted from:
  - `promotionMessage` fields
  - `pricingData.noCostEmi`
  - `widgets` section
- If the bank or instrument cannot be extracted from the offer message, we tag it as `"UNKNOWN"`.
- Default discount values:
  - NO_COST_EMI → ₹500
  - CASHBACK → ₹300
- Offers are treated as valid for 1 month (`validTill = now + 1 month`).
- Duplicate offers (based on title) are not re-saved.

---

## 🛠️ Design Choices

### ✅ Why Spring Boot?

- Rapid development with built-in server
- Auto-configuration of REST, DB, and Swagger
- Easy to extend (support versioning, security, etc.)

### ✅ Why H2?

- In-memory
- Easy to use and inspect
- No manual installation needed
- Exposed via browser console

### ✅ Why Versioned APIs?

- `/v1/offer`: simpler GET/POST logic
- `/v2/offer`: extended functionality to handle `paymentInstrument`

### ✅ Why Return 500 for NO_COST_EMI?

- Since these offers don’t define numeric discount, we simulate a fixed saving (₹500) to make them comparable
- Similarly, ₹300 for cashback if the message doesn’t define an amount

---

## 📈 Scaling Strategy for `/highest-discount`

To support 1000 RPS:

1. **Database Tuning**
   - Add indexes on `bankName`, `paymentInstrument`, `discountType`

2. **Caching**
   - Store computed best discounts in Redis for repeated queries

3. **Async Processing**
   - Queue POST `/offer` processing using Kafka or RabbitMQ

4. **Connection Pooling**
   - Use HikariCP for DB pooling with high concurrency configs

5. **Horizontal Scaling**
   - Containerize with Docker, deploy behind load balancer (e.g., NGINX, ALB)

---

## ⏳ What Would I Improve With More Time?

- Add unit and integration tests
- Add UI to visualize offers and simulate calculations
- Store offer source (promotion vs. widget vs. EMI)
- Externalize known banks and instruments in DB or config file
- Use PostgreSQL instead of H2 in prod
- Add rate limiting, logging, and security headers
- Implement role-based access for admin APIs
- Add pagination and search in `GET /offers`

---

## 👤 Author

**Keshav Garg**  
[LinkedIn](https://www.linkedin.com/in/keshav-garg01/)  
✉️ [keshavgarg019@gmail.com](mailto:keshavgarg019@gmail.com)

---

## 📄 License

This project is built as a backend take-home assignment and is meant for evaluation and demonstration purposes only.
