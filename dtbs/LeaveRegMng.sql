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

-- Bảng Division mới
CREATE TABLE Division (
    divisionId INT PRIMARY KEY IDENTITY(1,1),
    divisionName VARCHAR(20) UNIQUE NOT NULL
);
GO

-- Thêm các giá trị mặc định cho bảng Division
INSERT INTO Division (divisionName) VALUES ('IT'), ('QA'), ('Sale');
GO

-- Bảng Role mới
CREATE TABLE Role (
    roleId INT PRIMARY KEY IDENTITY(1,1),
    roleName NVARCHAR(50) UNIQUE NOT NULL
);
GO

-- Thêm các giá trị mặc định cho bảng Role
INSERT INTO Role (roleName) VALUES (N'Division Leader'), (N'Trưởng nhóm'), (N'Nhân viên');
GO

-- Staff table (extends Users) với các thay đổi
CREATE TABLE Staff (
    id INT PRIMARY KEY,
    divisionId INT NOT NULL,
    roleId INT NOT NULL,
    [group] NVARCHAR(50) NULL,  -- Thêm trường group, có thể NULL
    FOREIGN KEY (id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (divisionId) REFERENCES Division(divisionId),
    FOREIGN KEY (roleId) REFERENCES Role(roleId)
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