create table oauth_client_details (
  client_id varchar(256) not null,
  client_secret varchar(256) not null,
  web_server_redirect_uri varchar(2048) default null,
  scope varchar(256) default null,
  access_token_validity integer default null,
  refresh_token_validity integer default null,
  resource_ids varchar(1024) default null,
  authorized_grant_types varchar(1024) default null,
  authorities varchar(1024) default null,
  additional_information varchar(4096) default null,
  autoapprove varchar(256) default null,
  constraint pk_oauth_client_details primary key (client_id)
);

create table permission (
  id serial,
  name varchar(512) default null,
  constraint pk_permission primary key (id),
  constraint uk_permission unique (name)
);

create table role (
  id serial,
  name varchar(255) default null,
  constraint pk_role primary key (id),
  constraint uk_role unique (name)
)  ;

create table oauth_user (
  id serial,
  username varchar(100) not null,
  password varchar(1024) not null,
  email varchar(1024) not null,
  enabled smallint not null,
  account_non_expired smallint not null,
  credentials_non_expired smallint not null,
  account_non_locked smallint not null,
  constraint pk_user primary key (id),
  constraint uk_user unique (username)
) ;


create table permission_role (
  permission_id serial,
  role_id serial,
  constraint fk1_permission_role foreign key (permission_id) references permission (id),
  constraint fk2_permission_role foreign key (role_id) references role (id),
  constraint pk_permission_role primary key (permission_id,role_id)
) ;



create table role_user (
  role_id serial,
  user_id serial,
  constraint fk1_role_user foreign key (role_id) references role (id),
  constraint fk2_role_user foreign key (user_id) references oauth_user (id),
  constraint pk_role_user primary key (role_id,user_id)
) ;

-- token store
create table oauth_client_token (
  token_id VARCHAR(256),
  token bytea,
  authentication_id VARCHAR(256),
  user_name VARCHAR(256),
  client_id VARCHAR(256)
);

create table oauth_access_token (
  token_id VARCHAR(256),
  token bytea,
  authentication_id VARCHAR(256),
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication bytea,
  refresh_token VARCHAR(256)
);

create table oauth_refresh_token (
  token_id VARCHAR(256),
  token bytea,
  authentication bytea
);

create table oauth_code (
  code VARCHAR(256),
  authentication bytea
);

create table oauth_approvals (
	user_id VARCHAR(256),
	client_id VARCHAR(256),
	scope VARCHAR(256),
	status VARCHAR(10),
	expires_at TIMESTAMP,
	last_modified_at TIMESTAMP
);
