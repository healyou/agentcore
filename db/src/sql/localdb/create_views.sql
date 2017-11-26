----------- message type view -----------
CREATE VIEW IF NOT EXISTS service_message_v
  AS
    SELECT
      service_message.id,
      service_message.json_object,
      service_message.send_agent_type_codes,
      service_message.sender_code,
      service_message.create_date,
      service_message.use_date,
      service_message.object_type_id,
      service_message.system_agent_id,
      smot.code as message_object_type_code,
      smot.name as message_object_type_name,
      smot.is_deleted as message_object_type_is_deleted,
      service_message.message_type_id,
      smt.code as message_type_code,
      smt.name as message_type_name,
      smt.is_deleted as message_type_is_deleted
    FROM
      service_message
      INNER JOIN service_message_object_type as smot ON service_message.object_type_id = smot.id
      INNER JOIN service_message_type as smt ON service_message.message_type_id = smt.id;