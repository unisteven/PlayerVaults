CREATE TABLE pv_Vault(
uuid VARCHAR(255) NOT NULL,
page INTEGER NOT NULL,
items LONGTEXT,
PRIMARY KEY (uuid, page)
);

CREATE TABLE pv_Version(
  version INTEGER DEFAULT 1
);