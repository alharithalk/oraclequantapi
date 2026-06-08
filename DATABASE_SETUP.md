# Database Setup & Connection Guide

This project uses an **Oracle Database XE 21c** instance running in Docker. The
application user/schema (`oraclequant`) has already been created inside the
`XEPDB1` pluggable database.

## 1. Running database container

A container named `oracle-xe` is already running:

| Setting        | Value                                               |
|----------------|-----------------------------------------------------|
| Image          | `container-registry.oracle.com/database/express:latest` |
| Container name | `oracle-xe`                                         |
| Host port      | `1521` → container `1521` (DB listener)            |
| Host port      | `5500` → container `5500` (EM Express, optional)   |
| ORACLE_SID     | `XE`                                                |
| PDB            | `XEPDB1`                                            |
| SYS/SYSTEM pwd | `29999login`                                        |

If the container isn't running, start it with:

```bash
docker start oracle-xe
```

## 2. Application schema (already created)

The application connects as the `oraclequant` user inside the `XEPDB1` PDB —
matching `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=oraclequant
spring.datasource.password=oraclequant
```

This user was created with:

```sql
ALTER SESSION SET CONTAINER = XEPDB1;

CREATE USER oraclequant IDENTIFIED BY oraclequant
  DEFAULT TABLESPACE USERS
  TEMPORARY TABLESPACE TEMP
  QUOTA UNLIMITED ON USERS;

GRANT CONNECT, RESOURCE TO oraclequant;
GRANT CREATE SESSION, CREATE TABLE, CREATE SEQUENCE, CREATE VIEW, CREATE PROCEDURE TO oraclequant;
```

(Spring's `spring.jpa.hibernate.ddl-auto=update` will create/update the actual
tables on first application startup.)

## 3. Connection details (for any DB GUI tool)

| Field             | Value                       |
|-------------------|-----------------------------|
| Host              | `localhost`                 |
| Port              | `1521`                      |
| Connection type   | **Service Name** (not SID)  |
| Service name      | `XEPDB1`                    |
| Username          | `oraclequant`               |
| Password          | `oraclequant`               |
| JDBC URL          | `jdbc:oracle:thin:@localhost:1521/XEPDB1` |

> Note: connect using the **service name** `XEPDB1`, not the SID `XE` — `XE`
> is the container database (CDB), and the application schema lives in the
> pluggable database `XEPDB1`.

## 4. Connecting with DbVisualizer (Oracle DataDirect driver)

You already have a connection saved in DbVisualizer called **`Oracle-xe`**
(driver: *Oracle (DataDirect)*), but it currently points at the wrong
service name (`ORCL`) and has no username/password set — that's why it
fails. Fix it like this:

1. Open DbVisualizer → in the **Databases** tab on the left, select the
   **`Oracle-xe`** connection.
2. Open its **Properties** tab (or right-click → **Properties**) and check
   the driver is **Oracle (DataDirect)**.
3. On the connection's **Database** / connection settings, set:
   - **Server**: `localhost`
   - **Port**: `1521`
   - **Service Name**: `XEPDB1`   ← change this from `ORCL` to `XEPDB1`
   - **Database Userid**: `oraclequant`
   - **Database Password**: `oraclequant`
4. Click **Connect** (the plug icon, or right-click → **Connect**).
5. Once connected, expand `Oracle-xe` → **Schemas** → **ORACLEQUANT** to
   browse tables. Tables appear after the Spring Boot app has run at least
   once (Hibernate creates them via `ddl-auto=update`).

### If you'd rather create a fresh connection from scratch

1. **Connection → Create Connection...**
2. Pick **Oracle** as the database type, then choose driver
   **Oracle (DataDirect)** when prompted.
3. Name it (e.g. `oraclequant-xepdb1`), then on the connection settings tab
   fill in:
   - **Server**: `localhost`
   - **Port**: `1521`
   - **Service Name**: `XEPDB1`  *(NOT `XE` / `ORCL` — that's the CDB SID, the
     app schema lives in the pluggable database `XEPDB1`)*
   - **Database Userid**: `oraclequant`
   - **Database Password**: `oraclequant`
4. Click **Ping Server** to verify connectivity, then **Connect**.

> The Oracle DataDirect driver builds the JDBC URL from these fields itself
> — you don't need to type a URL manually. If DbVisualizer asks you to
> download/install the driver the first time, allow it.

## 5. Connecting via SQL*Plus (CLI, inside the container)

```bash
docker exec -it oracle-xe sqlplus oraclequant/oraclequant@localhost:1521/XEPDB1
```

## 6. Troubleshooting

- **ORA-12514 "TNS:listener does not currently know of service"**: the
  **Service Name** field has the wrong value (e.g. `ORCL` or `XE`). Set it
  to `XEPDB1`.
- **ORA-01017 "invalid username/password"**: double check **Database
  Userid**/**Database Password** are filled in (`oraclequant`/`oraclequant`)
  and that you're targeting `XEPDB1` — the `oraclequant` user only exists
  inside `XEPDB1`, not in the root `XE` container.
- **Connection refused**: make sure the container is running and healthy —
  `docker ps` should show `oracle-xe` as `Up ... (healthy)`.
