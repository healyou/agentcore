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