drop table if exists Subject cascade;
create table Subject(
id serial not null,
name text not null,
timeStamp int,
primary key (id));
drop table if exists UserAccount cascade;
create table UserAccount(
id serial not null,
loginName text not null,
password text not null,
firstName text not null,
lastName text not null,
email text not null,
timeStamp int,
primary key (id),
unique (loginName));
drop table if exists DatabasePool cascade;
create table DatabasePool(
id serial not null,
name text not null,
url text not null,
username text not null,
password text not null,
timeStamp int,
primary key (id));
drop table if exists UpdateMode cascade;
create table UpdateMode(
inUpdateMode boolean not null,
timeStamp int,
primary key (inUpdateMode));
insert into UpdateMode values (false, null);
drop table if exists Admin cascade;
create table Admin(
userId int not null,
timeStamp int,
primary key (userId),
foreign key (userId) references UserAccount(id));
drop table if exists News cascade;
create table News(
id serial not null,
title text,
text text,
endDate int,
startDate int,
timeStamp int,
primary key (id));
drop table if exists Teacher cascade;
create table Teacher(
userId int not null,
timeStamp int,
primary key (userId),
foreign key (userId) references UserAccount(id));
drop table if exists Property cascade;
create table Property(
type int not null,
refId int not null,
key int not null,
value text,
timeStamp int,
primary key (type, refId, key));
drop table if exists Student cascade;
create table Student(
userId int not null,
studentNumber text,
timeStamp int,
primary key (userId),
foreign key (userId) references UserAccount(id));
drop table if exists Task cascade;
create table Task(
id serial not null,
text text,
showTextInParent boolean not null,
status int not null,
name text,
rootTaskId int,
componentBits int not null,
timeStamp int,
oldText text,
primary key (id),
foreign key (rootTaskId) references Task(id));
drop table if exists Log cascade;
create table Log(
taskId int,
userId int,
event int not null,
par1 int,
par2 int,
address text,
timeStamp int,
courseTaskId int,
foreign key (userId) references UserAccount(id),
foreign key (taskId) references Task(id),
foreign key (courseTaskId) references Task(id));
drop table if exists RightsCluster cascade;
create table RightsCluster(
id serial not null,
taskId int not null,
ownerViewBits int not null,
ownerUpdateBits int not null,
ownerCreateBits int not null,
ownerDeleteBits int not null,
timeStamp int,
generalViewBits int not null,
generalUpdateBits int not null,
generalCreateBits int not null,
generalDeleteBits int not null,
type int not null,
primary key (id),
foreign key (taskId) references Task(id));
drop table if exists Permission cascade;
create table Permission(
id serial not null,
userRefId int,
userRefType int not null,
taskId int not null,
type int not null,
startDate int,
endDate int,
detail text,
timeStamp int,
primary key (id),
foreign key (taskId) references Task(id));
drop table if exists CourseImplementation cascade;
create table CourseImplementation(
masterTaskId int not null,
subjectId int,
databaseId int not null,
status int,
timeStamp int,
courseTaskId int not null,
primary key (masterTaskId),
foreign key (subjectId) references Subject(id),
foreign key (masterTaskId) references Task(id),
foreign key (databaseId) references DatabasePool(id));
drop table if exists ClusterMember cascade;
create table ClusterMember(
clusterId int not null,
userId int not null,
timeStamp int,
primary key (clusterId, userId),
foreign key (userId) references UserAccount(id),
foreign key (clusterId) references RightsCluster(id));
drop view if exists CourseView;
create view CourseView(masterTaskId, courseTaskId, name, subjectId, subjectName, databaseId)
 as select ci.mastertaskid, ci.coursetaskid, t.name, ci.subjectid, s.name, ci.databaseid from task as t, courseimplementation as ci, subject as s
   where t.id=ci.masterTaskId AND ci.subjectId=s.id;
drop view if exists UserTaskView;
create view UserTaskView(userId, loginName, firstName, lastName, email, taskId, clusterType)
 as select distinct ua.id, ua.loginname, ua.firstname, ua.lastname, ua.email, t.id, rc.type from useraccount as ua, task as t, rightscluster as rc, clustermember as cm
   where t.rootTaskId=rc.taskId AND rc.id=cm.clusterId AND cm.userId=ua.id;