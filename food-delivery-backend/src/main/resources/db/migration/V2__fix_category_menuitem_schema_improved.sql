-- ==============================================================================
-- Migration: Fix Category and MenuItem Schema (Improved)
-- Description: Add missing columns and foreign key constraints with safety checks
-- Date: 2026-10-08
-- ==============================================================================

-- ==============================================================================
-- Step 1: Add 'active' column to categories table (if not exists)
-- ==============================================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'categories' AND column_name = 'active'
    ) THEN
        ALTER TABLE categories ADD COLUMN active BOOLEAN;
        UPDATE categories SET active = true WHERE active IS NULL;
        ALTER TABLE categories ALTER COLUMN active SET NOT NULL;
    END IF;
END $$;

-- ==============================================================================
-- Step 2: Add 'category_id' column to menu_items table (if not exists)
-- ==============================================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'menu_items' AND column_name = 'category_id'
    ) THEN
        -- Add the column as nullable first
        ALTER TABLE menu_items ADD COLUMN category_id INTEGER;

        -- Ensure we have at least one category
        INSERT INTO categories (name, description, active)
        VALUES ('Default Category', 'Default category for existing menu items', true)
        ON CONFLICT (name) DO NOTHING;

        -- Assign existing menu_items to the first available category
        UPDATE menu_items
        SET category_id = (SELECT id FROM categories ORDER BY id LIMIT 1)
        WHERE category_id IS NULL;

        -- Make it NOT NULL
        ALTER TABLE menu_items ALTER COLUMN category_id SET NOT NULL;
    END IF;
END $$;

-- ==============================================================================
-- Step 3: Add foreign key constraint (if not exists)
-- ==============================================================================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints
        WHERE constraint_name = 'fk_menu_items_category'
        AND table_name = 'menu_items'
    ) THEN
        ALTER TABLE menu_items
        ADD CONSTRAINT fk_menu_items_category
        FOREIGN KEY (category_id) REFERENCES categories(id);
    END IF;
END $$;
