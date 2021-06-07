
create table user_details
(
    -- user_id  varchar(50) PRIMARY KEY NOT NULL,

    user_id  varchar(50) PRIMARY KEY REFERENCES users(user_id),
 
    first_name varchar(50),
    last_name varchar(50),
    title varchar(50),
    email varchar(50) UNIQUE,
    primary_phone varchar(50),
    mobile_phone varchar(50)
);

create index idx_user_details_user_id
  on user_details(user_id);


