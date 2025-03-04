CREATE DATABASE LeaveRegMng;
GO
USE LeaveRegMng;
GO

-- Users table (base information)
CREATE TABLE Users (
    id INT PRIMARY KEY IDENTITY(1,1),
    username VARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,  -- Store hashed password
    email VARCHAR(100) UNIQUE NOT NULL,
    fullName NVARCHAR(100) NOT NULL,
    createdAt DATETIME2 DEFAULT GETDATE(),
    updatedAt DATETIME2 DEFAULT GETDATE(),
    resetToken NVARCHAR(255),
    resetTokenExpiry DATETIME2
);
GO

-- Staff table (extends Users)
CREATE TABLE Staff (
    id INT PRIMARY KEY,
    division VARCHAR(20) NOT NULL CHECK (division IN ('IT', 'QA', 'Sale')),
    role NVARCHAR(50) NOT NULL CHECK (role IN (N'Division Leader', N'Trưởng nhóm', N'Nhân viên')),
    FOREIGN KEY (id) REFERENCES Users(id) ON DELETE CASCADE
);
GO

CREATE TABLE LeaveRequests (
    rid INT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(150) NOT NULL,
    reason VARCHAR(150) NOT NULL,
    startDate DATE NOT NULL, -- Đổi [from] thành startDate để tránh từ khóa SQL
    endDate DATE NOT NULL, -- Đổi [to] thành endDate để tránh từ khóa SQL
    status TINYINT NOT NULL,
    createdBy VARCHAR(150) NOT NULL,
    ownerId INT NOT NULL, -- Đổi owner_eid thành ownerId cho nhất quán
    createdAt DATETIME DEFAULT GETDATE()
);
GO

