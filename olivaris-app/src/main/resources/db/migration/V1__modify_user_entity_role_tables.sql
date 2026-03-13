-- Remove permissions system (entity_permission and relation table)
DROP TABLE IF EXISTS user_entity_permission;
DROP TABLE IF EXISTS entity_permission;

-- Update PK in user_entity_role 
ALTER TABLE user_entity_role
DROP CONSTRAINT user_entity_role_user_id_enabled_entity_id_key;

ALTER TABLE user_entity_role
ADD CONSTRAINT user_entity_role_unique
UNIQUE (user_id, enabled_entity_id, entity_role_id);

-- Update user permission to entity on user_entity_role table for the farmer user
UPDATE user_entity_role
SET 
    read_cue = true, 
    read_rea = true, 
    write_cue = true, 
    write_rea = true
WHERE user_id = 14;