# Testing MongoDB Connection

## Quick Test Commands

### 1. Test MongoDB Connection from Command Line
```bash
# Using mongo shell (if installed)
mongosh mongodb://admin:secret@localhost:27019/isu_pushnoti_mongo?authSource=admin

# Or using Docker exec
docker exec -it isu-backend-pushnoti-mongodb mongosh -u admin -p secret --authenticationDatabase admin
```

### 2. Verify MongoDB is Running
```bash
# Check if MongoDB container is running
docker ps | findstr mongodb

# Check MongoDB logs
docker logs isu-backend-pushnoti-mongodb
```

### 3. Test Spring Boot Connection
After starting your application, check the logs for:
```
✅ SUCCESS: Connected to database successfully
✅ MongoDB connection established
```

Or look for errors:
```
❌ ERROR: Failed to authenticate
❌ ERROR: Connection refused
```

## Troubleshooting

### If still getting authentication errors:

1. **Restart MongoDB container**
   ```bash
   docker restart isu-backend-pushnoti-mongodb
   ```

2. **Check MongoDB is using correct credentials**
   ```bash
   docker exec -it isu-backend-pushnoti-mongodb mongosh
   > use admin
   > db.auth("admin", "secret")
   ```
   Should return: `{ ok: 1 }`

3. **Recreate MongoDB with fresh data**
   ```bash
   make clean
   make build
   make up
   ```

### If connection works but can't create collections:

The user might need additional permissions. Connect to MongoDB and run:
```javascript
use admin
db.createUser({
  user: "admin",
  pwd: "secret",
  roles: [
    { role: "root", db: "admin" }
  ]
})
```

## Environment Variables Check

Your current configuration:
- **MongoDB Host**: localhost
- **MongoDB Port**: 27019
- **Database Name**: isu_pushnoti_mongo
- **Username**: admin
- **Password**: secret
- **Auth Database**: admin
- **Auth Mechanism**: SCRAM-SHA-256

## Connection String
```
mongodb://admin:secret@localhost:27019/isu_pushnoti_mongo?authSource=admin&authMechanism=SCRAM-SHA-256
```

This should work for:
- ✅ Running application locally (outside Docker)
- ✅ MongoDB running in Docker on port 27019
- ✅ MongoDB 7.0 with SCRAM-SHA-256 authentication

