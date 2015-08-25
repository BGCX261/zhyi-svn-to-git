create table bsuser (
    uname varchar(8) primary key,
    password char(32) not null,
    ugroup varchar(5) not null,
    question varchar(64) not null,
    answer char(32) not null
);
-- The default administrator
-- username: admin
-- password: bsadmin
-- question: What's this?
-- answer:   It's Max!
insert into bsuser values('admin', 'ba8b330942354fc3fa997008745999ae',
        'ADMIN', 'What''s this?', '970b92e7f546271b9ea43163cb6b1c62');

create table publisher (
    id char(36) primary key,
    pname varchar(48) unique not null,
    info varchar(1024),
    version integer not null
);

create table book (
    id char(36) primary key,
    title varchar(96) not null,
    pubdate date,
    info varchar(1024),
    filename varchar(255),
    popularity integer not null,
    rate integer not null,
    version integer not null,
    publisher_id char(36) references publisher(id)
);

create table author (
    id char(36) primary key,
    aname varchar(32) not null,
    info varchar(1024),
    version integer not null
);

create table tag (
    id varchar(16) primary key,
    info varchar(1024),
    version integer not null
);

create table book_author (
    book_id char(36) references book(id) not null,
    author_id char(36) references author(id) not null
);

create table book_tag (
    book_id char(36) references book(id) not null,
    tag_id varchar(16) references tag(id) not null
);

create table comment (
    id char(36) primary key,
    content varchar(1024) not null,
    user_uname varchar(8) references bsuser(uname) not null,
    book_id char(36) references book(id) not null
);

--create table deletionschedule (
--    id integer primary key,
--    book_id integer references book(id) not null,
--    deleter_id integer references bookzuser(id) not null,
--    begintime time not null,
--    deletedtime time,
--    status varchar(16) not null
--);
