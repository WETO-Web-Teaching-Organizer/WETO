CREATE TABLE NotificationTemplate (
  id serial not null,
  type text not null,
  template text not null,
  primary key (id)
);

INSERT INTO NotificationTemplate (type, template) VALUES ('forum_post', '&user; posted a response to the forum &forumTitle; with message &message;');