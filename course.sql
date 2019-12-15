drop table Document cascade;
create table Document(
id serial not null,
fileName text not null,
fileSize int not null,
mimeType text not null,
contentFileSize int not null,
contentMimeType text not null,
contentId int not null,
timeStamp int,
primary key (id));
drop table UserAccount cascade;
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
drop table UserIdReplication cascade;
create table UserIdReplication(
masterDbUserId int not null,
courseDbUserId int not null,
timeStamp int,
primary key (masterDbUserId, courseDbUserId),
foreign key (courseDbUserId) references UserAccount(id));
drop table Teacher cascade;
create table Teacher(
userId int not null,
timeStamp int,
primary key (userId),
foreign key (userId) references UserAccount(id));
drop table Property cascade;
create table Property(
type int not null,
refId int not null,
key int not null,
value text,
timeStamp int,
primary key (type, refId, key));
drop table Student cascade;
create table Student(
userId int not null,
studentNumber text,
timeStamp int,
primary key (userId),
foreign key (userId) references UserAccount(id));
drop table Task cascade;
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
drop table RightsCluster cascade;
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
drop table ClusterIdReplication cascade;
create table ClusterIdReplication(
masterDbClusterId int not null,
courseDbClusterId int not null,
timeStamp int,
primary key (masterDbClusterId, courseDbClusterId),
foreign key (courseDbClusterId) references RightsCluster(id));
drop table SubtaskLink cascade;
create table SubtaskLink(
containerId int not null,
subtaskId int not null,
rank int not null,
timeStamp int,
primary key (containerId, subtaskId),
unique (subtaskId),
foreign key (containerId) references Task(id),
foreign key (subtaskId) references Task(id));
drop table Submission cascade;
create table Submission(
id serial not null,
userId int not null,
timeStamp int not null,
autoGradeMark int,
status int not null,
taskId int not null,
message text,
error int,
fileCount int,
primary key (id),
foreign key (userId) references UserAccount(id),
foreign key (taskId) references Task(id));
drop table Permission cascade;
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
drop table PermissionIdReplication cascade;
create table PermissionIdReplication(
masterDbPermissionId int not null,
courseDbPermissionId int not null,
timeStamp int,
primary key (masterDbPermissionId, courseDbPermissionId),
foreign key (courseDbPermissionId) references Permission(id));
drop table Tag cascade;
create table Tag(
id serial not null,
authorId int not null,
status int not null,
rank int not null,
timeStamp int,
text text,
type int not null,
taggedId int not null,
primary key (id),
foreign key (authorId) references UserAccount(id));
drop table AutoGrading cascade;
create table AutoGrading(
taskId int not null,
testDocId int,
properties text not null,
timeStamp int,
primary key (taskId),
foreign key (testDocId) references Document(id),
foreign key (taskId) references Task(id));
drop table AutoGradeTestScore cascade;
create table AutoGradeTestScore(
submissionId int not null,
testNo int not null,
testScore int not null,
phase int not null,
processingTime int not null,
feedback text not null,
timeStamp int,
primary key (submissionId, testNo, phase),
foreign key (submissionId) references Submission(id));
drop table AutoGradeJobQueue cascade;
create table AutoGradeJobQueue(
id serial not null,
taskId int not null,
dbId int not null,
jobType int not null,
refId int not null,
queuePhase int not null,
testRunning boolean not null,
jobComment text,
timeStamp int not null,
primary key (id),
foreign key (taskId) references Task(id));
drop table SubmissionDocument cascade;
create table SubmissionDocument(
submissionId int not null,
documentId int not null,
timeStamp int,
primary key (submissionId, documentId),
foreign key (submissionId) references Submission(id),
foreign key (documentId) references Document(id));
drop table SubmissionProperties cascade;
create table SubmissionProperties(
taskId int not null,
properties text not null,
timeStamp int,
primary key (taskId, properties),
unique (taskId),
foreign key (taskId) references Task(id));
drop table Scoring cascade;
create table Scoring(
taskId int not null,
properties text not null,
gradeTable text,
timeStamp int,
primary key (taskId),
foreign key (taskId) references Task(id));
drop table TaskDocument cascade;
create table TaskDocument(
taskId int not null,
documentId int not null,
userId int,
status int,
timeStamp int,
primary key (taskId, documentId),
foreign key (documentId) references Document(id),
foreign key (taskId) references Task(id),
foreign key (userId) references UserAccount(id));
drop table Log cascade;
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
drop table UserGroup cascade;
create table UserGroup(
id serial not null,
name text not null,
type int not null,
taskId int not null,
timeStamp int,
primary key (id),
foreign key (taskId) references Task(id));
drop table GroupMember cascade;
create table GroupMember(
userId int not null,
groupId int not null,
taskId int not null,
timeStamp int,
primary key (userId, groupId, taskId),
foreign key (taskId) references Task(id),
foreign key (userId) references UserAccount(id),
foreign key (groupId) references UserGroup(id));
drop table Grade cascade;
create table Grade(
taskId int not null,
id serial not null,
reviewerId int,
receiverId int not null,
mark real,
status int not null,
timeStamp int,
primary key (id),
foreign key (reviewerId) references UserAccount(id),
foreign key (taskId) references Task(id),
foreign key (receiverId) references UserAccount(id));
drop table ClusterMember cascade;
create table ClusterMember(
clusterId int not null,
userId int not null,
timeStamp int,
primary key (clusterId, userId),
foreign key (userId) references UserAccount(id),
foreign key (clusterId) references RightsCluster(id));
drop view GroupView;
create view GroupView(userId, id, name, type, taskId)
 as select gm.userid, ug.id, ug.name, ug.type, ug.taskid from groupmember as gm, usergroup as ug
   where gm.groupId=ug.id;
drop view StudentView;
create view StudentView(taskId, email, userId, studentNumber, loginName, firstName, lastName)
 as select distinct t.id, ua.email, ua.id, s.studentnumber, ua.loginname, ua.firstname, ua.lastname from task as t, useraccount as ua, clustermember as cm, rightscluster as rc, student as s
   where t.rootTaskId=rc.taskId AND rc.type=0 AND rc.id=cm.clusterId AND cm.userId=ua.id AND s.userId=ua.id ORDER BY ua.lastName, ua.firstName;
drop view SubtaskView;
create view SubtaskView(rank, id, containerId, name, text, status, componentBits, showTextInParent, rootTaskId)
 as select stl.rank, stl.subtaskid, stl.containerid, t.name, t.text, t.status, t.componentbits, t.showtextinparent, t.roottaskid from subtasklink as stl, task as t
   where t.id=stl.subtaskId ORDER BY rank;
drop view TagView;
create view TagView(id, authorId, status, rank, timeStamp, text, type, taggedId, firstName, lastName)
 as select t.id, t.authorid, t.status, t.rank, t.timestamp, t.text, t.type, t.taggedid, ua.firstname, ua.lastname from tag as t, useraccount as ua
   where t.authorId=ua.id ORDER BY rank;
drop view GradeView;
create view GradeView(taskId, id, reviewerId, receiverId, mark, timeStamp, reviewerFirstName, reviewerLastName, receiverFirstName, receiverLastName, taskName)
 as select g.taskid, g.id, g.reviewerid, g.receiverid, g.mark, g.timestamp, ua1.firstname, ua1.lastname, ua2.firstname, ua2.lastname, t.name from grade as g, useraccount as ua1, useraccount as ua2, task as t
   where ua1.id=g.reviewerId AND ua2.id=g.receiverId AND t.id=g.taskId ORDER BY ua2.lastName, ua2.firstName, timeStamp;
drop view UserTaskView;
create view UserTaskView(userId, loginName, firstName, lastName, email, taskId, clusterType)
 as select distinct ua.id, ua.loginname, ua.firstname, ua.lastname, ua.email, t.id, rc.type from useraccount as ua, task as t, rightscluster as rc, clustermember as cm
   where t.rootTaskId=rc.taskId AND rc.id=cm.clusterId AND cm.userId=ua.id;
