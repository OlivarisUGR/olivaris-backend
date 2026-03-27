-- Ensure the new system role exists
INSERT INTO role(name)
SELECT 'ROLE_BASIC'
WHERE NOT EXISTS (
		SELECT 1 FROM role WHERE name = 'ROLE_BASIC'
);

-- Assign ROLE_BASIC to users that currently have legacy roles
-- (farmer/entity_admin, with and without ROLE_ prefix)
INSERT INTO user_role(user_id, role_id)
SELECT DISTINCT ur.user_id, rb.id
FROM user_role ur
JOIN role r ON r.id = ur.role_id
JOIN role rb ON rb.name = 'ROLE_BASIC'
WHERE LOWER(r.name) IN ('role_farmer', 'role_entity_admin', 'farmer', 'entity_admin')
	AND NOT EXISTS (
			SELECT 1
			FROM user_role ur2
			WHERE ur2.user_id = ur.user_id
				AND ur2.role_id = rb.id
	);

-- Remove old role assignments after migration
DELETE FROM user_role
WHERE role_id IN (
		SELECT id
		FROM role
		WHERE LOWER(name) IN ('role_farmer', 'role_entity_admin', 'farmer', 'entity_admin')
);

-- Delete legacy roles if they are no longer used
DELETE FROM role r
WHERE LOWER(r.name) IN ('role_farmer', 'role_entity_admin', 'farmer', 'entity_admin')
	AND NOT EXISTS (
			SELECT 1 FROM user_role ur WHERE ur.role_id = r.id
	);
