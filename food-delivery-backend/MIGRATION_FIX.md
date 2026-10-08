# Database Schema Migration Fix

## Problem
The application was failing to start in production due to Hibernate schema migration errors:
- `categories` table missing `active` column (NOT NULL constraint)
- `menu_items` table missing `category_id` column (NOT NULL constraint)
- Missing foreign key constraint between `menu_items.category_id` and `categories.id`

## Solution Implemented

### 1. Added Flyway Database Migration
Created a proper database migration script:
- **File**: `src/main/resources/db/migration/V2__fix_category_menuitem_schema_improved.sql`
- **Features**:
  - Idempotent - can be run multiple times safely
  - Uses DO blocks with conditional checks
  - Handles existing data gracefully
  - Creates default category if none exists
  - Assigns existing menu_items to valid categories

### 2. Updated pom.xml
Added Flyway dependencies:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

### 3. Updated Configuration Files

#### Production (application-prod.properties)
- Changed `spring.jpa.hibernate.ddl-auto` from `update` to `validate`
- Disabled SQL logging in production (`show-sql=false`)
- Enabled Flyway with proper configuration
- Added `spring.jpa.defer-datasource-initialization=true`

#### Development (application-dev.properties)
- Kept `ddl-auto=update` for development convenience
- Enabled Flyway for schema consistency

#### Local (application-local.properties)
- Changed to `ddl-auto=none` to rely on Flyway only
- Enabled Flyway for local development

## Migration Details

The migration script performs the following steps:

1. **Add `active` column to categories** (if not exists)
   - Adds column as nullable
   - Sets default value `true` for existing rows
   - Converts to NOT NULL

2. **Add `category_id` column to menu_items** (if not exists)
   - Adds column as nullable
   - Creates "Default Category" if no categories exist
   - Assigns existing menu_items to first available category
   - Converts to NOT NULL

3. **Add foreign key constraint** (if not exists)
   - Creates `fk_menu_items_category` constraint
   - Links `menu_items.category_id` to `categories.id`

## Testing Results

✅ **Application starts successfully**
- No schema errors
- Flyway migration applied successfully
- All tables now have correct schema
- Foreign key constraints in place

## Production Deployment Instructions

Before deploying to production:

1. **Test in staging environment first**
   - Run the application with `spring.profiles.active=dev` or staging profile
   - Verify Flyway migration completes successfully
   - Test all menu item and category operations

2. **Backup production database**
   ```bash
   pg_dump -h <host> -U <user> -d <database> > backup_$(date +%Y%m%d).sql
   ```

3. **Deploy with production profile**
   - Application will run Flyway migrations automatically on startup
   - Hibernate will validate schema against entities
   - Any mismatch will cause startup to fail (as expected)

4. **Monitor for issues**
   - Check logs for Flyway migration status
   - Verify all endpoints work correctly
   - Monitor for any constraint violations

## Rollback Plan

If issues occur after deployment:

1. Stop the application
2. Restore database from backup
3. Revert code changes if necessary
4. Investigate the issue

## Future Schema Changes

For future schema changes:

1. Create new migration files: `V3__description.sql`, `V4__description.sql`, etc.
2. Follow the same idempotent pattern
3. Test locally first
4. Test in staging before production
5. Never modify existing migration files (create new ones instead)

## Notes

- The migration is idempotent and safe to re-run
- Existing data is preserved and handled gracefully
- Flyway tracks migration history in `flyway_schema_history` table
- Production uses `validate` mode to prevent accidental schema changes
- Development environments can use `update` mode for convenience
