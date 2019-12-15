insert into useraccount (loginname, password, firstname, lastname, email) values ('ope', '1000:514aa8d861d94c29fcf3644c955f4668bb83ce512017a06f:08703590a4420c70747af05a5d9b7fb40c9611cf6f609034', 'Ope', 'Ope', 'ope@o.pe');
insert into databasepool (name, url, username, password) values ('kurssi', 'jdbc:postgresql://localhost/weto5_kurssi', 'weto', 'weto');
insert into admin select last_value from useraccount_id_seq;
insert into subject (name) values ('Computer Science');
