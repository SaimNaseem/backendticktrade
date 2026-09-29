UPDATE product
SET is_published = false
WHERE is_published IS NULL;

ALTER TABLE product
    ALTER COLUMN is_published SET NOT NULL;