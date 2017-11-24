------------------ parameter table ------------------
CREATE TABLE if not exists parameter
-- Таблицы параметров в системе
(
  id INTEGER PRIMARY KEY AUTOINCREMENT   NOT NULL, -- Идетификатор
  key            TEXT                    NOT NULL UNIQUE, -- Ключ параметра
  value          TEXT                    NOT NULL, -- Значение параметра
  create_date    TEXT                    NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  is_deleted     TEXT                    NOT NULL DEFAULT ('N') CHECK(is_deleted='N' OR is_deleted='Y') -- Удалено ли значение
);

------------------ system agent table ------------------
CREATE TABLE if not exists system_agent
  -- Таблицы системных агентов(локальные агенты) нужна для идентификации запускаемых агентов и отправки сообщений
(
  id INTEGER PRIMARY KEY AUTOINCREMENT    NOT NULL, -- Идетификатор
  service_login     TEXT                  NOT NULL UNIQUE, -- Логин от сервиса
  service_password  TEXT                  NOT NULL, -- Пароль от сервиса
  send_agent_type_codes TEXT              NOT NULL, -- Типы агентов, которым отправляется сообщение(через точку с запятой в строчку)
  create_date       TEXT                  NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  update_date       TEXT                  , -- Дата редактирования
  is_deleted TEXT NOT NULL DEFAULT ('N') CHECK(is_deleted='N' OR is_deleted='Y'), -- Удалено ли значение
  is_sendandget_messages TEXT NOT NULL DEFAULT ('Y') CHECK(is_deleted='N' OR is_deleted='Y') -- Необходимо ли для этого агента получать и отправлять сообщения в сервисе
);

------------------ service message table ------------------
CREATE TABLE if not exists service_message
-- Таблицы сообщений сервиса агентов
(
  id INTEGER PRIMARY KEY AUTOINCREMENT    NOT NULL, -- Идетификатор
  json_object     TEXT                    NOT NULL, -- Объект, полученный или отправляемый сообщением
  object_type_id  INTEGER                 NOT NULL, -- Тип агента
  message_type_id INTEGER                 NOT NULL, -- Тип сообщения
  create_date     TEXT                    NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  use_date        TEXT                    , -- Дата использования сообщения(отправка или чтение)
  system_agent_id INTEGER                 NOT NULL,
  FOREIGN KEY(object_type_id) REFERENCES service_message_object_type(id),
  FOREIGN KEY(message_type_id) REFERENCES service_message_type(id),
  FOREIGN KEY(system_agent_id) REFERENCES system_agent(id)
);

----------------------- service message object type table -----------------------
CREATE TABLE if not exists service_message_object_type
  -- Таблицы типа объекта в сообщении
(
  id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  code    TEXT                              NOT NULL, -- Системное имя
  name    TEXT                              NOT NULL, -- Читаемое имя
  is_deleted TEXT NOT NULL DEFAULT ('N') CHECK(is_deleted='N' OR is_deleted='Y') -- Удалено ли значение
);

----------------------- service message type table -----------------------
CREATE TABLE if not exists service_message_type
  -- Таблицы типа сообщения сервиса
(
  id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  code    TEXT                              NOT NULL, -- Системное имя
  name    TEXT                              NOT NULL, -- Читаемое имя
  is_deleted TEXT NOT NULL DEFAULT ('N') CHECK(is_deleted='N' OR is_deleted='Y') -- Удалено ли значение
);