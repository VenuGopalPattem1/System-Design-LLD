# Logging Framework — LLD

A thread-safe, extensible logging framework in Java with pluggable formatters and appenders.

---

https://github.com/ashishps1/awesome-low-level-design/blob/main/problems/logging-framework.md

## Classes & Interfaces

| Class / Interface | Type | Role |
|---|---|---|
| `LogLevel` | Enum | DEBUG(1) → FATAL(5) with numeric priority |
| `LogMessage` | Class | Immutable snapshot: timestamp, level, thread, message |
| `Formatter` | Interface | `format(LogMessage) → String` |
| `PlainTextFormatter` | Class | `[timestamp] [LEVEL] message` |
| `JSONFormatter` | Class | `{"timestamp":..., "level":..., "message":...}` |
| `CSVFormatter` | Class | `timestamp,LEVEL,thread,"message"` |
| `XMLFormatter` | Class | `<log><level>...</level><message>...</message></log>` |
| `LogAppender` | Interface | `append(LogMessage) → void` |
| `ConsoleAppender` | Class | Writes to stdout / stderr |
| `FileAppender` | Class | Buffered write to a log file |
| `DatabaseAppender` | Class | Simulates DB INSERT |
| `LoggerConfig` | Class | Bundles LogLevel + LogAppender as one unit |
| `Logger` | Singleton | Entry point — level gate → LogMessage → Appender |

---

## End-to-End Flow

```
logger.info("User logged in")
        │
        ▼
① LEVEL GATE
  INFO.priority >= configured minimum?
  NO  → drop silently (zero cost)
  YES → continue
        │
        ▼
② CREATE LogMessage
  { level, message, timestamp = now(), thread = currentThread() }
        │
        ▼
③ CONFIG SNAPSHOT
  Atomically grab (LogLevel + LogAppender) reference under lock
  → caller thread always uses a consistent pair
        │
        ▼
④ APPENDER.append(logMessage)
        │
        ├──► ConsoleAppender
        ├──► FileAppender
        └──► DatabaseAppender
                │
                ▼
⑤ FORMATTER.format(logMessage)
        │
        ├──► PlainTextFormatter → "[2026-03-09 10:00:00] [INFO] User logged in"
        ├──► JSONFormatter      → {"level":"INFO","message":"User logged in"}
        ├──► CSVFormatter       → 2026-03-09 10:00:00,INFO,main,"User logged in"
        └──► XMLFormatter       → <log><level>INFO</level>...</log>
                │
                ▼
⑥ OUTPUT WRITTEN
  → stdout / file on disk / in-memory DB
```

---

## Design Patterns

| Pattern | Where | Why |
|---|---|---|
| **Singleton** | `Logger` | One shared instance across the app; double-checked locking for thread-safe init |
| **Strategy** | `Formatter` interface | Swap PlainText/JSON/CSV/XML with zero changes to Logger or Appender |
| **Strategy** | `LogAppender` interface | Swap Console/File/DB with zero changes to Logger |
| **Immutable Value Object** | `LogMessage` | Safe to pass across threads — no synchronization needed on the data |

---

## Thread Safety

### 1. Singleton — Double-Checked Locking
```java
private static volatile Logger instance;

if (instance == null) {
    synchronized (Logger.class) {
        if (instance == null) instance = new Logger();
    }
}
```
- `volatile` — ensures the reference is visible across all threads after write
- Inner null check — avoids locking on every call after first init

### 2. Config Swap — Atomic Snapshot
```java
public void log(LogLevel level, String message) {
    LoggerConfig cfg;
    synchronized (this) { cfg = this.config; }   // grab reference, then release lock

    if (level.getPriority() >= cfg.getLogLevel().getPriority())
        cfg.getAppender().append(new LogMessage(level, message));
}
```
- Lock held **only** while reading the reference — not during formatting or I/O
- In-flight log always uses a consistent (level + appender) pair even if another thread calls `setConfig()` simultaneously

### 3. FileAppender — Synchronized Write
```java
public synchronized void append(LogMessage message) { ... }
```
- Prevents two threads from interleaving bytes in the same file

---

## Output Formats

| Formatter | Sample Output | Best For |
|---|---|---|
| `PlainText` | `[2026-03-09 10:00:00] [INFO] User logged in` | Console, human reading |
| `JSON` | `{"level":"INFO","message":"User logged in"}` | ELK, Splunk, log aggregators |
| `CSV` | `2026-03-09,INFO,main,"User logged in"` | Excel, analytics |
| `XML` | `<log><level>INFO</level>...</log>` | Enterprise / SOAP systems |

**Escaping handled:**
- JSON → `"` becomes `\"`
- CSV  → inner `"` doubled per RFC 4180
- XML  → `&`, `<`, `>`, `"` → XML entities

---

## How to Run

```bash
javac LoggingExample.java
java logging.LoggingExample
```

**Change log level at runtime:**
```java
logger.setConfig(new LoggerConfig(LogLevel.DEBUG, new ConsoleAppender()));
```

**Use a different format:**
```java
logger.setConfig(new LoggerConfig(LogLevel.INFO,
        new ConsoleAppender(new JSONFormatter())));
```

**Log to file with JSON:**
```java
FileAppender fa = new FileAppender("app.json", new JSONFormatter());
logger.setConfig(new LoggerConfig(LogLevel.WARNING, fa));
fa.close(); // always close to flush
```

---

## How to Extend

**New destination (e.g. Kafka, Slack):**
```java
class KafkaAppender implements LogAppender {
    public void append(LogMessage msg) { /* produce to topic */ }
    public void setFormatter(Formatter f) { this.formatter = f; }
}
```

**New format (e.g. Logfmt):**
```java
class LogfmtFormatter implements Formatter {
    public String format(LogMessage msg) {
        return "level=" + msg.getLevel() + " msg=\"" + msg.getMessage() + "\"";
    }
}
```

**New log level (e.g. TRACE):**
```java
enum LogLevel { TRACE(0), DEBUG(1), INFO(2), ... }
// Add logger.trace(msg) convenience method
```

> Zero changes to existing classes — Open/Closed Principle.

---

## Interview Talking Points

- **Start with**: Requirements → `LogLevel` enum → `LogMessage` data object → interfaces → concrete classes
- **Singleton**: Explain `volatile` + double-checked locking and why both null checks are needed
- **Config snapshot**: Why we grab the reference before the level check (consistency guarantee)
- **Strategy x2**: `Formatter` and `LogAppender` are independent strategies — any combination works
- **Immutable `LogMessage`**: No locks needed on the data object itself
- **Follow-up extensions**: Async appender (BlockingQueue + background thread), RollingFileAppender, MDC via `ThreadLocal`, shutdown hook to flush buffers