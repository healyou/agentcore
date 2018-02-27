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
  owner_id          INTEGER               NOT NULL, -- Владелец агента
  create_user_id    INTEGER               NOT NULL, -- Пользователь, создавший агента
  create_date       TEXT                  NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  update_date       TEXT                  , -- Дата редактирования
  is_deleted TEXT NOT NULL DEFAULT ('N') CHECK(is_deleted='N' OR is_deleted='Y'), -- Удалено ли значение
  is_sendandget_messages TEXT NOT NULL DEFAULT ('Y') CHECK(is_deleted='N' OR is_deleted='Y'), -- Необходимо ли для этого агента получать и отправлять сообщения в сервисе
  FOREIGN KEY(owner_id) REFERENCES users(id),
  FOREIGN KEY(create_user_id) REFERENCES users(id)
);

------------------ service message table ------------------
CREATE TABLE if not exists service_message
-- Таблицы сообщений сервиса агентов
(
  id INTEGER PRIMARY KEY AUTOINCREMENT    NOT NULL, -- Идетификатор
  json_object     TEXT                    NOT NULL, -- Объект, полученный или отправляемый сообщением
  message_type_id INTEGER                 NOT NULL, -- Тип сообщения
  send_agent_type_codes TEXT              , -- Типы агентов, которым отправляется сообщение(через знак '!')
  sender_code     TEXT                    , -- Тип агента, отправившего сообщение
  message_type    TEXT                    , -- Тип сообщения в RestApi
  message_body_type TEXT                  , -- Тип тела сообщения
  create_date     TEXT                    NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  use_date        TEXT                    , -- Дата использования сообщения(отправка или чтение)
  system_agent_id INTEGER                 NOT NULL,
  FOREIGN KEY(message_type_id) REFERENCES service_message_type(id),
  FOREIGN KEY(system_agent_id) REFERENCES system_agent(id)
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

----------------------- system agent event history -----------------------
CREATE TABLE if not exists system_agent_event_history
  -- Таблицы истории поведения агента
(
  id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  agent_id       TEXT                              NOT NULL, -- Идентификатор системного агента
  create_date    TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  message        TEXT                              NOT NULL,-- Сообщение
  FOREIGN KEY(agent_id) REFERENCES system_agent(id)
);

----------------------- dsl file -----------------------
CREATE TABLE if not exists dsl_file
  -- Таблицы с файлами работы агента
(
  id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  agent_id       TEXT                              NOT NULL, -- Идентификатор системного агента
  filename       TEXT                              NOT NULL, -- Имя файла
  data           BLOB                              NOT NULL, -- Данные
  length         INTEGER                           NOT NULL, -- Размер данных
  create_date    TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  end_date       TEXT                              , -- Дата завершения работы текущего файла
  FOREIGN KEY(agent_id) REFERENCES system_agent(id)
);

----------------------------- ПОЛЬЗОВАТЕЛИ -----------------------------------------------------------------------------

----------------------- users -----------------------
CREATE TABLE if not exists users
  -- Таблица пользователей
(
  id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  login          TEXT                              NOT NULL, -- Идентификатор системного агента
  password       TEXT                              NOT NULL, -- Имя файла
  create_date    TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания
  end_date       TEXT                              -- Дата отключения пользователя
);

----------------------- role -----------------------
CREATE TABLE if not exists role
  -- Таблица ролей
(
  id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  name           TEXT                              NOT NULL UNIQUE, -- Наименование роли
  description    TEXT                              NOT NULL, -- Описание роли
  create_date    TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')) -- Дата создания роли
);

----------------------- user role -----------------------
CREATE TABLE if not exists user_role
  -- Таблица ролей пользователя
(
  id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  user_id        INTEGER                           NOT NULL, -- Идентификатор пользователя
  role_id        INTEGER                           NOT NULL, -- Идентификатор роли
  create_date    TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания роли пользователя
  start_date     TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата начала действия роли
  end_date       TEXT                              , -- Окончание действия роли
  FOREIGN KEY(user_id) REFERENCES users(id),
  FOREIGN KEY(role_id) REFERENCES role(id)
);

----------------------- privilege -----------------------
CREATE TABLE if not exists privilege
  -- Таблица привилегий
(
  id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  code           TEXT                              NOT NULL UNIQUE, -- Код привилегии
  name           TEXT                              NOT NULL, -- Наименование привилегии
  description    TEXT                              , -- Описание привилегии
  create_date    TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')) -- Дата создания привилегии
);

----------------------- role privilege -----------------------
CREATE TABLE if not exists role_privilege
  -- Таблица привилегий для ролей
(
  id             INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, -- Идентификатор
  role_id        INTEGER                           NOT NULL, -- Идентификатор роли
  privilege_id   INTEGER                           NOT NULL, -- Идентификатор привилегии
  create_date    TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата создания привилегии для роли
  start_date     TEXT                              NOT NULL DEFAULT (strftime('%Y-%m-%d %H:%M:%f')), -- Дата начала привилегии для роли
  end_date       TEXT                              , -- Окончание действия привилегии для роли
  FOREIGN KEY(role_id) REFERENCES user_role(id),
  FOREIGN KEY(privilege_id) REFERENCES privilege(id)
);