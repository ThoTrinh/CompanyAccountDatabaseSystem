-- While working on the database design, it's useful to start from scratch every time
-- Hence, we drop tables in reverse order they are created (so the foreign key constraints are not violated)
DROP TABLE IF EXISTS Transaction1;
DROP TABLE IF EXISTS Department_Account;
DROP TABLE IF EXISTS Process_Account;
DROP TABLE IF EXISTS Assembly_Account;
DROP TABLE IF EXISTS Account;
DROP TABLE IF EXISTS Cut_Job;
DROP TABLE IF EXISTS Paint_Job;
DROP TABLE IF EXISTS Fit_Job;
DROP TABLE IF EXISTS Job;
DROP TABLE IF EXISTS Cut_Process;
DROP TABLE IF EXISTS Paint_Process;
DROP TABLE IF EXISTS Fit_Process;
DROP TABLE IF EXISTS Process;
DROP TABLE IF EXISTS Assembly1;
DROP TABLE IF EXISTS Department;
DROP TABLE IF EXISTS Customer;


-- Table Creation
CREATE TABLE Customer (
  name VARCHAR(64) PRIMARY KEY,
  address VARCHAR(128) NOT NULL,
  category INT NOT NULL,

  CONSTRAINT category_range CHECK(category < 11 AND category > 0)
)

CREATE TABLE Department (
  department_number INT PRIMARY KEY,
  department_data VARCHAR(128) NOT NULL
)

CREATE TABLE Assembly1 ( -- Renamed to Assembly1 because Assembly isn't allowed
  assembly_id INT PRIMARY KEY,
  date_ordered DATE NOT NULL,
  assembly_details VARCHAR(256) NOT NULL,
  name VARCHAR(64) NOT NULL,

  CONSTRAINT FK_customer_name FOREIGN KEY (name) REFERENCES Customer
)

CREATE TABLE Process (
  process_id INT PRIMARY KEY,
  process_data VARCHAR(128) NOT NULL,
  assembly_id INT,
  department_number INT NOT NULL,

  CONSTRAINT FK_assembly_id FOREIGN KEY (assembly_id) REFERENCES Assembly1,
  CONSTRAINT FK_department_number FOREIGN KEY (department_number) REFERENCES Department
)

CREATE TABLE Fit_Process (
  process_id INT PRIMARY KEY,
  process_data VARCHAR(128) NOT NULL,
  fit_type VARCHAR(64) NOT NULL,
  assembly_id INT,
  department_number INT NOT NULL,

  CONSTRAINT FK_assembly_id2 FOREIGN KEY (assembly_id) REFERENCES Assembly1,
  CONSTRAINT FK_department_number2 FOREIGN KEY (department_number) REFERENCES Department
)

CREATE TABLE Paint_Process (
  process_id INT PRIMARY KEY,
  process_data VARCHAR(128) NOT NULL,
  paint_type VARCHAR(64) NOT NULL,
  painting_method VARCHAR(64) NOT NULL,
  assembly_id INT,
  department_number INT NOT NULL,

  CONSTRAINT FK_assembly_id3 FOREIGN KEY (assembly_id) REFERENCES Assembly1,
  CONSTRAINT FK_department_number3 FOREIGN KEY (department_number) REFERENCES Department
)

CREATE TABLE Cut_Process (
  process_id INT PRIMARY KEY,
  process_data VARCHAR(128) NOT NULL,
  cutting_type VARCHAR(64) NOT NULL,
  machine_type VARCHAR(64) NOT NULL,
  assembly_id INT,
  department_number INT NOT NULL,

  CONSTRAINT FK_assembly_id4 FOREIGN KEY (assembly_id) REFERENCES Assembly1,
  CONSTRAINT FK_department_number4 FOREIGN KEY (department_number) REFERENCES Department
)

CREATE TABLE Job (
  job_number INT PRIMARY KEY,
  date_commenced DATE NOT NULL,
  date_completed DATE,
  process_id INT NOT NULL,
  assembly_id INT NOT NULL,

  CONSTRAINT FK_process_id FOREIGN KEY (process_id) REFERENCES Process,
  CONSTRAINT FK_assembly_id5 FOREIGN KEY (assembly_id) REFERENCES Assembly1
)

CREATE TABLE Fit_Job (
  job_number INT PRIMARY KEY,
  labor_time TIME NOT NULL
)

CREATE TABLE Paint_Job (
  job_number INT PRIMARY KEY,
  labor_time TIME NOT NULL,
  color VARCHAR(64) NOT NULL,
  volume INT NOT NULL
)

CREATE TABLE Cut_Job (
  job_number INT PRIMARY KEY,
  labor_time TIME NOT NULL,
  type_machine_used VARCHAR(64) NOT NULL,
  amount_of_time_machine_used TIME NOT NULL,
  material_used VARCHAR(64) NOT NULL
)

CREATE TABLE Account (
  account_number INT PRIMARY KEY,
  date_account_established DATE NOT NULL
)

CREATE TABLE Assembly_Account (
  account_number INT PRIMARY KEY,
  date_account_established DATE NOT NULL,
  cost1 INT DEFAULT 0,
  assembly_id INT NOT NULL,

  CONSTRAINT FK_assembly_id9 FOREIGN KEY (assembly_id) REFERENCES Assembly1
)

CREATE TABLE Process_Account (
  account_number INT PRIMARY KEY,
  date_account_established DATE NOT NULL,
  cost2 INT DEFAULT 0,
  process_id INT NOT NULL,

  CONSTRAINT FK_process_id5 FOREIGN KEY (process_id) REFERENCES Process
)

CREATE TABLE Department_Account (
  account_number INT PRIMARY KEY,
  date_account_established DATE NOT NULL,
  cost3 INT DEFAULT 0,
  department_number INT NOT NULL

  CONSTRAINT FK_department_number5 FOREIGN KEY (department_number) REFERENCES Department
)

CREATE TABLE Transaction1 ( -- Renamed Transaction1 because Transaction isn't allowed
  transaction_num INT PRIMARY KEY,
  cost INT NOT NULL,
  job_number INT NOT NULL,
  account_number INT NOT NULL,

  CONSTRAINT FK_job_number FOREIGN KEY (job_number) REFERENCES Job,
  CONSTRAINT FK_account_number FOREIGN KEY (account_number) REFERENCES Account
)
-- Index Tables ------------------------------------

-- Customer Table Index on name
CREATE INDEX idx_customer
ON Customer (name);

-- Process Table Index on process_id
CREATE INDEX idx_process
ON Process (process_id);

-- Fit Process Table Index on process_id
CREATE INDEX idx_fit_process
ON Fit_Process (process_id);

-- Paint Process Table Index on process_id
CREATE INDEX idx_paint_process
ON Paint_Process (process_id);

-- Cut Process Table Index on process_id
CREATE INDEX idx_cut_process
ON Cut_Process (process_id);

-- Job Table with 2 Indexes
-- Primary Index on job_number; Secondary Index on date_commenced
CREATE INDEX idx_job
ON Job (job_number, date_commenced);

-- Fit Job Table Index on job_number
CREATE INDEX idx_fit_job
ON Fit_Job (job_number);

-- Paint Job Table Index on job_number
CREATE INDEX idx_paint_job
ON Paint_Job (job_number);

-- Cut Job Table Index on job_number
CREATE INDEX idx_cut_job
ON Cut_Job (job_number);

-- Assembly Account Table Index on account_number
CREATE INDEX idx_assembly_account
ON Assembly_Account (account_number);

-- Process Account Table Index on account_number
CREATE INDEX idx_process_account
ON Process_Account (account_number);

-- Department Account Table Index on account_number
CREATE INDEX idx_department_account
ON Department_Account (account_number);
