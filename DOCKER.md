# Running Everything in Docker (Database + App)

`docker-compose.yml` spins up **both** the Oracle XE database and the
Spring Boot app as containers, networked together — no local Java/Maven/JDK
setup required.

## 1. Files involved

| File                              | Purpose                                                        |
|-----------------------------------|----------------------------------------------------------------|
| `Dockerfile`                       | Multi-stage build: compiles the jar with Maven+JDK17, runs it on a slim JRE 17 |
| `docker-compose.yml`               | Defines the `oracle-xe` and `app` services + shared network    |
| `db/setup/01_create_oraclequant_user.sql` | Auto-creates the `oraclequant` schema in `XEPDB1` on first DB init |
| `.dockerignore`                    | Keeps `target/`, `.git/`, `.idea/`, `logs/` out of the build context |

## 2. IMPORTANT: stop any existing standalone Oracle XE container first

You already have a separate Oracle XE container (`oracle-xe`, started from
`Documents/oracle-xe-db-hosting/docker-compose.yaml`) bound to host port
`1521`. It will conflict with the one in this stack. Stop it first:

```bash
docker compose -f "C:\Users\kinda\OneDrive - yay app\Documents\oracle-xe-db-hosting\docker-compose.yaml" down
```
*(This only stops that container — its data volume is untouched.)*

## 3. Start the full stack

From the project root:

```bash
docker compose up --build
```

What happens:
1. **`oracle-xe`** starts from a fresh `oracle-data` volume. On its very
   first initialization (this can take several minutes — Oracle XE creates
   the database from scratch), it automatically executes
   `db/setup/01_create_oraclequant_user.sql`, creating the `oraclequant`
   user/schema inside `XEPDB1` — exactly what the app needs.
2. Once `oracle-xe` reports **healthy**, the **`app`** service builds (Maven
   compiles the jar inside a build stage) and starts, connecting to
   `jdbc:oracle:thin:@oracle-xe:1521/XEPDB1` as `oraclequant`/`oraclequant`
   (passed via `DB_URL`/`DB_USERNAME`/`DB_PASSWORD` env vars — see
   `application.properties`, which reads these with `oraclequant` as default).
3. Hibernate (`ddl-auto=update`) creates `HISTORY_RECORD` automatically on
   first startup.

To run in the background:
```bash
docker compose up --build -d
```

## 4. Verify it's running

```bash
docker compose ps
# both `oraclequant-db` and `oraclequantapi` should show as Up/healthy

curl -s -G "http://localhost:8080/convert-measurements" --data-urlencode "input=aa"
# -> [1]

curl -s http://localhost:8080/history
```

See `TESTING.md` for a full set of endpoint test cases.

## 5. Logs

```bash
docker compose logs -f app          # Spring Boot / app logs
docker compose logs -f oracle-xe    # Database logs
```

The app also writes to `./logs/oraclequantapi.log` on the host (mounted via
`volumes: - ./logs:/app/logs`).

## 6. Connecting a DB GUI tool (DbVisualizer, etc.)

Same connection details as before — Docker still publishes port `1521` on
`localhost`:
- Host: `localhost`, Port: `1521`, Service Name: `XEPDB1`
- User: `oraclequant` / Password: `oraclequant`

See `DATABASE_SETUP.md` for full GUI connection steps.

## 7. Stopping / cleaning up

```bash
docker compose down          # stop + remove containers (keeps the data volume)
docker compose down -v       # also delete the database volume (full reset)
```

## 8. Troubleshooting

- **"port is already allocated" for 1521/8080**: another container or local
  process is using the port. Check `docker ps` and stop the conflicting
  container (see step 2), or stop a locally-running instance of the app.
- **`app` keeps restarting / can't connect to DB**: the database can take
  several minutes to initialize on first run. Watch
  `docker compose logs -f oracle-xe` until it reports healthy before the
  app's `depends_on: condition: service_healthy` lets it start.
- **`oraclequant` user missing after first run**: the setup script in
  `db/setup/` only runs against an *empty* `oracle-data` volume. If you've
  run the stack before with old data, either `docker compose down -v` for a
  clean slate, or create the user manually (see `DATABASE_SETUP.md` §2).
