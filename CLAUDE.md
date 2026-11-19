# TideTempo

Java SE (JDK 8+) Swing desktop app: time tracking, clients, projects, tasks, contracts. Ant/NetBeans project, MySQL backend, FlatLaf UI, JasperReports.

## Layout (src/com/wigerlabs/tidetempo)
- `gui/`, `panel/`, `dialog/`, `components/` – Swing UI (`.form` files are NetBeans-generated; don't hand-edit the generated blocks in matching `.java`)
- `entity/` – model classes; `connection/` – MySQL access; `util/`, `validation/`, `log/` – helpers
- `report/` – JasperReports `.jrxml`; `img/` – icons
- `test/` – tests; `tidetempo_db.sql` – schema

## Build
- `ant clean jar` (NetBeans `build.xml`); jars in `lib/` (do not modify)

## Conventions
- Keep changes minimal; follow existing package/naming style.
- Never read or edit `build/`, `dist/`, `lib/`, `logs/`, `nbproject/private/`, `db/` binaries.
- Use Grep/Glob before reading whole files; read only the needed line ranges.
- Don't touch `session.properties` or `app.properties` secrets/credentials.
