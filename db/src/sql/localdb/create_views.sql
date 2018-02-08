CREATE VIEW IF NOT EXISTS service_message_v
  -- Сообщения в системе
  AS
    SELECT
      service_message.id,
      service_message.json_object,
      service_message.send_agent_type_codes,
      service_message.sender_code,
      service_message.create_date,
      service_message.use_date,
      service_message.system_agent_id,
      service_message.message_type_id,
      service_message.message_type,
      service_message.message_body_type,
      smt.code as message_type_code,
      smt.name as message_type_name,
      smt.is_deleted as message_type_is_deleted
    FROM
      service_message
      INNER JOIN service_message_type as smt ON service_message.message_type_id = smt.id;


CREATE VIEW IF NOT EXISTS user_privileges_v
  -- Текущие привилегии пользователя
  AS
    SELECT
      u.id,
      u.login,
      priv.privilege_id,
      p.code AS privilege_code,
      p.name AS privilege_name
    FROM (((SELECT DISTINCT
              ur.user_id,
              rp.privilege_id
            FROM ((user_role ur
              JOIN role r ON ((r.id = ur.role_id)))
              JOIN role_privilege rp ON ((rp.role_id = ur.role_id)))
            WHERE ((strftime('%Y-%m-%d %H:%M:%f') >= ur.start_date) AND (strftime('%Y-%m-%d %H:%M:%f') <= ur.end_date OR ur.end_date IS NULL))) priv
      JOIN users u ON ((u.id = priv.user_id)))
      JOIN privilege p ON ((p.id = priv.privilege_id)));


CREATE VIEW IF NOT EXISTS system_agent_v
  -- Данные об агенте
  AS
    SELECT
      sa.id,
      sa.service_login,
      sa.service_password,
      sa.owner_id,
      ou.login as owner_login,
      ou.password as owner_password,
      ou.create_date as owner_create_date,
      ou.end_date as owner_end_date,
      sa.create_user_id,
      cu.login as create_user_login,
      cu.password as create_user_password,
      cu.create_date as create_user_create_date,
      cu.end_date as create_user_end_date,
      sa.create_date,
      sa.update_date,
      sa.is_deleted,
      sa.is_sendandget_messages
    FROM system_agent sa
      LEFT JOIN users ou ON sa.owner_id = ou.id
      LEFT JOIN users cu ON sa.create_user_id = cu.id;