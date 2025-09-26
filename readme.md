# Personal Salary Tracker

A comprehensive Spring Boot backend system that enables employees to manage their personal salary history across multiple years, currencies, and companies. The system provides currency-normalized insights, salary growth analytics, and visualization-ready APIs.

## Assumption: 
A User should be able to analyse the salary trend from the given set of structured data (Excel). 
Assumed that this is not for a single user. One user can analyse N numer of employees salary trend. Hence, User and Employee kept different. 

## Gap Analysis:

- Single vs Multi-user: Not clear if it’s just for one user or supports multiple employees with ADMIN role.
- Excel format: No fixed schema (columns, mandatory fields, handling of duplicates/invalid rows).
- Year definition: Ambiguous (fiscal year vs calendar year).
- Exchange rates: Source not defined (static, manual, or live API; current vs historical rates).
- Salary hike logic: Unclear if applied to Fixed only or entire CTC, and whether hikes compound.
- Company switch: Missing rules for closing old company record, start/end dates.
- Latest salary: Conflicting scope (single employee vs list of employees).
- APIs overlap: Trend API vs Graphical Comparison API partially duplicate functionality.

## Postman API Collection
- if you are using VS Code restcalls.rest file can be used from the vs code itself. 
-     

## 🚀 Features

- **Multi-year Salary Tracking**: Store and manage salary data across different financial years
- **Multi-company Support**: Track salary history across company switches
- **Multi-currency Handling**: Support for different currencies with normalized base currency conversion
- **Excel Integration**: Upload and parse Excel files containing salary history
- **Growth Analytics**: Generate salary trend analysis and company comparisons
- **Secure Authentication**: Google OAuth2 integration with role-based access control
- **Visualization Ready**: APIs designed for easy integration with chart libraries

## 📋 Table of Contents

- [Personal Salary Tracker](#personal-salary-tracker)
  - [Assumption:](#assumption)
  - [Gap Analysis:](#gap-analysis)
  - [Postman API Collection](#postman-api-collection)
  - [🚀 Features](#-features)
  - [📋 Table of Contents](#-table-of-contents)
  - [🔧 Prerequisites](#-prerequisites)
  - [🛠 Installation](#-installation)
  - [⚙️ Configuration](#️-configuration)
    - [Google OAuth2 Setup](#google-oauth2-setup)
  - [📊 Excel Format](#-excel-format)
    - [Sheet Naming Convention](#sheet-naming-convention)
    - [Sheet Structure](#sheet-structure)
    - [Sample Excel Structure](#sample-excel-structure)
    - [Postman Collection](#postman-collection)
    - [Key Components](#key-components)
    - [Design Patterns Used](#design-patterns-used)
  - [🚀 Deployment](#-deployment)
    - [Docker Support](#docker-support)
    - [Environment Variables](#environment-variables)
    - [Deploying to Render with GitHub Actions](#deploying-to-render-with-github-actions)
      - [How It Works](#how-it-works)
      - [Example Render Deploy Step](#example-render-deploy-step)
      - [Full Example](#full-example)
  - [Demo](#demo)
  - [User Creation, Admin Role, and OAuth 2.0](#user-creation-admin-role-and-oauth-20)
    - [User Creation \& Admin Role](#user-creation--admin-role)
    - [OAuth 2.0 Authentication](#oauth-20-authentication)

## 🔧 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Google OAuth2 credentials
- Currency conversion based on the data table

## 🛠 Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/gaurav9969351313/salary-tracker.git
   cd salary-tracker
   ```

2. **Configure application properties**
   Update the configuration with your Google OAuth2 credentials and other settings.

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## ⚙️ Configuration

### Google OAuth2 Setup

1. Create a project in [Google Cloud Console](https://console.cloud.google.com/)
2. Enable Google+ API
3. Create OAuth2 credentials
4. Add your credentials to `application.properties`:

```properties
# Google OAuth2 Configuration
spring.security.oauth2.client.registration.google.client-id=your-client-id
spring.security.oauth2.client.registration.google.client-secret=your-client-secret
spring.security.oauth2.client.registration.google.scope=profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}

# Database Configuration (H2)
spring.datasource.url=jdbc:h2:mem:salarydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.h2.console.enabled=true

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 📊 Excel Format

The system expects Excel files with the following structure:

### Sheet Naming Convention
- Each sheet represents a financial year (e.g., "2021-22", "2022-23")

### Sheet Structure
| Column | Description | Example |
|--------|-------------|---------|
| A | Company | TechCorp |
| B | Fixed CTC | 800000 |
| C | Variable | 120000 |
| D | Deductions | 50000 |
| E | Currency | INR |

### Sample Excel Structure
```
Sheet: 2023-24
┌─────────────┬─────────────┬──────────┬─────────────┬──────────┬
│ Company     │ Fixed CTC   │ Variable │ Deductions  │ Currency │ 
├─────────────┼─────────────┼──────────┼─────────────┼──────────┼
│ TechCorp    │ 800000      │ 120000   │ 50000       │ INR      │
└─────────────┴─────────────┴──────────┴─────────────┴──────────┴
```


### Postman Collection

Import the provided Postman collection from `postman/Salary-Management-APIs.json` for testing all endpoints.

Sample test scenarios Needs to be added as Unit Test Cases:
- Authentication flow
- Excel upload with various formats (.xlsx / .xls)
- Currency conversion validation
- Error handling
- Multi-company salary comparison

### Key Components

- **SalaryController**: REST API endpoints
- **SalaryService**: Business logic and data processing
- **ExcelParserService**: Excel file parsing using Apache POI
- **CurrencyService**: Currency conversion logic
- **SecurityConfig**: OAuth2 and role-based security
- **AuditService**: Audit trail for salary changes

### Design Patterns Used

- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic separation
- **DTO Pattern**: Data transfer optimization
- **Factory Pattern**: Excel parser creation

## 🚀 Deployment

### Docker Support
```bash
docker build -t salary-tracker:0.0.1 . 
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=docker -e GOOGLE_CLIENT_ID=client-id -e GOOGLE_CLIENT_SECRET=client-secret --name salary-tracker salary-tracker:0.0.1
```

### Environment Variables
```bash
export GOOGLE_CLIENT_ID=your-client-id
export GOOGLE_CLIENT_SECRET=your-client-secret
export DATABASE_URL=jdbc:h2:mem:salarydb
export SPRING_PROFILES_ACTIVE=docker / dev
```

### Deploying to Render with GitHub Actions

This project can be automatically deployed to [Render](https://render.com/) using GitHub Actions. Render is a cloud platform for running web services, APIs, and static sites.

#### How It Works
- The GitHub Actions workflow (`.github/workflows/ci-pipeline.yml`) builds your project on every push or pull request to the `main` branch, and can also be triggered manually.
- To deploy to Render, you can add a deployment step in your workflow that uses the [Render Deploy Action](https://github.com/marketplace/actions/render-deploy) or triggers a deploy hook provided by Render.

#### Example Render Deploy Step
Add the following step to your workflow after building and testing:

```yaml
- name: Deploy to Render
  uses: render-examples/deploy-to-render-action@v1
  with:
    service-id: ${{ secrets.RENDER_SERVICE_ID }}
    api-key: ${{ secrets.RENDER_API_KEY }}
```

- `RENDER_SERVICE_ID` and `RENDER_API_KEY` should be set as GitHub repository secrets.
- You can find your Service ID and API Key in your Render dashboard.

Alternatively, you can use a [Render Deploy Hook](https://render.com/docs/deploy-hooks):

```yaml
- name: Trigger Render Deploy Hook
  run: |
    curl -X POST ${{ secrets.RENDER_DEPLOY_HOOK_URL }}
```

- Set `RENDER_DEPLOY_HOOK_URL` as a secret containing your Render deploy hook URL.

#### Full Example
See the [Render documentation](https://render.com/docs/deploy-from-github) for more details and advanced options.

## Demo

Check the live demo on Render: [https://salary-tracker-0fbe.onrender.com](https://salary-tracker-0fbe.onrender.com)

---

## User Creation, Admin Role, and OAuth 2.0

### User Creation & Admin Role
- Users are stored in the database.
- The first user(s) can be created by inserting directly into the database or via a registration process (if enabled).
- Each user has a `role` field, which can be `USER` or `ADMIN`.
- Only users with the `ADMIN` role can access the admin dashboard and perform management actions (user management (needs developement), salary uploads, analytics, etc.).

### OAuth 2.0 Authentication
- The application supports Google OAuth 2.0 login for secure authentication.
- Users can log in using their Google account by clicking the "Login with Google" button.
- OAuth 2.0 configuration is set in `application-dev.properties` and `application-docker.properties` using environment variables:
  - `GOOGLE_CLIENT_ID`
  - `GOOGLE_CLIENT_SECRET`
- On successful login, the user's email and profile information are retrieved from Google and used to authenticate or register the user in the system.
- Role-based access is enforced after login (e.g., only admins can access `/admin/*` routes).

---

Made with ❤️ by [Gaurav Talele](https://github.com/gaurav9969351313)