# 🚗 Smart Parking Management System

Complete parking lot management system with Java backend and modern web frontend.

---

## 📋 Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Structure](#project-structure)
3. [Database Setup](#database-setup)
4. [Backend Setup](#backend-setup)
5. [Frontend Setup](#frontend-setup)
6. [Running the Application](#running-the-application)
7. [Features](#features)
8. [Troubleshooting](#troubleshooting)

---

## 🔧 Prerequisites

### Required Software:
1. **Java JDK 11 or higher**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify: Open CMD and run `java -version`

2. **MySQL Server 8.0 or higher**
   - Download: https://dev.mysql.com/downloads/mysql/
   - Install MySQL Server and MySQL Workbench
   - Remember your root password during installation

3. **Web Browser**
   - Chrome, Firefox, or Edge (latest version)

---

## 📁 Project Structure

```
anudip java jdbc project/
│
├── src/
│   ├── model/              # Entity classes
│   │   ├── ParkingSlot.java
│   │   └── Ticket.java
│   │
│   ├── dao/                # Database operations
│   │   ├── ParkingSlotDAO.java
│   │   └── TicketDAO.java
│   │
│   ├── service/            # Business logic
│   │   └── ParkingService.java
│   │
│   ├── util/               # Database connection
│   │   └── DatabaseConnection.java
│   │
│   ├── ui/                 # Console application
│   │   └── ParkingLotApp.java
│   │
│   └── api/                # REST API server
│       └── ParkingAPI.java
│
├── web/                    # Frontend files
│   ├── index.html
│   ├── style.css
│   └── app.js
│
├── bin/                    # Compiled classes (auto-generated)
│
├── mysql-connector-j-9.5.0.jar  # MySQL JDBC driver
├── schema.sql              # Database setup script
├── compile.bat             # Compilation script
├── run.bat                 # Console app runner
└── start-server.bat        # Web server runner
```

---

## 🗄️ Database Setup

### Step 1: Start MySQL Server

**Windows:**
- Open "Services" (Win + R → type `services.msc`)
- Find "MySQL80" service
- Right-click → Start (if not running)

**OR**

- Open MySQL Workbench
- Click on your local connection

### Step 2: Update Database Password

1. Open file: `src\util\DatabaseConnection.java`
2. Find these lines:
```java
private static final String PASSWORD = "Ankit@123";
```
3. Replace `Ankit@123` with YOUR MySQL root password
4. Save the file

### Step 3: Create Database and Tables

**Method 1: Using MySQL Workbench (Recommended)**

1. Open MySQL Workbench
2. Connect to your local MySQL server
3. Click: File → Open SQL Script
4. Select: `schema.sql` from project folder
5. Click: Execute (⚡ lightning icon)
6. Verify:
   ```sql
   USE parking_lot;
   SELECT COUNT(*) FROM slots;
   ```
   Should show: 150 rows (100 CAR + 50 BIKE)

**Method 2: Using MySQL Command Line**

1. Open MySQL Command Line Client
2. Enter your root password
3. Run:
   ```sql
   source C:/anudip java jdbc project/schema.sql
   ```
4. Verify:
   ```sql
   USE parking_lot;
   SHOW TABLES;
   SELECT vehicle_type, COUNT(*) FROM slots GROUP BY vehicle_type;
   ```

### Step 4: Verify Database

Run these queries to confirm setup:

```sql
USE parking_lot;

-- Check tables exist
SHOW TABLES;
-- Should show: slots, tickets

-- Check slot counts
SELECT vehicle_type, COUNT(*) as total FROM slots GROUP BY vehicle_type;
-- Should show: CAR=100, BIKE=50

-- Check all slots are empty
SELECT COUNT(*) FROM slots WHERE occupied = TRUE;
-- Should show: 0
```

---

## 🔨 Backend Setup

### Step 1: Verify MySQL Connector

Check if `mysql-connector-j-9.5.0.jar` exists in project root folder.

If missing:
1. Download from: https://dev.mysql.com/downloads/connector/j/
2. Extract and copy `.jar` file to project root

### Step 2: Compile Java Code

**Option A: Using Batch File (Easy)**
```cmd
Double-click: compile.bat
```

**Option B: Manual Compilation**
```cmd
cd "C:\anudip java jdbc project"
javac -cp ".;mysql-connector-j-9.5.0.jar" -d bin src\model\*.java src\util\*.java src\dao\*.java src\service\*.java src\ui\*.java src\api\*.java
```

**Expected Output:**
- No errors
- `bin` folder created with compiled `.class` files

### Step 3: Test Database Connection

Run the test file:
```cmd
java -cp ".;mysql-connector-j-9.5.0.jar" SimpleJdbcTest
```

**Expected Output:**
```
✅ MySQL Connected!
1 | 1 | CAR | false
✅ Done.
```

If you see errors, check:
- MySQL server is running
- Password in `DatabaseConnection.java` is correct
- Database `parking_lot` exists

---

## 🌐 Frontend Setup

### Files Already Created:
- `web/index.html` - Main page
- `web/style.css` - Styling
- `web/app.js` - JavaScript logic

### No Additional Setup Required!
Frontend files are ready to use.

---

## 🚀 Running the Application

### Option 1: Web Application (Recommended)

**Step 1: Start Server**
```cmd
Double-click: start-server.bat
```

**OR manually:**
```cmd
cd "C:\anudip java jdbc project"
java -cp "bin;mysql-connector-j-9.5.0.jar" api.ParkingAPI
```

**Expected Output:**
```
Server started: http://localhost:8080
Open browser and go to: http://localhost:8080
```

**Step 2: Open Browser**
- Open any browser
- Go to: `http://localhost:8080`
- You should see the parking management dashboard

**Step 3: Use the Application**
- Navigate using sidebar menu
- Dashboard shows live statistics
- Park vehicles, generate bills, search, view history

### Option 2: Console Application

**Run:**
```cmd
Double-click: run.bat
```

**OR manually:**
```cmd
cd "C:\anudip java jdbc project"
java -cp "bin;mysql-connector-j-9.5.0.jar" ui.ParkingLotApp
```

**Menu Options:**
```
1. Park Vehicle (CAR/BIKE)
2. Exit Vehicle & Generate Bill
3. Show Parking Status
4. Search Vehicle by Number
5. View All Parked Vehicles
6. View Parking History (Last 10)
7. Show Pricing Information
0. Exit System
```

---

## ✨ Features

### 1. Dashboard
- Live parking statistics
- Available slots for CAR and BIKE
- Pricing information
- Recent transactions

### 2. Park Vehicle
- Select vehicle type (CAR/BIKE)
- Enter vehicle number
- Get ticket ID and slot number
- Automatic slot assignment

### 3. Exit Vehicle & Billing
- Enter vehicle number
- Automatic bill calculation
- Duration-based pricing
- Instant payment processing

### 4. Search Vehicle
- Find vehicle by number
- View parking duration
- Check ticket details

### 5. Parked Vehicles
- View all currently parked vehicles
- See slot assignments
- Check parking duration
- Separate counts for cars and bikes

### 6. Transaction History
- Last 10 completed transactions
- Vehicle details and amounts
- Auto-refresh every 5 seconds

### 💰 Pricing Structure

**CAR:**
- First 2 hours: ₹30
- After 2 hours: ₹10 per hour
- Example: 5 hours = ₹30 + (3 × ₹10) = ₹60

**BIKE:**
- First 2 hours: ₹20
- After 2 hours: ₹5 per hour
- Example: 5 hours = ₹20 + (3 × ₹5) = ₹35

**Note:** Minimum billing is 1 hour, duration rounded up to nearest hour.

---

## 🔍 Troubleshooting

### Problem 1: "MySQL JDBC Driver not found"

**Solution:**
1. Check `mysql-connector-j-9.5.0.jar` exists in project root
2. Recompile using `compile.bat`

### Problem 2: "Access denied for user 'root'"

**Solution:**
1. Open `src\util\DatabaseConnection.java`
2. Update password:
   ```java
   private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";
   ```
3. Recompile: `compile.bat`

### Problem 3: "Unknown database 'parking_lot'"

**Solution:**
1. Open MySQL Workbench
2. Run `schema.sql` script
3. Verify: `SHOW DATABASES;` should list `parking_lot`

### Problem 4: "Table 'slots' doesn't exist"

**Solution:**
1. Run in MySQL:
   ```sql
   USE parking_lot;
   SHOW TABLES;
   ```
2. If empty, run `schema.sql` again

### Problem 5: Web page shows "Cannot connect to server"

**Solution:**
1. Check if server is running (should see "Server started" message)
2. Verify URL: `http://localhost:8080` (not https)
3. Check firewall isn't blocking port 8080
4. Restart server: Close CMD and run `start-server.bat` again

### Problem 6: "javac is not recognized"

**Solution:**
1. Install Java JDK
2. Add to PATH:
   - Right-click "This PC" → Properties
   - Advanced System Settings → Environment Variables
   - Edit PATH, add: `C:\Program Files\Java\jdk-XX\bin`
3. Restart CMD and try again

### Problem 7: Compilation errors

**Solution:**
1. Delete `bin` folder
2. Run `compile.bat` again
3. Check all source files are present in `src` folder

### Problem 8: "Port 8080 already in use"

**Solution:**
1. Close any running server instances
2. Check Task Manager for java.exe processes
3. Kill java.exe processes
4. Run `start-server.bat` again

### Problem 9: Frontend not loading

**Solution:**
1. Check `web` folder exists with all 3 files:
   - index.html
   - style.css
   - app.js
2. Clear browser cache (Ctrl + Shift + Delete)
3. Refresh page (Ctrl + F5)

### Problem 10: Data not showing in frontend

**Solution:**
1. Open browser console (F12)
2. Check for errors
3. Verify API calls are successful
4. Check database has data:
   ```sql
   SELECT * FROM slots LIMIT 5;
   ```

---

## 📊 Testing the Application

### Test Scenario 1: Park a Car

1. Open web application
2. Click "Park Vehicle" in sidebar
3. Select "Car"
4. Enter: `UP14AB1234`
5. Click "Park Vehicle"
6. Should see: Success message with Ticket ID and Slot

### Test Scenario 2: View Parked Vehicles

1. Click "Parked Vehicles" in sidebar
2. Should see: Card showing UP14AB1234
3. Should show: Slot number and duration

### Test Scenario 3: Search Vehicle

1. Click "Search" in sidebar
2. Enter: `UP14AB1234`
3. Click "Search"
4. Should see: Vehicle details with parking duration

### Test Scenario 4: Exit Vehicle

1. Click "Exit Vehicle" in sidebar
2. Enter: `UP14AB1234`
3. Click "Generate Bill"
4. Should see: Bill with duration and amount

### Test Scenario 5: View History

1. Click "History" in sidebar
2. Should see: UP14AB1234 transaction with amount

---

## 🎯 Quick Start Commands

```cmd
# 1. Setup database (one time)
# Open MySQL Workbench → Run schema.sql

# 2. Compile code (after any changes)
compile.bat

# 3. Run web application
start-server.bat
# Then open: http://localhost:8080

# 4. Run console application
run.bat
```

---

## 📞 Support

If you encounter any issues:

1. Check [Troubleshooting](#troubleshooting) section
2. Verify all prerequisites are installed
3. Ensure MySQL server is running
4. Check database password is correct
5. Verify all files are present

---

## 🎓 Project Details

**Capacity:**
- 100 Car parking slots
- 50 Bike parking slots
- Total: 150 slots

**Technology Stack:**
- Backend: Java 11+
- Database: MySQL 8.0+
- Frontend: HTML5, CSS3, JavaScript
- Server: Java HttpServer
- JDBC Driver: MySQL Connector/J 9.5.0

**Architecture:**
- MVC Pattern
- REST API
- DAO Pattern
- Service Layer
- Responsive Web Design

---

## ✅ Checklist

Before running, ensure:

- [ ] Java JDK installed and in PATH
- [ ] MySQL Server installed and running
- [ ] Database password updated in `DatabaseConnection.java`
- [ ] `schema.sql` executed successfully
- [ ] Database has 150 slots (100 CAR + 50 BIKE)
- [ ] `mysql-connector-j-9.5.0.jar` present in project root
- [ ] Code compiled successfully (no errors)
- [ ] `bin` folder created with `.class` files
- [ ] Web browser available

---

## 🎉 Success Indicators

**Database Setup Success:**
```sql
SELECT COUNT(*) FROM slots;
-- Result: 150
```

**Compilation Success:**
```
No error messages
bin/ folder created
```

**Server Start Success:**
```
Server started: http://localhost:8080
```

**Application Working:**
- Dashboard loads in browser
- Can park vehicles
- Can generate bills
- Can view parked vehicles
- Statistics update automatically

---

**Made with ❤️ for Anudip Java JDBC Project**

Last Updated: December 2024
