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

------------------ service message table ------------------
CREATE TABLE if not exists service_message
-- Таблицы сообщений сервиса агентов
(
  id INTEGER PRIMARY KEY AUTOINCREMENT    NOT NULL, -- Идетификатор
  json_object     TEXT                    NOT NULL, -- Объект, полученный или отправляемый сообщением
  object_type_id  INTEGER                 NOT NULL, -- Тип агента
  message_type_id INTEGER                 NOT NULL, -- Тип сообщения
  create_date     TEXT                    NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  use_date        TEXT                    DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата использования сообщения(отправка или чтение)
  FOREIGN KEY(object_type_id) REFERENCES service_message_object_type(id),
  FOREIGN KEY(message_type_id) REFERENCES service_message_type(id)
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