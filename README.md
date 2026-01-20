# TideTempo 🕐

A comprehensive time tracking and project management desktop application built with Java Swing for managing clients, projects, tasks, and work contracts.

## 📋 Project Overview

**TideTempo** is a Java SE desktop application developed as part of the Java Institute OOPC (Object-Oriented Programming Concepts) viva project. The application provides a complete solution for freelancers and businesses to track time, manage clients, projects, tasks, and contracts all in one place.

### Developed By
**Maleesha Pramud** - Java Institute

---

## ✨ Features

### Core Functionality
- 🔐 **User Authentication** - Secure login and registration system with session management
- 👥 **Client Management** - Add, edit, view, and manage client information
- 📁 **Project Management** - Organize work into projects with full CRUD operations
- ✅ **Task Management** - Create and track tasks with detailed information
- 📝 **Contract Management** - Handle contracts and agreements with clients
- ⏱️ **Time Tracking** - Start/stop timer to track working hours on tasks
- 📊 **Dashboard** - Visual overview of all activities and statistics
- ⚙️ **Settings** - Customize application preferences
- 📄 **Reporting** - Generate reports using JasperReports

### User Interface
- Modern dark theme using **FlatLaf (FlatMacDarkLaf)**
- Responsive Swing-based GUI with custom panels and dialogs
- SVG icon support for crisp, scalable graphics
- Date picker integration with LGoodDatePicker
- Action tables for data visualization
- Confirmation and success/warning dialogs

---

## 🛠️ Technology Stack

### Core Technologies
- **Language:** Java SE
- **GUI Framework:** Java Swing
- **IDE:** Apache NetBeans
- **Build Tool:** Apache Ant (build.xml)
- **Database:** MySQL 8.0

### Libraries & Dependencies

#### UI/UX Libraries
- **FlatLaf 3.6** - Modern Look and Feel
- **FlatLaf Extras 3.6** - Additional UI components and icons
- **jsvg 1.4.0** - SVG rendering support
- **AbsoluteLayout** - Layout manager
- **LGoodDatePicker 11.2.1** - Date selection component

#### Database
- **MySQL Connector/J 8.0.33** - JDBC driver for MySQL

#### Reporting
- **JasperReports 6.21.3** - Report generation
- **iText 2.1.7 / 5.5.13.3** - PDF generation
- **OpenPDF 1.3.30** - PDF library
- **Barbecue 1.5-beta1** - Barcode generation

#### Utilities
- **Commons Collections 3.2.2 / 4.4** - Collection utilities
- **Commons BeanUtils 1.9.4** - Bean manipulation
- **Commons Logging 1.2** - Logging abstraction
- **Commons Digester 2.1** - XML to object mapping
- **Spring Core 5.3.18** - Core utilities
- **Spring Beans 5.3.18** - Dependency injection support

---

## 📁 Project Structure

```
TideTempo/
├── src/
│   └── com/wigerlabs/tidetempo/
│       ├── components/          # Reusable UI components
│       │   ├── action_table/    # Custom table components
│       │   ├── contracts/       # Contract-related components
│       │   └── dashboard/       # Dashboard widgets
│       ├── connection/          # Database connectivity
│       │   └── MySQL.java       # MySQL connection handler
│       ├── dialog/              # Dialog windows
│       │   ├── ClientAddEditDialog.java
│       │   ├── ClientViewDialog.java
│       │   ├── ProjectAddEditDialog.java
│       │   ├── TaskAddEditDialog.java
│       │   ├── ContractViewDialog.java
│       │   ├── ConfirmationDialog.java
│       │   ├── SuccessDialog.java
│       │   └── WarningDialog.java
│       ├── entity/              # Data models
│       │   └── Gender.java
│       ├── gui/                 # Main application screens
│       │   ├── AuthScreen.java  # Authentication screen
│       │   └── HomeScreen.java  # Main application window
│       ├── img/                 # Image resources
│       ├── log/                 # Logging utilities
│       │   └── CustomLogger.java
│       ├── panel/               # Content panels
│       │   ├── DashboardPanel.java
│       │   ├── ClientsPanel.java
│       │   ├── ProjectsPanel.java
│       │   ├── TasksPanel.java
│       │   ├── ContractsPanel.java
│       │   ├── StartWorkingPanel.java
│       │   ├── SettingsPanel.java
│       │   ├── LoginPanel.java
│       │   ├── RegisterPanel.java
│       │   ├── ActionPanel.java
│       │   └── CountDownTimerPanel.java
│       ├── report/              # Report generation
│       ├── util/                # Utility classes
│       │   ├── SessionManager.java
│       │   ├── User.java
│       │   ├── Client.java
│       │   ├── Colors.java
│       │   ├── Borders.java
│       │   ├── TopBarStyle.java
│       │   ├── ComboItem.java
│       │   └── CurrentTimeGenerator.java
│       └── validation/          # Input validation
├── lib/                         # External JAR libraries
├── nbproject/                   # NetBeans project configuration
├── build/                       # Compiled classes
├── dist/                        # Distribution JAR
├── build.xml                    # Ant build script
├── manifest.mf                  # JAR manifest
└── README.md                    # This file
```

---

## 🚀 Getting Started

### Prerequisites

1. **Java Development Kit (JDK) 8 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK

2. **MySQL Server 8.0+**
   - Download from [MySQL Official Site](https://dev.mysql.com/downloads/mysql/)

3. **Apache NetBeans IDE** (recommended)
   - Download from [NetBeans Official Site](https://netbeans.apache.org/download/)

### Database Setup

1. Create a MySQL database named `tidetempo`:
   ```sql
   CREATE DATABASE tidetempo;
   ```

2. Update database credentials in [MySQL.java](src/com/wigerlabs/tidetempo/connection/MySQL.java):
   ```java
   private static final String DATABASE = "tidetempo";
   private static final String USERNAME = "root";
   private static final String PASSWORD = "your_password";
   ```

3. Import the database schema (if provided) or run the application to auto-create tables

### Installation & Running

#### Option 1: Using NetBeans IDE

1. Clone or download the project
   ```bash
   git clone <repository-url>
   ```

2. Open NetBeans IDE

3. Go to `File → Open Project`

4. Navigate to the TideTempo folder and open it

5. Right-click on the project and select `Clean and Build`

6. Run the project by clicking `Run → Run Project` or pressing `F6`

#### Option 2: Using Command Line

1. Navigate to the project directory:
   ```bash
   cd TideTempo
   ```

2. Build the project using Ant:
   ```bash
   ant clean
   ant compile
   ant jar
   ```

3. Run the application:
   ```bash
   java -jar dist/TideTempo.jar
   ```

---

## 📖 Usage Guide

### First Launch
1. The application starts with the **Authentication Screen**
2. Register a new account or login with existing credentials
3. User session is managed automatically

### Main Features

#### Dashboard
- View overview of clients, projects, tasks, and contracts
- Quick statistics and recent activities
- Visual representations of work data

#### Client Management
- Add new clients with detailed information
- Edit existing client details
- View client profiles
- Track client-related projects and contracts

#### Project Management
- Create projects linked to clients
- Set project deadlines and budgets
- Track project progress
- Associate tasks with projects

#### Task Management
- Create tasks with descriptions and priorities
- Assign tasks to projects
- Track task status and completion
- Time tracking for billable hours

#### Time Tracking
- Start working on a task with countdown timer
- Automatic time logging
- View time reports for billing

#### Contracts
- Create and manage client contracts
- View contract details and status
- Generate contract reports

---

## 🏗️ Architecture

### Design Patterns Used
- **Singleton Pattern** - Used in `AuthScreen`, `HomeScreen`, and `SessionManager` for single instance management
- **MVC Pattern** - Separation of data models (entity), views (gui/panel), and controllers
- **Factory Pattern** - Dialog creation and management
- **Observer Pattern** - Panel state management

### Key Components

- **SessionManager** - Manages user authentication state and session persistence
- **MySQL Connection Handler** - Centralized database connection management
- **CardLayout Navigation** - Panel switching in AuthScreen and HomeScreen
- **Custom Logger** - Application-wide logging functionality
- **TopBarStyle** - Consistent window title bar styling

---

## 🧪 Testing

The project includes a `test/` directory for unit and integration tests. Run tests using:

```bash
ant test
```

---

## 📦 Building for Distribution

To create a distributable JAR file:

```bash
ant clean jar
```

The output JAR will be located at `dist/TideTempo.jar`

To create a complete distribution with dependencies:

```bash
ant clean dist
```

---

## 🐛 Troubleshooting

### Common Issues

**Database Connection Failed**
- Verify MySQL server is running
- Check database credentials in `MySQL.java`
- Ensure database `tidetempo` exists

**Application Won't Start**
- Verify JDK version compatibility
- Check all library JARs are in the `lib/` folder
- Review logs in the console output

**UI Rendering Issues**
- Update FlatLaf libraries to latest version
- Check system graphics driver compatibility

---

## 📝 Future Enhancements

- [ ] Multi-user support with role-based access control
- [ ] Cloud database synchronization
- [ ] Export data to CSV/Excel
- [ ] Email notifications for deadlines
- [ ] Mobile companion app
- [ ] Advanced analytics and charts
- [ ] Invoice generation
- [ ] Backup and restore functionality

---

## 👨‍💻 Development

### Contributing
This is an academic project. For suggestions or improvements, please contact the developer.

### Code Style
- Follow Java naming conventions
- Use meaningful variable and method names
- Comment complex logic
- Keep methods focused and concise

---

## 📄 License

This project is developed for educational purposes as part of the Java Institute OOPC viva examination.

---

## 📞 Contact

**Developer:** Maleesha Pramud  
**Institution:** Java Institute  
**Project Type:** OOPC Viva Project  
**Year:** 2026

---

## 🙏 Acknowledgments

- Java Institute for project guidance
- FlatLaf team for the modern UI framework
- MySQL for the database management system
- JasperReports for reporting capabilities
- Open source community for various libraries used

---

**Made with ☕ and Java** 
