# 🧾 Flipkart Offer Service
**PiePay Take Home Assignment** <br><br>

A Spring Boot backend service that parses Flipkart-style offer APIs, stores them in a relational database, and provides endpoints to retrieve the best offer based on bank and payment method.

_Includes all the parts including bonus_.

---

## 📦 Features

- 🧠 Parse Flipkart-like offer JSON responses
- 💾 Store offers with categories: discount, cashback, no-cost EMI, deferred payment
- 🔍 Query highest applicable discount for any amount, bank name, and payment instrument
- 🌐 RESTful APIs with Swagger documentation (OpenAPI 3.0)
- 🗃️ In-memory H2 database (easy to inspect)

---

## 🛠️ Tech Stack
- Java 17
- Spring Boot 3
- Spring Web, Spring Data JPA
- H2 (In-Memory RDBMS)
- Springdoc OpenAPI for Swagger UI
- Lombok

---

## 🚀 How to Run the Project

### ✅ Prerequisites

- Java 17+
- Maven 3.x+ (not necessary if using the provided wrapper)
- Internet access (for dependency download)

---

### ⚙️ Setup Instructions

1. **Clone the repository**

   ```bash
   git clone https://github.com/gargkeshav2002/flipkart-offer-service.git
   cd flipkart-offer-service
   ```

2. **Build the project**

   ```bash
   ./mvnw clean install
   ```

3. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

Or if you're using Windows:

````bash
mvnw.cmd clean install
mvnw.cmd spring-boot:run
 ````

**Or, from your IDE (like IntelliJ):**

1. Open OfferServiceApplication.java
2. Right-click → Run
---

## 📘 API Documentation (Swagger)

### Access the Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

You’ll find endpoints like:

- `POST /offer` → Upload Flipkart-style API response and store offers
- `GET /highest-discount` → Find highest applicable offer based on amount, bank name and payment instrument


### Test APIs Using Postman

Alternatively, you can test the APIs using **Postman**:

#### 1. `POST /offer`
**URL:** `http://localhost:8080/offer`  
**Method:** `POST`  
**Body Type:** `raw` → `JSON`  
**Payload (Example):**
```json
{
  "promotionMessage": "Flat ₹500 on HDFC Bank Credit Card",
  "pricingData": {
    "noCostEmi": [
      {
        "description": "No Cost EMI on Credit Cards"
      }
    ]
  }
}
```

#### 2. `GET /highest-discount`
**URL:** `http://localhost:8080/highest-discount?amountToPay=10000&bankName=HDFC&paymentInstrument=CREDIT`  
**Method:** `GET`
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

### ✅ Why Use RDBMS (PostgreSQL) and H2 for This Assignment?

- Offers and payment rules have a **structured, tabular format** — making relational databases ideal for normalization, indexing, and querying.
- An **RDBMS schema** (with columns like bank name, discount type, payment instrument, etc.) ensures **data consistency, integrity, and fast lookups** using indexed fields.
- SQL-based filtering (e.g., `WHERE bankName = 'HDFC' AND paymentInstrument = 'CREDIT'`) allows us to **efficiently query the best offer** based on user input.
- We follow a normalized schema to avoid redundancy and allow **future scalability** (e.g., separating banks, instruments, offer categories if needed).
- While a full-fledged RDBMS like **PostgreSQL** would be used in production, we used **H2** because:
    - It behaves similarly (SQL syntax, schema, constraints)
    - Runs **in-memory**, requiring **no setup or installation**
    - Includes a **browser-accessible console** for reviewers to easily inspect stored offers




### ✅ Why Return 500 for NO_COST_EMI?

- Since these offers don’t define numeric discount, we simulate a fixed saving (₹500) to make them comparable
- Similarly, ₹300 for cashback if the message doesn’t define an amount
- These values are logical approximations based on Flipkart’s platform behavior.
- Can later be replaced with dynamic values from actual EMI plan APIs if available


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
- Enhance bank and instrument detection using:
  - NLP or ML-based fuzzy matching 
  - External mapping file (JSON or YAML)
- Implement full offer expiry validation (e.g., don't return expired offers)
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
