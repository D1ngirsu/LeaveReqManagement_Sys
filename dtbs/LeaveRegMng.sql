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

INSERT INTO Users (username, password, email, fullName)
VALUES 
    ('admin', 'admin123', 'admin@company.com', N'Administrator'),
    ('nguyenvan', 'pass123', 'nguyenvan@company.com', N'Nguyễn Văn An'),
    ('tranthiB', 'pass456', 'tranthib@company.com', N'Trần Thị Bình'),
    ('phamtuan', 'pass789', 'phamtuan@company.com', N'Phạm Tuấn'),
    ('leminh', 'pass321', 'leminh@company.com', N'Lê Minh'),
    ('hoangmai', 'pass654', 'hoangmai@company.com', N'Hoàng Mai'),
    ('vuduc', 'pass987', 'vuduc@company.com', N'Vũ Đức'),
    ('ngothao', 'passabc', 'ngothao@company.com', N'Ngô Thảo'),
    ('duongthanh', 'passxyz', 'duongthanh@company.com', N'Dương Thanh');
GO

-- Thêm dữ liệu vào bảng Staff
-- Sử dụng divisionId và roleId từ dữ liệu mặc định đã có sẵn
INSERT INTO Staff (id, divisionId, roleId, [group])
VALUES 
    (1, 1, 3, NULL), -- Admin là nhân viên phòng IT, không thuộc nhóm nào
    (2, 1, 1, N'Dev Team'), -- Nguyễn Văn An là Division Leader phòng IT, nhóm Dev Team
    (3, 1, 2, N'Dev Team'), -- Trần Thị Bình là Trưởng nhóm phòng IT, nhóm Dev Team
    (4, 1, 3, N'Dev Team'), -- Phạm Tuấn là Nhân viên phòng IT, nhóm Dev Team
    (5, 2, 1, NULL), -- Lê Minh là Division Leader phòng QA, không thuộc nhóm
    (6, 2, 3, N'Test Team'), -- Hoàng Mai là Nhân viên phòng QA, nhóm Test Team
    (7, 3, 1, NULL), -- Vũ Đức là Division Leader phòng Sale
    (8, 3, 2, N'Domestic Team'), -- Ngô Thảo là Trưởng nhóm phòng Sale, nhóm Domestic Team
    (9, 3, 3, N'Domestic Team'); -- Dương Thanh là Nhân viên phòng Sale, nhóm Domestic Team
GO

-- Thêm dữ liệu vào bảng LeaveRequests
INSERT INTO LeaveRequests (title, reason, startDate, endDate, status, createdBy, ownerId, createdAt)
VALUES 
    (N'Nghỉ phép năm', N'Nghỉ phép thường niên', '2025-03-25', '2025-03-26', 0, 'nguyenvan', 2, '2025-03-20'),
    (N'Nghỉ ốm', N'Bị cảm cúm', '2025-03-24', '2025-03-25', 1, 'tranthiB', 3, '2025-03-19'),
    (N'Nghỉ việc riêng', N'Có việc gia đình', '2025-03-28', '2025-03-29', 2, 'phamtuan', 4, '2025-03-18'),
    (N'Nghỉ phép năm', N'Du lịch cùng gia đình', '2025-04-01', '2025-04-05', 0, 'leminh', 5, '2025-03-21'),
    (N'Nghỉ không lương', N'Có việc cá nhân gấp', '2025-03-27', '2025-03-30', 1, 'hoangmai', 6, '2025-03-20'),
    (N'Nghỉ đám cưới', N'Tham dự đám cưới bạn', '2025-04-10', '2025-04-11', 0, 'vuduc', 7, '2025-03-22'),
    (N'Nghỉ ốm', N'Khám sức khỏe định kỳ', '2025-04-02', '2025-04-02', 2, 'ngothao', 8, '2025-03-19'),
    (N'Nghỉ việc gia đình', N'Có việc gia đình quan trọng', '2025-04-03', '2025-04-04', 1, 'duongthanh', 9, '2025-03-21');
GO