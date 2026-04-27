-- ChatMetric schema (MySQL 8.0)

-- 1) 权限与组织
CREATE TABLE IF NOT EXISTS users (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  username     VARCHAR(64) NOT NULL,
  org_code     VARCHAR(32) NOT NULL,
  org_name     VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles (
  role_code    VARCHAR(32) PRIMARY KEY,
  role_name    VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_role_rel (
  user_id      BIGINT NOT NULL,
  role_code    VARCHAR(32) NOT NULL,
  PRIMARY KEY (user_id, role_code)
);

CREATE TABLE IF NOT EXISTS menus (
  menu_code    VARCHAR(32) PRIMARY KEY,
  menu_name    VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS role_menu_rel (
  role_code    VARCHAR(32) NOT NULL,
  menu_code    VARCHAR(32) NOT NULL,
  PRIMARY KEY (role_code, menu_code)
);

-- 2) 机构维指标（窄表模式）
CREATE TABLE IF NOT EXISTS org_metric_desc (
  metric_id          VARCHAR(64) PRIMARY KEY,
  metric_name        VARCHAR(128) NOT NULL,
  table_name         VARCHAR(128) NOT NULL,
  metric_logic_desc  TEXT NOT NULL
);

-- 注意：这里用统一明细表名 org_metric_data（也可按 table_name 分表）
CREATE TABLE IF NOT EXISTS org_metric_data (
  data_date     DATE NOT NULL,
  org_code      VARCHAR(32) NOT NULL,
  org_name      VARCHAR(128) NOT NULL,
  metric_id     VARCHAR(64) NOT NULL,
  metric_name   VARCHAR(128) NOT NULL,
  metric_value  DECIMAL(38, 8) NOT NULL,
  PRIMARY KEY (data_date, org_code, metric_id)
);

-- 3) 客户维指标（宽表模式）
CREATE TABLE IF NOT EXISTS cust_metric_desc (
  metric_id          VARCHAR(64) PRIMARY KEY,
  metric_name        VARCHAR(128) NOT NULL,
  table_name         VARCHAR(128) NOT NULL,
  column_name        VARCHAR(128) NOT NULL,
  metric_logic_desc  TEXT NOT NULL
);

-- 客户宽表按你的业务实际创建：以 cust_id 为主键，dt 为日期/分区字段
-- 示例：
-- CREATE TABLE cust_metric_wide_xxx (
--   cust_id BIGINT PRIMARY KEY,
--   dt DATE NOT NULL,
--   some_metric DECIMAL(38, 8) NULL,
--   ...
-- );

