INSERT INTO oauth_client_details (client_id, client_secret, web_server_redirect_uri, scope, access_token_validity, refresh_token_validity, resource_ids, authorized_grant_types, additional_information) VALUES
 ('mobile', '$2a$10$.nXilPpfyYleLrJFT1lAF.1FQss/UgLB4HHtlrroqRLCaX8ZafwEm', 'http://localhost:8080/code', 'READ,WRITE', '3600', '10000', null, 'authorization_code,password,refresh_token,implicit', '{}');

 INSERT INTO PERMISSION (NAME) VALUES
 ('create_profile'),
 ('read_profile'),
 ('update_profile'),
 ('delete_profile');

 INSERT INTO role (NAME) VALUES
		('PROFESSOR'),('STUDENT');

 INSERT INTO PERMISSION_ROLE (PERMISSION_ID, ROLE_ID) VALUES
     (1,1), /*create-> admin */
     (2,1), /* read admin */
     (3,1), /* update admin */
     (4,1), /* delete admin */
     (2,2),  /* read operator */
     (3,2);  /* update operator */
insert into oauth_user (id, username,password, email, enabled, account_non_expired, credentials_non_expired, account_non_locked) VALUES ('1', 'dana','$2a$10$/K4k2PWk9l5N1Mg8uBdFp.a0nJT3OxdxJHlKpQG9CScm0V185lY/u', 'dana@yahoo.com', '1', '1', '1', '1');
insert into  oauth_user (id, username,password, email, enabled, account_non_expired, credentials_non_expired, account_non_locked) VALUES ('2', 'roxana', '$2a$10$tUJSiu11slhhYj4Rv1X3/.KG4NmvWy.zyK6oeewkYIeGlxoFgUPn2','roxana@yahoo.com', '1', '1', '1', '1');

INSERT INTO ROLE_USER (ROLE_ID, USER_ID)
    VALUES
    (1, 1) /* dana-PROFESSOR */,
    (2, 2) /* roxana-STUDENT */ ;
