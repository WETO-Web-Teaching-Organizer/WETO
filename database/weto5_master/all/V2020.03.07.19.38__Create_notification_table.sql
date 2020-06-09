CREATE TABLE Notification (
  id serial not null,
  userId integer not null,
  courseId integer not null,
  type text not null,
  message text not null,
  createdAt int not null,
  readByUser boolean DEFAULT false,
  sentByEmail boolean DEFAULT false,
  primary key (id),
  foreign key (userId) references UserAccount(id),
  foreign key (courseId) references CourseImplementation(mastertaskid)
);