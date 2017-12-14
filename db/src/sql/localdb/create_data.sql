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