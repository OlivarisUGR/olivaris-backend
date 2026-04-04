UPDATE enclosure
SET name = UPPER(name)
WHERE name IS NOT NULL
    AND name <> UPPER(name);