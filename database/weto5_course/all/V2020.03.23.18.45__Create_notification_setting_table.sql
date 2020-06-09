CREATE TABLE NotificationSetting (
  id serial not null,
  userId integer not null,
  courseId integer not null,
  type text not null,
  notifications boolean not null,
  emailNotifications boolean not null,
  primary key (id),
  foreign key (userId) references UserAccount(id),
  foreign key (courseId) references Task(id)
);