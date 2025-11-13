CREATE TABLE oauth2_authorization (
    id VARCHAR(100) PRIMARY KEY,
    registered_client_id VARCHAR(100) NOT NULL,
    principal_name VARCHAR(200) NOT NULL,
    authorization_grant_type VARCHAR(100) NOT NULL,
    authorized_scopes VARCHAR(1000),
    attributes TEXT,
    state VARCHAR(500),

    authorization_code_value BLOB,
    authorization_code_issued_at DATETIME,
    authorization_code_expires_at DATETIME,
    authorization_code_metadata TEXT,

    access_token_value BLOB,
    access_token_issued_at DATETIME,
    access_token_expires_at DATETIME,
    access_token_metadata TEXT,
    access_token_type VARCHAR(100),
    access_token_scopes VARCHAR(1000),

    refresh_token_value BLOB,
    refresh_token_issued_at DATETIME,
    refresh_token_expires_at DATETIME,
    refresh_token_metadata TEXT,

    oidc_id_token_value BLOB,
    oidc_id_token_issued_at DATETIME,
    oidc_id_token_expires_at DATETIME,
    oidc_id_token_metadata TEXT,

    user_code_value BLOB,
    user_code_issued_at DATETIME,
    user_code_expires_at DATETIME,
    user_code_metadata TEXT,

    device_code_value BLOB,
    device_code_issued_at DATETIME,
    device_code_expires_at DATETIME,
    device_code_metadata TEXT
);