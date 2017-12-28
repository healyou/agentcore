------------------ parameter data ------------------
INSERT INTO parameter (key, value) VALUES
  ('agent.service.base.url', 'http://127.0.0.1:9999'),
  ('agent.service.login.login.url', '/login/login'),
  ('agent.service.login.registration.url', '/login/registration'),
  ('agent.service.login.logout.url', '/login/logout'),
  ('agent.service.password', 'psw'),
  ('agent.service.agent.get.current.agent.url', '/agent/getCurrentAgent'),
  ('agent.service.agent.get.agents.url', '/agent/getAgents'),
  ('agent.service.agent.get.agent.url', '/agent/getAgent'),
  ('agent.service.message.send.message.url', '/message/sendMessage'),
  ('agent.service.message.get.messages.url', '/message/getMessages'),
  ('agent.service.type.get.agent.types.url', '/type/getAgentTypes'),
  ('agent.service.type.get.message.body.types.url', '/type/getMessageBodyTypes'),
  ('agent.service.type.get.message.goal.types.url', '/type/getMessageGoalTypes'),
  ('agent.service.type.get.message.types.by.goal.type.url', '/type/getMessageTypes'),
  ('agent.service.type.get.message.types.url', '/type/getMessageTypes');

------------------ service_message_type data ------------------
INSERT INTO service_message_type (code, name) VALUES
  ('send', 'Отправка сообщения'),
  ('get', 'Получение сообщения');

----------------------------- ПОЛЬЗОВАТЕЛИ И ПРИВИЛЕГИИ ----------------------------------------------------------------
------------------ privilege data ------------------
INSERT INTO privilege (code, name, description) VALUES
  ('create_agent', 'Создание агента', 'Возможность создания агента'),--1
  ('view_all_agents', 'Просмотр списка всех агентов', 'Возможность просмотра списка всех агентов'),--2
  ('view_own_agents', 'Просмотр списка своих агентов', 'Возможность просмотра списка своих агентов'),--3
  ('edit_own_agent', 'Редактирование данных своего агента', 'Возможность редактирование данных своего агента');--4

------------------ role data ------------------
INSERT INTO role (name, description) VALUES
  ('Администратор', 'Выполнение всех действий'),--1
  ('Наблюдатель', 'Просмотр данных без внесения изменений');--2

------------------ role privilege data ------------------
INSERT INTO role_privilege (role_id, privilege_id) VALUES
  --Администратор
  (1, 1),--create_agent
  (1, 2),--view_all_agents
  (1, 3),--view_own_agents
  (1, 4),--edit_own_agent
  --Наблюдатель
  (2, 2),--view_all_agents
  (2, 3);--view_own_agents

------------------ users data ------------------
INSERT INTO users (login, password) VALUES
  ('admin', 'admin'),--1
  ('viewer', 'viewer');--2

------------------ user role ------------------
INSERT INTO user_role (user_id, role_id) VALUES
  (1, 1),--admin
  (2, 2);--viewer