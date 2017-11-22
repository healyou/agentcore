------------------ parameter data ------------------
INSERT INTO parameter (key, value) VALUES
  ('agent.service.base.url', 'http://127.0.0.1:9999'),
  ('agent.service.login.login.url', '/login/login'),
  ('agent.service.login.registration.url', '/login/registration'),
  ('agent.service.login.logout.url', '/login/logout'),
  ('agent.service.password', 'psw'),
  ('agent.service.agent.get.current.agent.url', '/agent/getCurrentAgent'),
  ('agent.service.agent.get.agents.url', '/agent/getAgents'),
  ('agent.service.message.send.message.url', '/message/sendMessage'),
  ('agent.service.message.get.messages.url', '/message/getMessages'),
  ('agent.service.type.get.agent.types.url', '/type/getAgentTypes'),
  ('agent.service.type.get.message.body.types.url', '/type/getMessageBodyTypes'),
  ('agent.service.type.get.message.goal.types.url', '/type/getMessageGoalTypes'),
  ('agent.service.type.get.message.types.url', '/type/getMessageTypes');

------------------ systemAgent data ------------------
INSERT INTO system_agent (service_login, service_password, send_agent_type_codes, is_sendandget_messages) VALUES
  ('masId', 'psw', 'worker!server', 'Y'),
  ('test1', 'psw', 'worker', 'Y');

------------------ service_message_type data ------------------
INSERT INTO service_message_type (code, name) VALUES
  ('send', 'Отправка сообщения'),
  ('get', 'Получение сообщения');

------------------ agentType data ------------------
INSERT INTO service_message_object_type (code, name) VALUES
  ('get_service_message', 'Сообщение сервиса'),
  ('send_message_data', 'Объект отправки сообщения в сервис');

------------------ agentType data ------------------
INSERT INTO service_message (json_object, object_type_id, message_type_id, use_date, system_agent_id) VALUES
  ('{test_json}', 1, 2, strftime('%Y-%m-%d %H:%M:%f'), 1);