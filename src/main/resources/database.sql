USE [master]
GO
/****** Object:  Database [SaleElectricVehicleManagement]    Script Date: 24/11/2025 1:03:01 SA ******/
CREATE DATABASE [SaleElectricVehicleManagement]
 CONTAINMENT = NONE
 ON  PRIMARY
( NAME = N'SaleElectricVehicleManagement', FILENAME = N'/var/opt/mssql/data/SaleElectricVehicleManagement.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON
( NAME = N'SaleElectricVehicleManagement_log', FILENAME = N'/var/opt/mssql/data/SaleElectricVehicleManagement_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [SaleElectricVehicleManagement].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET ANSI_NULLS OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET ANSI_PADDING OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET ARITHABORT OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET CURSOR_DEFAULT  GLOBAL
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET  ENABLE_BROKER
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET RECOVERY FULL
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET  MULTI_USER
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET PAGE_VERIFY CHECKSUM
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET DB_CHAINING OFF
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF )
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET TARGET_RECOVERY_TIME = 60 SECONDS
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET DELAYED_DURABILITY = DISABLED
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET ACCELERATED_DATABASE_RECOVERY = OFF
GO
EXEC sys.sp_db_vardecimal_storage_format N'SaleElectricVehicleManagement', N'ON'
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET QUERY_STORE = ON
GO
ALTER DATABASE [SaleElectricVehicleManagement] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [SaleElectricVehicleManagement]
GO
/****** Object:  Table [dbo].[appointments]    Script Date: 24/11/2025 1:03:02 SA ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[appointments](
    [appointment_id] [int] IDENTITY(1,1) NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [end_time] [datetime2](6) NOT NULL,
    [start_time] [datetime2](6) NOT NULL,
    [status] [smallint] NULL,
    [updated_at] [datetime2](6) NOT NULL,
    [customer_id] [int] NOT NULL,
    [model_id] [int] NOT NULL,
    [store_id] [int] NOT NULL,
    [staff_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[appointment_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[colors]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[colors](
    [color_id] [int] IDENTITY(1,1) NOT NULL,
    [color_code] [varchar](7) NULL,
    [color_name] [nvarchar](50) NULL,
    PRIMARY KEY CLUSTERED
(
[color_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[company_bank_accounts]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[company_bank_accounts](
    [account_id] [int] IDENTITY(1,1) NOT NULL,
    [account_holder_name] [nvarchar](255) NOT NULL,
    [account_number] [nvarchar](255) NOT NULL,
    [bank_name] [nvarchar](255) NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [is_active] [bit] NOT NULL,
    [updated_at] [datetime2](6) NULL,
    PRIMARY KEY CLUSTERED
(
[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[contracts]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[contracts](
    [contract_id] [int] IDENTITY(1,1) NOT NULL,
    [contract_code] [varchar](255) NULL,
    [contract_date] [date] NOT NULL,
    [contract_file_url] [varchar](255) NULL,
    [created_at] [datetime2](6) NOT NULL,
    [deposit_price] [decimal](15, 0) NULL,
    [remain_price] [decimal](15, 0) NULL,
    [status] [varchar](255) NULL,
    [terms] [nvarchar](1000) NULL,
    [total_payment] [decimal](15, 0) NULL,
    [updated_at] [datetime2](6) NULL,
    [uploaded_by] [nvarchar](255) NOT NULL,
    [order_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[contract_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[customers]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[customers](
    [customer_id] [int] IDENTITY(1,1) NOT NULL,
    [address] [nvarchar](255) NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [email] [varchar](255) NULL,
    [full_name] [nvarchar](255) NOT NULL,
    [identification_number] [varchar](255) NOT NULL,
    [phone] [varchar](255) NOT NULL,
    [updated_at] [datetime2](6) NULL,
    PRIMARY KEY CLUSTERED
(
[customer_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[feedback_details]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[feedback_details](
    [feedback_detail_id] [int] IDENTITY(1,1) NOT NULL,
    [category] [nvarchar](255) NOT NULL,
    [content] [nvarchar](255) NULL,
    [rating] [int] NOT NULL,
    [feedback_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[feedback_detail_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[feedbacks]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[feedbacks](
    [feedback_id] [int] IDENTITY(1,1) NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [resolve_at] [datetime2](6) NULL,
    [status] [varchar](255) NOT NULL,
    [created_by] [int] NULL,
    [order_id] [int] NULL,
    [resolved_by] [int] NULL,
    PRIMARY KEY CLUSTERED
(
[feedback_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[inventory_transaction_contracts]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[inventory_transaction_contracts](
    [contract_id] [int] IDENTITY(1,1) NOT NULL,
    [contract_code] [varchar](255) NULL,
    [contract_date] [date] NOT NULL,
    [contract_file_url] [varchar](255) NULL,
    [created_at] [datetime2](6) NOT NULL,
    [status] [varchar](255) NULL,
    [updated_at] [datetime2](6) NULL,
    [uploaded_by] [nvarchar](255) NOT NULL,
    [evm_staff_id] [int] NOT NULL,
    [inventory_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[contract_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[inventory_transactions]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[inventory_transactions](
    [inventory_id] [int] IDENTITY(1,1) NOT NULL,
    [delivery_date] [datetime2](6) NULL,
    [discount_percentage] [int] NULL,
    [image_url] [varchar](255) NULL,
    [import_quantity] [int] NOT NULL,
    [order_date] [datetime2](6) NOT NULL,
    [status] [varchar](255) NOT NULL,
    [total_price] [numeric](38, 2) NOT NULL,
    [unit_base_price] [numeric](38, 2) NOT NULL,
    [updated_at] [datetime2](6) NULL,
    [store_stock_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[inventory_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[model_color]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[model_color](
    [model_color_id] [int] IDENTITY(1,1) NOT NULL,
    [image_path] [varchar](255) NULL,
    [price] [decimal](15, 0) NULL,
    [color_id] [int] NOT NULL,
    [model_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[model_color_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[models]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[models](
    [model_id] [int] IDENTITY(1,1) NOT NULL,
    [acceleration] [decimal](5, 2) NULL,
    [battery_capacity] [decimal](5, 2) NULL,
    [body_type] [varchar](255) NOT NULL,
    [create_at] [datetime2](6) NOT NULL,
    [description] [nvarchar](1000) NULL,
    [model_name] [nvarchar](255) NOT NULL,
    [model_year] [int] NOT NULL,
    [power_hp] [decimal](5, 2) NULL,
    [range] [decimal](5, 2) NULL,
    [seating_capacity] [int] NOT NULL,
    [torque_nm] [decimal](6, 2) NULL,
    [updated_at] [datetime2](6) NULL,
    PRIMARY KEY CLUSTERED
(
[model_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[order_details]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[order_details](
    [id] [int] IDENTITY(1,1) NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [discount_amount] [decimal](15, 0) NULL,
    [license_plate_fee] [decimal](15, 0) NULL,
    [quantity] [int] NOT NULL,
    [registration_fee] [decimal](15, 0) NULL,
    [total_price] [decimal](15, 0) NULL,
    [unit_price] [decimal](15, 0) NULL,
    [updated_at] [datetime2](6) NULL,
    [order_id] [int] NOT NULL,
    [promotion_id] [int] NULL,
    [store_stock_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[orders]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[orders](
    [order_id] [int] IDENTITY(1,1) NOT NULL,
    [order_code] [varchar](255) NULL,
    [order_date] [datetime2](6) NOT NULL,
    [status] [varchar](255) NULL,
    [total_payment] [decimal](15, 0) NULL,
    [total_price] [decimal](15, 0) NULL,
    [total_promotion_amount] [decimal](15, 0) NULL,
    [total_tax_price] [decimal](15, 0) NULL,
    [updated_at] [datetime2](6) NULL,
    [contract_id] [int] NULL,
    [customer_id] [int] NOT NULL,
    [store_id] [int] NOT NULL,
    [staff_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[order_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[payments]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[payments](
    [payment_id] [int] IDENTITY(1,1) NOT NULL,
    [amount] [decimal](15, 0) NULL,
    [created_at] [datetime2](6) NOT NULL,
    [payment_code] [varchar](255) NULL,
    [payment_method] [varchar](255) NULL,
    [payment_type] [varchar](255) NULL,
    [remain_price] [decimal](15, 0) NULL,
    [status] [varchar](255) NULL,
    [updated_at] [datetime2](6) NULL,
    [contract_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[payment_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[promotions]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[promotions](
    [promotion_id] [int] IDENTITY(1,1) NOT NULL,
    [amount] [decimal](15, 0) NULL,
    [created_at] [datetime2](6) NOT NULL,
    [description] [nvarchar](255) NOT NULL,
    [end_date] [datetime2](6) NOT NULL,
    [is_active] [bit] NOT NULL,
    [is_manually_disabled] [bit] NOT NULL,
    [promotion_name] [nvarchar](255) NOT NULL,
    [promotion_type] [varchar](255) NULL,
    [start_date] [datetime2](6) NOT NULL,
    [updated_at] [datetime2](6) NULL,
    [model_id] [int] NOT NULL,
    [store_id] [int] NULL,
    PRIMARY KEY CLUSTERED
(
[promotion_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[roles]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[roles](
    [role_id] [int] IDENTITY(1,1) NOT NULL,
    [role_name] [nvarchar](255) NOT NULL,
    PRIMARY KEY CLUSTERED
(
[role_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[sale_targets]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[sale_targets](
    [target_id] [int] IDENTITY(1,1) NOT NULL,
    [achieved_amount] [decimal](15, 0) NULL,
    [month] [int] NOT NULL,
    [status] [varchar](255) NOT NULL,
    [target_amount] [decimal](15, 0) NULL,
    [year] [int] NOT NULL,
    [store_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[target_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[store_stocks]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[store_stocks](
    [stock_id] [int] IDENTITY(1,1) NOT NULL,
    [price_of_store] [numeric](38, 2) NULL,
    [quantity] [int] NOT NULL,
    [reserved_quantity] [int] NULL,
    [model_color_id] [int] NULL,
    [store_id] [int] NULL,
    PRIMARY KEY CLUSTERED
(
[stock_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[stores]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[stores](
    [store_id] [int] IDENTITY(1,1) NOT NULL,
    [address] [nvarchar](255) NOT NULL,
    [contract_end_date] [date] NOT NULL,
    [contract_start_date] [date] NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [image_path] [varchar](255) NULL,
    [owner_name] [nvarchar](255) NOT NULL,
    [phone] [varchar](255) NOT NULL,
    [province_name] [nvarchar](255) NOT NULL,
    [status] [varchar](255) NOT NULL,
    [store_name] [nvarchar](255) NOT NULL,
    [updated_at] [datetime2](6) NULL,
    PRIMARY KEY CLUSTERED
(
[store_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[test_drive_configs]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[test_drive_configs](
    [config_id] [int] IDENTITY(1,1) NOT NULL,
    [appointment_duration_minutes] [int] NOT NULL,
    [end_time] [time](7) NOT NULL,
    [max_appointments_per_model_per_slot] [int] NOT NULL,
    [start_time] [time](7) NOT NULL,
    [store_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[config_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[transactions]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[transactions](
    [transaction_id] [int] IDENTITY(1,1) NOT NULL,
    [amount] [decimal](15, 0) NULL,
    [bank_transaction_code] [varchar](255) NULL,
    [gateway] [varchar](255) NULL,
    [status] [varchar](255) NULL,
    [transaction_ref] [varchar](255) NULL,
    [transaction_time] [datetime2](6) NOT NULL,
    [payment_id] [int] NOT NULL,
    PRIMARY KEY CLUSTERED
(
[transaction_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
/****** Object:  Table [dbo].[users]    Script Date: 24/11/2025 1:03:02 SA ******/
    SET ANSI_NULLS ON
    GO
    SET QUOTED_IDENTIFIER ON
    GO
CREATE TABLE [dbo].[users](
    [user_id] [int] IDENTITY(1,1) NOT NULL,
    [created_at] [datetime2](6) NOT NULL,
    [email] [varchar](255) NOT NULL,
    [full_name] [nvarchar](255) NOT NULL,
    [password] [varchar](255) NOT NULL,
    [phone] [varchar](255) NOT NULL,
    [status] [varchar](255) NOT NULL,
    [updated_at] [datetime2](6) NULL,
    [role_id] [int] NOT NULL,
    [store_id] [int] NULL,
    PRIMARY KEY CLUSTERED
(
[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    ) ON [PRIMARY]
    GO
    SET IDENTITY_INSERT [dbo].[colors] ON
    GO
    INSERT [dbo].[colors] ([color_id], [color_code], [color_name]) VALUES (2, N'#151414', N'Đen')
    GO
    INSERT [dbo].[colors] ([color_id], [color_code], [color_name]) VALUES (3, N'#00FF00', N'Xanh lá')
    GO
    INSERT [dbo].[colors] ([color_id], [color_code], [color_name]) VALUES (4, N'#BB259B', N'Tím')
    GO
    INSERT [dbo].[colors] ([color_id], [color_code], [color_name]) VALUES (5, N'#E8EC09', N'Vàng')
    GO
    INSERT [dbo].[colors] ([color_id], [color_code], [color_name]) VALUES (6, N'#0000FF', N'Xanh Dương')
    GO
    INSERT [dbo].[colors] ([color_id], [color_code], [color_name]) VALUES (7, N'#ACA7AF', N'Xám')
    GO
    INSERT [dbo].[colors] ([color_id], [color_code], [color_name]) VALUES (8, N'#DD0E0E', N'Đỏ')
    GO
    INSERT [dbo].[colors] ([color_id], [color_code], [color_name]) VALUES (9, N'#C440B3', N'Hồng')
    GO
    SET IDENTITY_INSERT [dbo].[colors] OFF
    GO
    SET IDENTITY_INSERT [dbo].[contracts] ON
    GO
    INSERT [dbo].[contracts] ([contract_id], [contract_code], [contract_date], [contract_file_url], [created_at], [deposit_price], [remain_price], [status], [terms], [total_payment], [updated_at], [uploaded_by], [order_id]) VALUES (1, N'CTR000001', CAST(N'2025-11-23' AS Date), NULL, CAST(N'2025-11-23T02:37:12.4897710' AS DateTime2), CAST(10900000 AS Decimal(15, 0)), CAST(43600000 AS Decimal(15, 0)), N'DRAFT', NULL, CAST(54500000 AS Decimal(15, 0)), NULL, N'Nguyễn Lê Hoàng Quân', 1)
    GO
    INSERT [dbo].[contracts] ([contract_id], [contract_code], [contract_date], [contract_file_url], [created_at], [deposit_price], [remain_price], [status], [terms], [total_payment], [updated_at], [uploaded_by], [order_id]) VALUES (2, N'CTR000002', CAST(N'2025-11-23' AS Date), N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763840642/contracts/m3dfbil7rmr8domdatcz.png', CAST(N'2025-11-23T02:43:06.4871190' AS DateTime2), CAST(13100000 AS Decimal(15, 0)), CAST(52400000 AS Decimal(15, 0)), N'FULLY_PAID', NULL, CAST(65500000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:44:03.1289240' AS DateTime2), N'Nguyễn Lê Hoàng Quân', 2)
    GO
    INSERT [dbo].[contracts] ([contract_id], [contract_code], [contract_date], [contract_file_url], [created_at], [deposit_price], [remain_price], [status], [terms], [total_payment], [updated_at], [uploaded_by], [order_id]) VALUES (3, N'CTR000003', CAST(N'2025-11-23' AS Date), NULL, CAST(N'2025-11-23T02:46:13.3945060' AS DateTime2), CAST(52400000 AS Decimal(15, 0)), CAST(209600000 AS Decimal(15, 0)), N'DRAFT', NULL, CAST(262000000 AS Decimal(15, 0)), NULL, N'Nguyễn Lê Hoàng Quân', 3)
    GO
    INSERT [dbo].[contracts] ([contract_id], [contract_code], [contract_date], [contract_file_url], [created_at], [deposit_price], [remain_price], [status], [terms], [total_payment], [updated_at], [uploaded_by], [order_id]) VALUES (4, N'CTR000004', CAST(N'2025-11-23' AS Date), N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763856966/contracts/cskpw2w4s4nii72d3f2w.jpg', CAST(N'2025-11-23T07:15:16.9284770' AS DateTime2), CAST(32700000 AS Decimal(15, 0)), CAST(130800000 AS Decimal(15, 0)), N'DEPOSIT_PAID', NULL, CAST(163500000 AS Decimal(15, 0)), CAST(N'2025-11-23T07:16:06.6798940' AS DateTime2), N'Nguyễn Lê Hoàng Quân', 5)
    GO
    SET IDENTITY_INSERT [dbo].[contracts] OFF
    GO
    SET IDENTITY_INSERT [dbo].[customers] ON
    GO
    INSERT [dbo].[customers] ([customer_id], [address], [created_at], [email], [full_name], [identification_number], [phone], [updated_at]) VALUES (1, N'đường Bà Điểm 3, Xã Bà Điểm, Huyện Hóc Môn, Thành phố Hồ Chí Minh', CAST(N'2025-11-23T01:53:50.4726510' AS DateTime2), N'khoi@gmail.com', N'Trần Lê Minh Khôi', N'046578345634', N'0946758345', NULL)
    GO
    INSERT [dbo].[customers] ([customer_id], [address], [created_at], [email], [full_name], [identification_number], [phone], [updated_at]) VALUES (2, N'đường Phan Lâm, Phường Phước Lộc, Thị xã La Gi, Tỉnh Bình Thuận', CAST(N'2025-11-23T01:57:54.9061190' AS DateTime2), N'minh@gmail.com', N'Hồ Nguyễn Lê Minh', N'046759345234', N'0947958345', NULL)
    GO
    INSERT [dbo].[customers] ([customer_id], [address], [created_at], [email], [full_name], [identification_number], [phone], [updated_at]) VALUES (3, N'đường Tân Thuận Tây, Phường Tân Thuận Tây, Quận 7, Thành phố Hồ Chí Minh', CAST(N'2025-11-23T01:58:55.7721160' AS DateTime2), N'minhnhat@gmail.com', N'Lê Nhật Minh', N'046753234423', N'0957837543', NULL)
    GO
    INSERT [dbo].[customers] ([customer_id], [address], [created_at], [email], [full_name], [identification_number], [phone], [updated_at]) VALUES (4, N'45 đường Lê Lợi, Xã Đăk Roong, Huyện KBang, Tỉnh Gia Lai', CAST(N'2025-11-23T01:59:36.9838430' AS DateTime2), N'nam@gmail.com', N'Phan Nhật Nam', N'046758930043', N'0957834523', NULL)
    GO
    INSERT [dbo].[customers] ([customer_id], [address], [created_at], [email], [full_name], [identification_number], [phone], [updated_at]) VALUES (5, N'43 đường Tân Sơn Nhất, Phường 1, Quận Tân Bình, Thành phố Hồ Chí Minh', CAST(N'2025-11-23T02:00:26.8555250' AS DateTime2), N'nghia@gmail.com', N'Trần Trọng Nghĩa', N'046539999943', N'0948858834', NULL)
    GO
    INSERT [dbo].[customers] ([customer_id], [address], [created_at], [email], [full_name], [identification_number], [phone], [updated_at]) VALUES (6, N'123 đường abc, Xã Phước Kiển, Huyện Nhà Bè, Thành phố Hồ Chí Minh', CAST(N'2025-11-23T07:08:54.9774400' AS DateTime2), N'abc@gmail.com', N'ABC', N'046738573462', N'0946753485', NULL)
    GO
    SET IDENTITY_INSERT [dbo].[customers] OFF
    GO
    SET IDENTITY_INSERT [dbo].[inventory_transaction_contracts] ON
    GO
    INSERT [dbo].[inventory_transaction_contracts] ([contract_id], [contract_code], [contract_date], [contract_file_url], [created_at], [status], [updated_at], [uploaded_by], [evm_staff_id], [inventory_id]) VALUES (1, N'ITC000001', CAST(N'2025-11-23' AS Date), N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763854797/inventory-contracts/ev8qtczrq2a6cqsfmakr.jpg', CAST(N'2025-11-23T06:36:36.4461140' AS DateTime2), N'SIGNED', CAST(N'2025-11-23T06:39:59.6745930' AS DateTime2), N'Phạm Trần Kha', 5, 1)
    GO
    SET IDENTITY_INSERT [dbo].[inventory_transaction_contracts] OFF
    GO
    SET IDENTITY_INSERT [dbo].[inventory_transactions] ON
    GO
    INSERT [dbo].[inventory_transactions] ([inventory_id], [delivery_date], [discount_percentage], [image_url], [import_quantity], [order_date], [status], [total_price], [unit_base_price], [updated_at], [store_stock_id]) VALUES (1, CAST(N'2025-11-23T06:42:05.5376560' AS DateTime2), 5, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763854827/inventory-receipts/bmytcj3b0xpppwehew3e.jpg', 3, CAST(N'2025-11-23T06:27:04.2454130' AS DateTime2), N'DELIVERED', CAST(253650000.00 AS Numeric(38, 2)), CAST(89000000.00 AS Numeric(38, 2)), CAST(N'2025-11-23T06:42:05.5376780' AS DateTime2), 7)
    GO
    SET IDENTITY_INSERT [dbo].[inventory_transactions] OFF
    GO
    SET IDENTITY_INSERT [dbo].[model_color] ON
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (1, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763838677/model-colors/1/f9xnr7tdj2llnbycrmzt.jpg', CAST(30000000 AS Decimal(15, 0)), 2, 1)
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (2, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763838840/model-colors/1/jdkd6sgkcjoctz7i0jjj.jpg', CAST(35000000 AS Decimal(15, 0)), 6, 1)
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (4, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763839173/model-colors/1/vjmkv3ywxazbqnqfoe6d.jpg', CAST(32000000 AS Decimal(15, 0)), 5, 1)
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (5, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763839261/model-colors/2/cv53xazejs94zck4882b.jpg', CAST(54000000 AS Decimal(15, 0)), 2, 2)
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (6, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763839330/model-colors/2/vzasfedt3yrji9wp9oix.jpg', CAST(52000000 AS Decimal(15, 0)), 7, 2)
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (7, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763839419/model-colors/3/onwz74rwsmbr9aet3ua8.jpg', CAST(90000000 AS Decimal(15, 0)), 6, 3)
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (8, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763839432/model-colors/3/gdk35o5w5xj0qlmnoy9a.jpg', CAST(89000000 AS Decimal(15, 0)), 4, 3)
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (9, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763839817/model-colors/4/pz8l2fito6cukzesdpsn.jpg', CAST(84000000 AS Decimal(15, 0)), 2, 4)
    GO
    INSERT [dbo].[model_color] ([model_color_id], [image_path], [price], [color_id], [model_id]) VALUES (10, N'https://res.cloudinary.com/dipk5szv2/image/upload/v1763839830/model-colors/4/zuxrrvvjoxbdhilxor9l.jpg', CAST(89000000 AS Decimal(15, 0)), 6, 4)
    GO
    SET IDENTITY_INSERT [dbo].[model_color] OFF
    GO
    SET IDENTITY_INSERT [dbo].[models] ON
    GO
    INSERT [dbo].[models] ([model_id], [acceleration], [battery_capacity], [body_type], [create_at], [description], [model_name], [model_year], [power_hp], [range], [seating_capacity], [torque_nm], [updated_at]) VALUES (1, CAST(4.50 AS Decimal(5, 2)), CAST(87.70 AS Decimal(5, 2)), N'SEDAN', CAST(N'2025-11-23T02:07:16.3536700' AS DateTime2), NULL, N'Electra Citylink', 2025, CAST(203.00 AS Decimal(5, 2)), CAST(420.00 AS Decimal(5, 2)), 5, CAST(450.00 AS Decimal(6, 2)), NULL)
    GO
    INSERT [dbo].[models] ([model_id], [acceleration], [battery_capacity], [body_type], [create_at], [description], [model_name], [model_year], [power_hp], [range], [seating_capacity], [torque_nm], [updated_at]) VALUES (2, CAST(4.50 AS Decimal(5, 2)), CAST(90.00 AS Decimal(5, 2)), N'SEDAN', CAST(N'2025-11-23T02:08:45.9707770' AS DateTime2), NULL, N'Electra Urbanpluse', 2024, CAST(550.00 AS Decimal(5, 2)), CAST(300.00 AS Decimal(5, 2)), 7, CAST(720.00 AS Decimal(6, 2)), NULL)
    GO
    INSERT [dbo].[models] ([model_id], [acceleration], [battery_capacity], [body_type], [create_at], [description], [model_name], [model_year], [power_hp], [range], [seating_capacity], [torque_nm], [updated_at]) VALUES (3, CAST(4.50 AS Decimal(5, 2)), CAST(120.00 AS Decimal(5, 2)), N'CONVERTIBLE', CAST(N'2025-11-23T02:23:10.3455970' AS DateTime2), NULL, N'Electra Grandtour', 2024, CAST(630.00 AS Decimal(5, 2)), CAST(500.00 AS Decimal(5, 2)), 8, CAST(780.00 AS Decimal(6, 2)), CAST(N'2025-11-23T02:23:17.9668350' AS DateTime2))
    GO
    INSERT [dbo].[models] ([model_id], [acceleration], [battery_capacity], [body_type], [create_at], [description], [model_name], [model_year], [power_hp], [range], [seating_capacity], [torque_nm], [updated_at]) VALUES (4, CAST(6.70 AS Decimal(5, 2)), CAST(140.00 AS Decimal(5, 2)), N'VAN', CAST(N'2025-11-23T02:28:01.9458550' AS DateTime2), NULL, N'Electra Voyager', 2025, CAST(720.00 AS Decimal(5, 2)), CAST(630.00 AS Decimal(5, 2)), 8, CAST(880.00 AS Decimal(6, 2)), CAST(N'2025-11-23T02:28:48.8085790' AS DateTime2))
    GO
    SET IDENTITY_INSERT [dbo].[models] OFF
    GO
    SET IDENTITY_INSERT [dbo].[order_details] ON
    GO
    INSERT [dbo].[order_details] ([id], [created_at], [discount_amount], [license_plate_fee], [quantity], [registration_fee], [total_price], [unit_price], [updated_at], [order_id], [promotion_id], [store_stock_id]) VALUES (1, CAST(N'2025-11-23T02:22:13.4240770' AS DateTime2), CAST(0 AS Decimal(15, 0)), CAST(20000000 AS Decimal(15, 0)), 1, CAST(1500000 AS Decimal(15, 0)), CAST(54500000 AS Decimal(15, 0)), CAST(33000000 AS Decimal(15, 0)), NULL, 1, NULL, 1)
    GO
    INSERT [dbo].[order_details] ([id], [created_at], [discount_amount], [license_plate_fee], [quantity], [registration_fee], [total_price], [unit_price], [updated_at], [order_id], [promotion_id], [store_stock_id]) VALUES (2, CAST(N'2025-11-23T02:42:59.5402210' AS DateTime2), CAST(0 AS Decimal(15, 0)), CAST(20000000 AS Decimal(15, 0)), 1, CAST(1500000 AS Decimal(15, 0)), CAST(65500000 AS Decimal(15, 0)), CAST(44000000 AS Decimal(15, 0)), NULL, 2, NULL, 2)
    GO
    INSERT [dbo].[order_details] ([id], [created_at], [discount_amount], [license_plate_fee], [quantity], [registration_fee], [total_price], [unit_price], [updated_at], [order_id], [promotion_id], [store_stock_id]) VALUES (3, CAST(N'2025-11-23T02:45:58.5695590' AS DateTime2), CAST(0 AS Decimal(15, 0)), CAST(80000000 AS Decimal(15, 0)), 4, CAST(6000000 AS Decimal(15, 0)), CAST(262000000 AS Decimal(15, 0)), CAST(44000000 AS Decimal(15, 0)), NULL, 3, NULL, 2)
    GO
    INSERT [dbo].[order_details] ([id], [created_at], [discount_amount], [license_plate_fee], [quantity], [registration_fee], [total_price], [unit_price], [updated_at], [order_id], [promotion_id], [store_stock_id]) VALUES (4, CAST(N'2025-11-23T07:12:23.4050420' AS DateTime2), CAST(6000000 AS Decimal(15, 0)), CAST(40000000 AS Decimal(15, 0)), 2, CAST(3000000 AS Decimal(15, 0)), CAST(103000000 AS Decimal(15, 0)), CAST(33000000 AS Decimal(15, 0)), NULL, 5, 1, 1)
    GO
    INSERT [dbo].[order_details] ([id], [created_at], [discount_amount], [license_plate_fee], [quantity], [registration_fee], [total_price], [unit_price], [updated_at], [order_id], [promotion_id], [store_stock_id]) VALUES (5, CAST(N'2025-11-23T07:12:23.4418270' AS DateTime2), CAST(5000000 AS Decimal(15, 0)), CAST(20000000 AS Decimal(15, 0)), 1, CAST(1500000 AS Decimal(15, 0)), CAST(60500000 AS Decimal(15, 0)), CAST(44000000 AS Decimal(15, 0)), NULL, 5, 3, 2)
    GO
    SET IDENTITY_INSERT [dbo].[order_details] OFF
    GO
    SET IDENTITY_INSERT [dbo].[orders] ON
    GO
    INSERT [dbo].[orders] ([order_id], [order_code], [order_date], [status], [total_payment], [total_price], [total_promotion_amount], [total_tax_price], [updated_at], [contract_id], [customer_id], [store_id], [staff_id]) VALUES (1, N'ORD000001', CAST(N'2025-11-23T02:00:49.3931600' AS DateTime2), N'CONTRACT_PENDING', CAST(54500000 AS Decimal(15, 0)), CAST(33000000 AS Decimal(15, 0)), CAST(0 AS Decimal(15, 0)), CAST(21500000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:37:02.1190410' AS DateTime2), 1, 5, 4, 3)
    GO
    INSERT [dbo].[orders] ([order_id], [order_code], [order_date], [status], [total_payment], [total_price], [total_promotion_amount], [total_tax_price], [updated_at], [contract_id], [customer_id], [store_id], [staff_id]) VALUES (2, N'ORD000002', CAST(N'2025-11-23T02:42:48.4384780' AS DateTime2), N'DELIVERED', CAST(65500000 AS Decimal(15, 0)), CAST(44000000 AS Decimal(15, 0)), CAST(0 AS Decimal(15, 0)), CAST(21500000 AS Decimal(15, 0)), CAST(N'2025-11-23T03:00:06.1369680' AS DateTime2), 2, 4, 4, 3)
    GO
    INSERT [dbo].[orders] ([order_id], [order_code], [order_date], [status], [total_payment], [total_price], [total_promotion_amount], [total_tax_price], [updated_at], [contract_id], [customer_id], [store_id], [staff_id]) VALUES (3, N'ORD000003', CAST(N'2025-11-23T02:45:08.7348490' AS DateTime2), N'CONTRACT_PENDING', CAST(262000000 AS Decimal(15, 0)), CAST(176000000 AS Decimal(15, 0)), CAST(0 AS Decimal(15, 0)), CAST(86000000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:46:08.8992030' AS DateTime2), 3, 3, 4, 3)
    GO
    INSERT [dbo].[orders] ([order_id], [order_code], [order_date], [status], [total_payment], [total_price], [total_promotion_amount], [total_tax_price], [updated_at], [contract_id], [customer_id], [store_id], [staff_id]) VALUES (4, N'ORD000004', CAST(N'2025-11-23T03:04:50.3010610' AS DateTime2), N'DRAFT', CAST(0 AS Decimal(15, 0)), CAST(0 AS Decimal(15, 0)), CAST(0 AS Decimal(15, 0)), CAST(0 AS Decimal(15, 0)), NULL, NULL, 2, 4, 3)
    GO
    INSERT [dbo].[orders] ([order_id], [order_code], [order_date], [status], [total_payment], [total_price], [total_promotion_amount], [total_tax_price], [updated_at], [contract_id], [customer_id], [store_id], [staff_id]) VALUES (5, N'ORD000005', CAST(N'2025-11-23T07:09:09.7399360' AS DateTime2), N'DEPOSIT_PAID', CAST(163500000 AS Decimal(15, 0)), CAST(110000000 AS Decimal(15, 0)), CAST(11000000 AS Decimal(15, 0)), CAST(64500000 AS Decimal(15, 0)), CAST(N'2025-11-23T07:17:20.4825030' AS DateTime2), 4, 6, 4, 3)
    GO
    SET IDENTITY_INSERT [dbo].[orders] OFF
    GO
    SET IDENTITY_INSERT [dbo].[payments] ON
    GO
    INSERT [dbo].[payments] ([payment_id], [amount], [created_at], [payment_code], [payment_method], [payment_type], [remain_price], [status], [updated_at], [contract_id]) VALUES (1, CAST(13100000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:46:29.4037770' AS DateTime2), N'DP000001', N'VNPAY', N'DEPOSIT', CAST(0 AS Decimal(15, 0)), N'COMPLETED', CAST(N'2025-11-23T02:47:00.6624270' AS DateTime2), 2)
    GO
    INSERT [dbo].[payments] ([payment_id], [amount], [created_at], [payment_code], [payment_method], [payment_type], [remain_price], [status], [updated_at], [contract_id]) VALUES (2, CAST(52400000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:59:09.4365370' AS DateTime2), N'BL000002', N'VNPAY', N'BALANCE', CAST(0 AS Decimal(15, 0)), N'COMPLETED', CAST(N'2025-11-23T02:59:33.8353660' AS DateTime2), 2)
    GO
    INSERT [dbo].[payments] ([payment_id], [amount], [created_at], [payment_code], [payment_method], [payment_type], [remain_price], [status], [updated_at], [contract_id]) VALUES (3, CAST(32700000 AS Decimal(15, 0)), CAST(N'2025-11-23T07:16:24.2329640' AS DateTime2), N'DP000003', N'VNPAY', N'DEPOSIT', CAST(0 AS Decimal(15, 0)), N'COMPLETED', CAST(N'2025-11-23T07:17:20.4545630' AS DateTime2), 4)
    GO
    INSERT [dbo].[payments] ([payment_id], [amount], [created_at], [payment_code], [payment_method], [payment_type], [remain_price], [status], [updated_at], [contract_id]) VALUES (4, CAST(130800000 AS Decimal(15, 0)), CAST(N'2025-11-23T07:17:40.9683040' AS DateTime2), N'BL000004', N'VNPAY', N'BALANCE', CAST(130800000 AS Decimal(15, 0)), N'DRAFT', NULL, 4)
    GO
    SET IDENTITY_INSERT [dbo].[payments] OFF
    GO
    SET IDENTITY_INSERT [dbo].[promotions] ON
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (1, CAST(10 AS Decimal(15, 0)), CAST(N'2025-11-23T02:55:04.8802170' AS DateTime2), N'Giảm giá cực mạnh', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Giảm giá cuối năm', N'PERCENTAGE', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 1, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (2, CAST(10 AS Decimal(15, 0)), CAST(N'2025-11-23T02:55:04.8852030' AS DateTime2), N'Giảm giá cực mạnh', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Giảm giá cuối năm', N'PERCENTAGE', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 2, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (3, CAST(5000000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:55:41.1598720' AS DateTime2), N'giảm giá Noel', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Giảm giá giáng sinh', N'FIXED_AMOUNT', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 1, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (4, CAST(5000000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:55:41.1635430' AS DateTime2), N'giảm giá Noel', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Giảm giá giáng sinh', N'FIXED_AMOUNT', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 2, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (5, CAST(5000000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:55:41.1648950' AS DateTime2), N'giảm giá Noel', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Giảm giá giáng sinh', N'FIXED_AMOUNT', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 3, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (6, CAST(5000000 AS Decimal(15, 0)), CAST(N'2025-11-23T02:55:41.1885560' AS DateTime2), N'giảm giá Noel', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Giảm giá giáng sinh', N'FIXED_AMOUNT', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 4, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (7, CAST(5 AS Decimal(15, 0)), CAST(N'2025-11-23T02:58:53.0972420' AS DateTime2), N'Xe mới giảm giá cho khách mới', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Giảm riêng xe mới', N'PERCENTAGE', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 1, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (8, CAST(9 AS Decimal(15, 0)), CAST(N'2025-11-23T03:18:01.1641000' AS DateTime2), N'dành cho tất cả mẫu xe', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Khai trương chi nhánh mới', N'PERCENTAGE', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 3, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (9, CAST(9 AS Decimal(15, 0)), CAST(N'2025-11-23T03:18:01.1679080' AS DateTime2), N'dành cho tất cả mẫu xe', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Khai trương chi nhánh mới', N'PERCENTAGE', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 1, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (10, CAST(9 AS Decimal(15, 0)), CAST(N'2025-11-23T03:18:01.1623480' AS DateTime2), N'dành cho tất cả mẫu xe', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Khai trương chi nhánh mới', N'PERCENTAGE', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 2, 4)
    GO
    INSERT [dbo].[promotions] ([promotion_id], [amount], [created_at], [description], [end_date], [is_active], [is_manually_disabled], [promotion_name], [promotion_type], [start_date], [updated_at], [model_id], [store_id]) VALUES (11, CAST(9 AS Decimal(15, 0)), CAST(N'2025-11-23T03:18:01.1573050' AS DateTime2), N'dành cho tất cả mẫu xe', CAST(N'2025-11-30T23:59:59.0000000' AS DateTime2), 1, 0, N'Khai trương chi nhánh mới', N'PERCENTAGE', CAST(N'2025-11-23T00:00:00.0000000' AS DateTime2), NULL, 4, 4)
    GO
    SET IDENTITY_INSERT [dbo].[promotions] OFF
    GO
    SET IDENTITY_INSERT [dbo].[roles] ON
    GO
    INSERT [dbo].[roles] ([role_id], [role_name]) VALUES (4, N'Nhân viên cửa hàng')
    GO
    INSERT [dbo].[roles] ([role_id], [role_name]) VALUES (2, N'Nhân viên hãng xe')
    GO
    INSERT [dbo].[roles] ([role_id], [role_name]) VALUES (3, N'Quản lý cửa hàng')
    GO
    INSERT [dbo].[roles] ([role_id], [role_name]) VALUES (1, N'Quản trị viên')
    GO
    SET IDENTITY_INSERT [dbo].[roles] OFF
    GO
    SET IDENTITY_INSERT [dbo].[store_stocks] ON
    GO
    INSERT [dbo].[store_stocks] ([stock_id], [price_of_store], [quantity], [reserved_quantity], [model_color_id], [store_id]) VALUES (1, CAST(30000000.00 AS Numeric(38, 2)), 100, 4, 1, 4)
    GO
    INSERT [dbo].[store_stocks] ([stock_id], [price_of_store], [quantity], [reserved_quantity], [model_color_id], [store_id]) VALUES (2, CAST(40000000.00 AS Numeric(38, 2)), 99, 6, 2, 4)
    GO
    INSERT [dbo].[store_stocks] ([stock_id], [price_of_store], [quantity], [reserved_quantity], [model_color_id], [store_id]) VALUES (5, CAST(50000000.00 AS Numeric(38, 2)), 70, 0, 4, 4)
    GO
    INSERT [dbo].[store_stocks] ([stock_id], [price_of_store], [quantity], [reserved_quantity], [model_color_id], [store_id]) VALUES (6, CAST(50000000.00 AS Numeric(38, 2)), 30, 0, 6, 4)
    GO
    INSERT [dbo].[store_stocks] ([stock_id], [price_of_store], [quantity], [reserved_quantity], [model_color_id], [store_id]) VALUES (7, CAST(50000000.00 AS Numeric(38, 2)), 18, 13, 8, 4)
    GO
    INSERT [dbo].[store_stocks] ([stock_id], [price_of_store], [quantity], [reserved_quantity], [model_color_id], [store_id]) VALUES (9, CAST(25000000.00 AS Numeric(38, 2)), 34, 15, 10, 4)
    GO
    SET IDENTITY_INSERT [dbo].[store_stocks] OFF
    GO
    SET IDENTITY_INSERT [dbo].[stores] ON
    GO
    INSERT [dbo].[stores] ([store_id], [address], [contract_end_date], [contract_start_date], [created_at], [image_path], [owner_name], [phone], [province_name], [status], [store_name], [updated_at]) VALUES (1, N'43 đường Thạnh Xuân, Phường Thạnh Xuân, Quận 12, Thành phố Hồ Chí Minh', CAST(N'2025-11-30' AS Date), CAST(N'2025-11-23' AS Date), CAST(N'2025-11-23T01:15:26.4461890' AS DateTime2), NULL, N'Nguyễn Quân', N'0485769345', N'Thành phố Hồ Chí Minh', N'ACTIVE', N'Electra quận 12', NULL)
    GO
    INSERT [dbo].[stores] ([store_id], [address], [contract_end_date], [contract_start_date], [created_at], [image_path], [owner_name], [phone], [province_name], [status], [store_name], [updated_at]) VALUES (2, N'23 đường Hai Bà Trưng, Phường Bến Nghé, Quận 1, Thành phố Hồ Chí Minh', CAST(N'2025-11-30' AS Date), CAST(N'2025-11-23' AS Date), CAST(N'2025-11-23T01:16:30.4009080' AS DateTime2), NULL, N'Nguyễn Trần Quang', N'0348953412', N'Thành phố Hồ Chí Minh', N'ACTIVE', N'Electra Bến Nghé', NULL)
    GO
    INSERT [dbo].[stores] ([store_id], [address], [contract_end_date], [contract_start_date], [created_at], [image_path], [owner_name], [phone], [province_name], [status], [store_name], [updated_at]) VALUES (3, N'53 đường Sơn Trà, Phường Nại Hiên Đông, Quận Sơn Trà, Thành phố Đà Nẵng', CAST(N'2025-12-01' AS Date), CAST(N'2025-11-23' AS Date), CAST(N'2025-11-23T01:17:27.3061350' AS DateTime2), NULL, N'Trần Anh Dũng', N'0478353425', N'Thành phố Đà Nẵng', N'ACTIVE', N'Electra Sơn Trà', NULL)
    GO
    INSERT [dbo].[stores] ([store_id], [address], [contract_end_date], [contract_start_date], [created_at], [image_path], [owner_name], [phone], [province_name], [status], [store_name], [updated_at]) VALUES (4, N'78 đường Tân Kì Tân Quý, Phường Tây Thạnh, Quận Tân Phú, Thành phố Hồ Chí Minh', CAST(N'2025-12-06' AS Date), CAST(N'2025-11-23' AS Date), CAST(N'2025-11-23T01:18:32.8270130' AS DateTime2), NULL, N'Nguyễn Thị Dung', N'0378495342', N'Thành phố Hồ Chí Minh', N'ACTIVE', N'Electra Tân Phú', NULL)
    GO
    INSERT [dbo].[stores] ([store_id], [address], [contract_end_date], [contract_start_date], [created_at], [image_path], [owner_name], [phone], [province_name], [status], [store_name], [updated_at]) VALUES (5, N'43 đường Đồng Xuân, Phường Đồng Xuân, Quận Hoàn Kiếm, Thành phố Hà Nội', CAST(N'2025-12-05' AS Date), CAST(N'2025-11-23' AS Date), CAST(N'2025-11-23T01:19:34.8818730' AS DateTime2), NULL, N'Phạm Lê Dũng', N'0478593453', N'Thành phố Hà Nội', N'ACTIVE', N'Electra Hà Nội', NULL)
    GO
    INSERT [dbo].[stores] ([store_id], [address], [contract_end_date], [contract_start_date], [created_at], [image_path], [owner_name], [phone], [province_name], [status], [store_name], [updated_at]) VALUES (6, N'56 đường Kha Vạn Cân, Phường Bình Chiểu, Thành phố Thủ Đức, Thành phố Hồ Chí Minh', CAST(N'2025-11-30' AS Date), CAST(N'2025-11-23' AS Date), CAST(N'2025-11-23T01:26:42.7395690' AS DateTime2), NULL, N'Nguyễn Anh Dũng', N'0478593453', N'Thành phố Hồ Chí Minh', N'ACTIVE', N'Electra Thủ Đức', NULL)
    GO
    INSERT [dbo].[stores] ([store_id], [address], [contract_end_date], [contract_start_date], [created_at], [image_path], [owner_name], [phone], [province_name], [status], [store_name], [updated_at]) VALUES (7, N'45 đường Lê Văn Việt, Thành phố Hồ Chí Minh', CAST(N'2025-12-02' AS Date), CAST(N'2025-11-23' AS Date), CAST(N'2025-11-23T01:33:14.8130340' AS DateTime2), NULL, N'Trần Lê Anh Kiệt', N'0384759864', N'Thành phố Hồ Chí Minh', N'ACTIVE', N'Electra Quận 9', NULL)
    GO
    SET IDENTITY_INSERT [dbo].[stores] OFF
    GO
    SET IDENTITY_INSERT [dbo].[transactions] ON
    GO
    INSERT [dbo].[transactions] ([transaction_id], [amount], [bank_transaction_code], [gateway], [status], [transaction_ref], [transaction_time], [payment_id]) VALUES (1, CAST(13100000 AS Decimal(15, 0)), N'VNP15281432', N'VNPAY', N'SUCCESS', N'15281432', CAST(N'2025-11-23T02:46:54.0000000' AS DateTime2), 1)
    GO
    INSERT [dbo].[transactions] ([transaction_id], [amount], [bank_transaction_code], [gateway], [status], [transaction_ref], [transaction_time], [payment_id]) VALUES (2, CAST(52400000 AS Decimal(15, 0)), N'VNP15281442', N'VNPAY', N'SUCCESS', N'15281442', CAST(N'2025-11-23T02:59:30.0000000' AS DateTime2), 2)
    GO
    INSERT [dbo].[transactions] ([transaction_id], [amount], [bank_transaction_code], [gateway], [status], [transaction_ref], [transaction_time], [payment_id]) VALUES (3, CAST(32700000 AS Decimal(15, 0)), N'VNP15281531', N'VNPAY', N'SUCCESS', N'15281531', CAST(N'2025-11-23T07:17:17.0000000' AS DateTime2), 3)
    GO
    SET IDENTITY_INSERT [dbo].[transactions] OFF
    GO
    SET IDENTITY_INSERT [dbo].[users] ON
    GO
    INSERT [dbo].[users] ([user_id], [created_at], [email], [full_name], [password], [phone], [status], [updated_at], [role_id], [store_id]) VALUES (1, CAST(N'2025-11-23T00:35:16.4551450' AS DateTime2), N'admin@gmail.com', N'ADMIN', N'$2a$10$3d7zGhZlhcDStkxbkIDeoush36Vivu7Vrw49BGhpHo0ut.HWpMp8u', N'0356964383', N'ACTIVE', CAST(N'2025-11-23T00:38:58.1950030' AS DateTime2), 1, NULL)
    GO
    INSERT [dbo].[users] ([user_id], [created_at], [email], [full_name], [password], [phone], [status], [updated_at], [role_id], [store_id]) VALUES (2, CAST(N'2025-11-23T01:46:54.6513960' AS DateTime2), N'loan@gmail.com', N'Nguyễn Thị Loan', N'$2a$10$U325V7GdCdak.P/4L0xW/uAaEbe7JBTV10AGC8gWrIDet.VEawYAi', N'0487596345', N'ACTIVE', CAST(N'2025-11-23T01:49:28.7607300' AS DateTime2), 4, 4)
    GO
    INSERT [dbo].[users] ([user_id], [created_at], [email], [full_name], [password], [phone], [status], [updated_at], [role_id], [store_id]) VALUES (3, CAST(N'2025-11-23T01:47:28.6883170' AS DateTime2), N'quan@gmail.com', N'Nguyễn Lê Hoàng Quân', N'$2a$10$Z/bbefwEuDZ6ObR32VkyIuhY/Zb4MqXUBJcJRAdF43syMtYvS5su2', N'0487583564', N'ACTIVE', CAST(N'2025-11-23T01:49:09.9838840' AS DateTime2), 4, 4)
    GO
    INSERT [dbo].[users] ([user_id], [created_at], [email], [full_name], [password], [phone], [status], [updated_at], [role_id], [store_id]) VALUES (4, CAST(N'2025-11-23T01:48:39.8255300' AS DateTime2), N'kha@gmail.com', N'Phạm Trần Kha', N'$2a$10$VULn.IbWZ3gFySWyiiZtp.YYJ6eCMWSqxFvnMXDheIAfh85ZXwsDe', N'0478593456', N'ACTIVE', CAST(N'2025-11-23T01:49:49.3014110' AS DateTime2), 3, 4)
    GO
    INSERT [dbo].[users] ([user_id], [created_at], [email], [full_name], [password], [phone], [status], [updated_at], [role_id], [store_id]) VALUES (5, CAST(N'2025-11-23T01:51:09.6371390' AS DateTime2), N'evm@gmail.com', N'Nguyễn Minh Khôi', N'$2a$10$YSmJ/r1w0l5COWYNu3f8p.fSpgOUKTxHwW8XQwSAQoDOEIGFoe7va', N'0478573453', N'ACTIVE', CAST(N'2025-11-23T01:51:28.1023880' AS DateTime2), 2, NULL)
    GO
    SET IDENTITY_INSERT [dbo].[users] OFF
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UK7y3kha7h6vhnqtpdkcm5j8fgr]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[colors] ADD  CONSTRAINT [UK7y3kha7h6vhnqtpdkcm5j8fgr] UNIQUE NONCLUSTERED
    (
    [color_code] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UKggsx4sf2c5i5d8becr67fps3d]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[colors] ADD  CONSTRAINT [UKggsx4sf2c5i5d8becr67fps3d] UNIQUE NONCLUSTERED
    (
    [color_name] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
/****** Object:  Index [UKmec5jx2swptj1qdmprm4uf8yo]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[contracts] ADD  CONSTRAINT [UKmec5jx2swptj1qdmprm4uf8yo] UNIQUE NONCLUSTERED
    (
    [order_id] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UKcumggxjbttojw3k8b7fgleib6]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKcumggxjbttojw3k8b7fgleib6] ON [dbo].[contracts]
(
	[contract_code] ASC
)
WHERE ([contract_code] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UKq7kek82ckkyl0ub1i547kg2pe]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKq7kek82ckkyl0ub1i547kg2pe] ON [dbo].[contracts]
(
	[contract_file_url] ASC
)
WHERE ([contract_file_url] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UKm3iom37efaxd5eucmxjqqcbe9]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[customers] ADD  CONSTRAINT [UKm3iom37efaxd5eucmxjqqcbe9] UNIQUE NONCLUSTERED
    (
    [phone] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UKqb9qib0slor9y4ywvrki14rs7]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[customers] ADD  CONSTRAINT [UKqb9qib0slor9y4ywvrki14rs7] UNIQUE NONCLUSTERED
    (
    [identification_number] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UKrfbvkrffamfql7cjmen8v976v]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKrfbvkrffamfql7cjmen8v976v] ON [dbo].[customers]
(
	[email] ASC
)
WHERE ([email] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UKhmlh8kxpicstcu589yif47wah]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKhmlh8kxpicstcu589yif47wah] ON [dbo].[feedbacks]
(
	[order_id] ASC
)
WHERE ([order_id] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UK496dfxr9gjswk79m76us9wo2a]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[inventory_transaction_contracts] ADD  CONSTRAINT [UK496dfxr9gjswk79m76us9wo2a] UNIQUE NONCLUSTERED
    (
    [inventory_id] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UKexycgn7g0cj09h9dfjhokjidf]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKexycgn7g0cj09h9dfjhokjidf] ON [dbo].[inventory_transaction_contracts]
(
	[contract_file_url] ASC
)
WHERE ([contract_file_url] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UKltpmnj7kj74cv4pe92edrhb46]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKltpmnj7kj74cv4pe92edrhb46] ON [dbo].[inventory_transaction_contracts]
(
	[contract_code] ASC
)
WHERE ([contract_code] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UKdhk2umg8ijjkg4njg6891trit]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKdhk2umg8ijjkg4njg6891trit] ON [dbo].[orders]
(
	[order_code] ASC
)
WHERE ([order_code] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UKl5780twvjl4c9rpqufjm9uuck]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKl5780twvjl4c9rpqufjm9uuck] ON [dbo].[orders]
(
	[contract_id] ASC
)
WHERE ([contract_id] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UK716hgxp60ym1lifrdgp67xt5k]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[roles] ADD  CONSTRAINT [UK716hgxp60ym1lifrdgp67xt5k] UNIQUE NONCLUSTERED
    (
    [role_name] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
/****** Object:  Index [UKbnxae1eoxw9ojgiuho65ts1o8]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[test_drive_configs] ADD  CONSTRAINT [UKbnxae1eoxw9ojgiuho65ts1o8] UNIQUE NONCLUSTERED
    (
    [store_id] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UK7ii4hpkuc8h5sccqt5wfky4na]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UK7ii4hpkuc8h5sccqt5wfky4na] ON [dbo].[transactions]
(
	[transaction_ref] ASC
)
WHERE ([transaction_ref] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UKega8if0k2jsx0nbaddiutruox]    Script Date: 24/11/2025 1:03:02 SA ******/
CREATE UNIQUE NONCLUSTERED INDEX [UKega8if0k2jsx0nbaddiutruox] ON [dbo].[transactions]
(
	[bank_transaction_code] ASC
)
WHERE ([bank_transaction_code] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UK6dotkott2kjsp8vw4d0m25fb7]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [UK6dotkott2kjsp8vw4d0m25fb7] UNIQUE NONCLUSTERED
    (
    [email] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
    SET ANSI_PADDING ON
    GO
/****** Object:  Index [UKdu5v5sr43g5bfnji4vb8hg5s3]    Script Date: 24/11/2025 1:03:02 SA ******/
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [UKdu5v5sr43g5bfnji4vb8hg5s3] UNIQUE NONCLUSTERED
    (
    [phone] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
    GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD  CONSTRAINT [FK88083ngr9rv9wj4p916pj40c2] FOREIGN KEY([staff_id])
    REFERENCES [dbo].[users] ([user_id])
    GO
ALTER TABLE [dbo].[appointments] CHECK CONSTRAINT [FK88083ngr9rv9wj4p916pj40c2]
    GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD  CONSTRAINT [FKjbco6wecyw596k6e5a9ly6247] FOREIGN KEY([store_id])
    REFERENCES [dbo].[stores] ([store_id])
    GO
ALTER TABLE [dbo].[appointments] CHECK CONSTRAINT [FKjbco6wecyw596k6e5a9ly6247]
    GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD  CONSTRAINT [FKrlbb09f329sfsmftrh7y0yxtk] FOREIGN KEY([customer_id])
    REFERENCES [dbo].[customers] ([customer_id])
    GO
ALTER TABLE [dbo].[appointments] CHECK CONSTRAINT [FKrlbb09f329sfsmftrh7y0yxtk]
    GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD  CONSTRAINT [FKrr5acnsfiwgefxduwno3ebe2t] FOREIGN KEY([model_id])
    REFERENCES [dbo].[models] ([model_id])
    GO
ALTER TABLE [dbo].[appointments] CHECK CONSTRAINT [FKrr5acnsfiwgefxduwno3ebe2t]
    GO
ALTER TABLE [dbo].[contracts]  WITH CHECK ADD  CONSTRAINT [FK5yknvfmbs6bdjclgsgi0iv80k] FOREIGN KEY([order_id])
    REFERENCES [dbo].[orders] ([order_id])
    GO
ALTER TABLE [dbo].[contracts] CHECK CONSTRAINT [FK5yknvfmbs6bdjclgsgi0iv80k]
    GO
ALTER TABLE [dbo].[feedback_details]  WITH CHECK ADD  CONSTRAINT [FKp2b3so8s2d7ed1rruyvsee1eb] FOREIGN KEY([feedback_id])
    REFERENCES [dbo].[feedbacks] ([feedback_id])
    GO
ALTER TABLE [dbo].[feedback_details] CHECK CONSTRAINT [FKp2b3so8s2d7ed1rruyvsee1eb]
    GO
ALTER TABLE [dbo].[feedbacks]  WITH CHECK ADD  CONSTRAINT [FKbdhoov2mv332ks2m84owt5tv3] FOREIGN KEY([order_id])
    REFERENCES [dbo].[orders] ([order_id])
    GO
ALTER TABLE [dbo].[feedbacks] CHECK CONSTRAINT [FKbdhoov2mv332ks2m84owt5tv3]
    GO
ALTER TABLE [dbo].[feedbacks]  WITH CHECK ADD  CONSTRAINT [FKg5lw2csewuqx3kvqa01ngk57y] FOREIGN KEY([resolved_by])
    REFERENCES [dbo].[users] ([user_id])
    GO
ALTER TABLE [dbo].[feedbacks] CHECK CONSTRAINT [FKg5lw2csewuqx3kvqa01ngk57y]
    GO
ALTER TABLE [dbo].[feedbacks]  WITH CHECK ADD  CONSTRAINT [FKles0odmr6glac9r0jjhp3g8ji] FOREIGN KEY([created_by])
    REFERENCES [dbo].[users] ([user_id])
    GO
ALTER TABLE [dbo].[feedbacks] CHECK CONSTRAINT [FKles0odmr6glac9r0jjhp3g8ji]
    GO
ALTER TABLE [dbo].[inventory_transaction_contracts]  WITH CHECK ADD  CONSTRAINT [FKiwapurfhw9bh7u7qmb2ds3ks2] FOREIGN KEY([evm_staff_id])
    REFERENCES [dbo].[users] ([user_id])
    GO
ALTER TABLE [dbo].[inventory_transaction_contracts] CHECK CONSTRAINT [FKiwapurfhw9bh7u7qmb2ds3ks2]
    GO
ALTER TABLE [dbo].[inventory_transaction_contracts]  WITH CHECK ADD  CONSTRAINT [FKs8px920hpu3w6w8dae4mhrvvy] FOREIGN KEY([inventory_id])
    REFERENCES [dbo].[inventory_transactions] ([inventory_id])
    GO
ALTER TABLE [dbo].[inventory_transaction_contracts] CHECK CONSTRAINT [FKs8px920hpu3w6w8dae4mhrvvy]
    GO
ALTER TABLE [dbo].[inventory_transactions]  WITH CHECK ADD  CONSTRAINT [FKgb9m2di28s0kwxx6vectsxfv2] FOREIGN KEY([store_stock_id])
    REFERENCES [dbo].[store_stocks] ([stock_id])
    GO
ALTER TABLE [dbo].[inventory_transactions] CHECK CONSTRAINT [FKgb9m2di28s0kwxx6vectsxfv2]
    GO
ALTER TABLE [dbo].[model_color]  WITH CHECK ADD  CONSTRAINT [FK335gdvuwfr91rya81ahayuru] FOREIGN KEY([color_id])
    REFERENCES [dbo].[colors] ([color_id])
    GO
ALTER TABLE [dbo].[model_color] CHECK CONSTRAINT [FK335gdvuwfr91rya81ahayuru]
    GO
ALTER TABLE [dbo].[model_color]  WITH CHECK ADD  CONSTRAINT [FK9f54yam5eyuxfudm681g9dn8c] FOREIGN KEY([model_id])
    REFERENCES [dbo].[models] ([model_id])
    GO
ALTER TABLE [dbo].[model_color] CHECK CONSTRAINT [FK9f54yam5eyuxfudm681g9dn8c]
    GO
ALTER TABLE [dbo].[order_details]  WITH CHECK ADD  CONSTRAINT [FK5jx2syactke9oe9mv735t04fq] FOREIGN KEY([promotion_id])
    REFERENCES [dbo].[promotions] ([promotion_id])
    GO
ALTER TABLE [dbo].[order_details] CHECK CONSTRAINT [FK5jx2syactke9oe9mv735t04fq]
    GO
ALTER TABLE [dbo].[order_details]  WITH CHECK ADD  CONSTRAINT [FK9jlv7kwgcp5f1hg38xhqosux6] FOREIGN KEY([store_stock_id])
    REFERENCES [dbo].[store_stocks] ([stock_id])
    GO
ALTER TABLE [dbo].[order_details] CHECK CONSTRAINT [FK9jlv7kwgcp5f1hg38xhqosux6]
    GO
ALTER TABLE [dbo].[order_details]  WITH CHECK ADD  CONSTRAINT [FKjyu2qbqt8gnvno9oe9j2s2ldk] FOREIGN KEY([order_id])
    REFERENCES [dbo].[orders] ([order_id])
    GO
ALTER TABLE [dbo].[order_details] CHECK CONSTRAINT [FKjyu2qbqt8gnvno9oe9j2s2ldk]
    GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD  CONSTRAINT [FK5mmv7liaa1s59yhlwu64yo3m5] FOREIGN KEY([contract_id])
    REFERENCES [dbo].[contracts] ([contract_id])
    GO
ALTER TABLE [dbo].[orders] CHECK CONSTRAINT [FK5mmv7liaa1s59yhlwu64yo3m5]
    GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD  CONSTRAINT [FKe979ux6efhhi6ph712agy4bit] FOREIGN KEY([staff_id])
    REFERENCES [dbo].[users] ([user_id])
    GO
ALTER TABLE [dbo].[orders] CHECK CONSTRAINT [FKe979ux6efhhi6ph712agy4bit]
    GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD  CONSTRAINT [FKnqkwhwveegs6ne9ra90y1gq0e] FOREIGN KEY([store_id])
    REFERENCES [dbo].[stores] ([store_id])
    GO
ALTER TABLE [dbo].[orders] CHECK CONSTRAINT [FKnqkwhwveegs6ne9ra90y1gq0e]
    GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD  CONSTRAINT [FKpxtb8awmi0dk6smoh2vp1litg] FOREIGN KEY([customer_id])
    REFERENCES [dbo].[customers] ([customer_id])
    GO
ALTER TABLE [dbo].[orders] CHECK CONSTRAINT [FKpxtb8awmi0dk6smoh2vp1litg]
    GO
ALTER TABLE [dbo].[payments]  WITH CHECK ADD  CONSTRAINT [FKqywegtqyijw241foqfkseq1l6] FOREIGN KEY([contract_id])
    REFERENCES [dbo].[contracts] ([contract_id])
    GO
ALTER TABLE [dbo].[payments] CHECK CONSTRAINT [FKqywegtqyijw241foqfkseq1l6]
    GO
ALTER TABLE [dbo].[promotions]  WITH CHECK ADD  CONSTRAINT [FKom2fn577fpuiddjbssbq16wyu] FOREIGN KEY([store_id])
    REFERENCES [dbo].[stores] ([store_id])
    GO
ALTER TABLE [dbo].[promotions] CHECK CONSTRAINT [FKom2fn577fpuiddjbssbq16wyu]
    GO
ALTER TABLE [dbo].[promotions]  WITH CHECK ADD  CONSTRAINT [FKtgk2givmvwal3b66ad3ar1upq] FOREIGN KEY([model_id])
    REFERENCES [dbo].[models] ([model_id])
    GO
ALTER TABLE [dbo].[promotions] CHECK CONSTRAINT [FKtgk2givmvwal3b66ad3ar1upq]
    GO
ALTER TABLE [dbo].[sale_targets]  WITH CHECK ADD  CONSTRAINT [FKd61j5ysq01pfmpmjpxd12iypl] FOREIGN KEY([store_id])
    REFERENCES [dbo].[stores] ([store_id])
    GO
ALTER TABLE [dbo].[sale_targets] CHECK CONSTRAINT [FKd61j5ysq01pfmpmjpxd12iypl]
    GO
ALTER TABLE [dbo].[store_stocks]  WITH CHECK ADD  CONSTRAINT [FK41341uuxrclknay14dtust447] FOREIGN KEY([store_id])
    REFERENCES [dbo].[stores] ([store_id])
    GO
ALTER TABLE [dbo].[store_stocks] CHECK CONSTRAINT [FK41341uuxrclknay14dtust447]
    GO
ALTER TABLE [dbo].[store_stocks]  WITH CHECK ADD  CONSTRAINT [FKg6im6vhqq0oec0o449qp2vlj2] FOREIGN KEY([model_color_id])
    REFERENCES [dbo].[model_color] ([model_color_id])
    GO
ALTER TABLE [dbo].[store_stocks] CHECK CONSTRAINT [FKg6im6vhqq0oec0o449qp2vlj2]
    GO
ALTER TABLE [dbo].[test_drive_configs]  WITH CHECK ADD  CONSTRAINT [FKhaxa2924a3ocm3l3yf7nuypnu] FOREIGN KEY([store_id])
    REFERENCES [dbo].[stores] ([store_id])
    GO
ALTER TABLE [dbo].[test_drive_configs] CHECK CONSTRAINT [FKhaxa2924a3ocm3l3yf7nuypnu]
    GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD  CONSTRAINT [FKmt44qv8av8abvaqb5nbhjnmi2] FOREIGN KEY([payment_id])
    REFERENCES [dbo].[payments] ([payment_id])
    GO
ALTER TABLE [dbo].[transactions] CHECK CONSTRAINT [FKmt44qv8av8abvaqb5nbhjnmi2]
    GO
ALTER TABLE [dbo].[users]  WITH CHECK ADD  CONSTRAINT [FK7wra86jadsraitoewujbjj1pd] FOREIGN KEY([store_id])
    REFERENCES [dbo].[stores] ([store_id])
    GO
ALTER TABLE [dbo].[users] CHECK CONSTRAINT [FK7wra86jadsraitoewujbjj1pd]
    GO
ALTER TABLE [dbo].[users]  WITH CHECK ADD  CONSTRAINT [FKp56c1712k691lhsyewcssf40f] FOREIGN KEY([role_id])
    REFERENCES [dbo].[roles] ([role_id])
    GO
ALTER TABLE [dbo].[users] CHECK CONSTRAINT [FKp56c1712k691lhsyewcssf40f]
    GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD CHECK  (([status]>=(0) AND [status]<=(4)))
    GO
ALTER TABLE [dbo].[contracts]  WITH CHECK ADD CHECK  (([status]='EXPIRED' OR [status]='CANCELLED' OR [status]='COMPLETED' OR [status]='FULLY_PAID' OR [status]='DEPOSIT_PAID' OR [status]='SIGNED' OR [status]='PENDING' OR [status]='DRAFT'))
    GO
ALTER TABLE [dbo].[feedback_details]  WITH CHECK ADD CHECK  (([category]>=(0) AND [category]<=(4)))
    GO
ALTER TABLE [dbo].[feedbacks]  WITH CHECK ADD CHECK  (([status]='REJECTED' OR [status]='RESOLVED' OR [status]='IN_PROGRESS' OR [status]='PENDING' OR [status]='DRAFT'))
    GO
ALTER TABLE [dbo].[inventory_transaction_contracts]  WITH CHECK ADD CHECK  (([status]='SIGNED' OR [status]='EVM_SIGNED' OR [status]='DRAFT'))
    GO
ALTER TABLE [dbo].[inventory_transactions]  WITH CHECK ADD CHECK  (([status]='CANCELLED' OR [status]='DELIVERED' OR [status]='IN_TRANSIT' OR [status]='PAYMENT_CONFIRMED' OR [status]='FILE_UPLOADED' OR [status]='REJECTED' OR [status]='CONTRACT_SIGNED' OR [status]='EVM_SIGNED' OR [status]='CONFIRMED' OR [status]='PENDING'))
    GO
ALTER TABLE [dbo].[models]  WITH CHECK ADD CHECK  (([body_type]='CROSSOVER' OR [body_type]='PICKUP_TRUCK' OR [body_type]='VAN' OR [body_type]='WAGON' OR [body_type]='CONVERTIBLE' OR [body_type]='COUPE' OR [body_type]='HATCHBACK' OR [body_type]='SUV' OR [body_type]='SEDAN'))
    GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD CHECK  (([status]='CANCELLED' OR [status]='DELIVERED' OR [status]='FULLY_PAID' OR [status]='DEPOSIT_PAID' OR [status]='CONTRACT_SIGNED' OR [status]='CONTRACT_PENDING' OR [status]='CONFIRMED' OR [status]='PENDING' OR [status]='DRAFT'))
    GO
ALTER TABLE [dbo].[payments]  WITH CHECK ADD CHECK  (([payment_method]='CASH' OR [payment_method]='VNPAY'))
    GO
ALTER TABLE [dbo].[payments]  WITH CHECK ADD CHECK  (([payment_type]='BALANCE' OR [payment_type]='DEPOSIT'))
    GO
ALTER TABLE [dbo].[payments]  WITH CHECK ADD CHECK  (([status]='CANCELLED' OR [status]='COMPLETED' OR [status]='INCOMPLETE' OR [status]='DRAFT'))
    GO
ALTER TABLE [dbo].[promotions]  WITH CHECK ADD CHECK  (([promotion_type]='FIXED_AMOUNT' OR [promotion_type]='PERCENTAGE'))
    GO
ALTER TABLE [dbo].[sale_targets]  WITH CHECK ADD CHECK  (([status]='NOT_MET' OR [status]='MET'))
    GO
ALTER TABLE [dbo].[stores]  WITH CHECK ADD CHECK  (([status]='INACTIVE' OR [status]='ACTIVE'))
    GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD CHECK  (([gateway]='MOMO' OR [gateway]='VNPAY'))
    GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD CHECK  (([status]='FAILED' OR [status]='SUCCESS' OR [status]='PENDING'))
    GO
ALTER TABLE [dbo].[users]  WITH CHECK ADD CHECK  (([status]='DISABLED' OR [status]='ACTIVE' OR [status]='PENDING'))
    GO
    USE [master]
    GO
ALTER DATABASE [SaleElectricVehicleManagement] SET  READ_WRITE
GO
