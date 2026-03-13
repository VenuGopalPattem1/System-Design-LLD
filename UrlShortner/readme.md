# 🔗 URL Shortener — LLD Interview: Complete Discussion Guide

> A reference for everything you can bring up in a machine coding / LLD interview on URL Shortening. Use this as a checklist to stand out.

---

## Table of Contents

1. [Core Flow Recap](#1-core-flow-recap)
2. [Short Code Generation — Deep Dive](#2-short-code-generation--deep-dive)
3. [Concurrency & Thread Safety](#3-concurrency--thread-safety)
4. [Edge Cases to Proactively Mention](#4-edge-cases-to-proactively-mention)
5. [Design Patterns](#5-design-patterns)
6. [Expiry & TTL Handling](#6-expiry--ttl-handling)
7. [Analytics & Tracking](#7-analytics--tracking)
8. [Scalability Ladder](#8-scalability-ladder)
9. [Database Design](#9-database-design)
10. [Caching Strategy](#10-caching-strategy)
11. [API Design Discussion](#11-api-design-discussion)
12. [Security Concerns](#12-security-concerns)
13. [SOLID Principles Checklist](#13-solid-principles-checklist)
14. [Testing Strategy](#14-testing-strategy)
15. [Interview Flow & Time Tips](#15-interview-flow--time-tips)

---

## 1. Core Flow Recap

```
User → POST /shorten {longUrl}
     → Generate shortCode (Base62)
     → Store mapping (shortCode → longUrl)
     → Return shortUrl

User → GET /{shortCode}
     → Lookup shortCode
     → 301/302 Redirect → longUrl
```

**Key talking point:** Difference between **301 (permanent)** and **302 (temporary)** redirect.
- `301` → browser caches it → less server load, but no analytics on repeat visits
- `302` → hits your server every time → accurate analytics but more load
- **Ask interviewer** which matters more for the use case.

---

## 2. Short Code Generation — Deep Dive

### Option A: Base62 + Auto-Increment Counter
```
Characters: a-z + A-Z + 0-9 = 62 chars
7 chars = 62^7 ≈ 3.5 trillion combinations
```
✅ Simple, predictable, fast  
⚠️ Sequential codes are guessable (security concern)

### Option B: MD5 / SHA256 Hash (first 7 chars)
```java
String hash = DigestUtils.md5Hex(longUrl).substring(0, 7);
```
✅ Same URL → same hash (natural dedup)  
⚠️ Hash collisions possible — need collision retry logic

### Option C: UUID (random)
```java
String code = UUID.randomUUID().toString().replace("-", "").substring(0, 7);
```
✅ Very low collision chance  
⚠️ No dedup — same URL can get multiple short codes

### Option D: Snowflake ID (Production)
- Distributed ID generator (Twitter's approach)
- 64-bit: timestamp + machine ID + sequence
- Guarantees uniqueness across distributed nodes

**Mention to interviewer:** In production, you'd use Snowflake or a dedicated ID service. In this interview, Base62 + AtomicLong is the right tradeoff.

---

## 3. Concurrency & Thread Safety

This is a common follow-up. Be ready to explain each choice:

| Component | Why Thread-Safe |
|---|---|
| `ConcurrentHashMap` | Multiple threads reading/writing mappings safely |
| `AtomicLong` for counter | Atomic increment — no race condition on ID generation |
| `AtomicInteger` for clickCount | Concurrent click counting without `synchronized` |

### Race Condition Scenario to Mention
```
Thread A: checks if "abc1234" exists → not found
Thread B: checks if "abc1234" exists → not found
Thread A: inserts "abc1234"
Thread B: inserts "abc1234" → COLLISION!
```

**Fix:** Use `ConcurrentHashMap.putIfAbsent()` to atomically check-and-insert.

```java
UrlMapping existing = shortToLong.putIfAbsent(shortCode, newMapping);
if (existing != null) {
    // collision — regenerate and retry
}
```

---

## 4. Edge Cases to Proactively Mention

Mentioning these before the interviewer asks shows senior-level thinking:

| Edge Case | Handling |
|---|---|
| **Duplicate long URL** | Check `longToShort` map first — return existing short code |
| **Invalid URL format** | Validate with `java.net.URI` or regex before storing |
| **Custom alias already taken** | Throw `AliasAlreadyExistsException` |
| **Short code not found** | Throw `UrlNotFoundException` → return HTTP 404 |
| **Expired URL** | Check `expiresAt` on lookup → throw `UrlExpiredException` → return HTTP 410 Gone |
| **Very long URLs** | Set a max length limit (e.g., 2048 chars) |
| **Malicious URLs** | URL blacklist check before storing |
| **Empty / null input** | Input validation at service boundary |

---

## 5. Design Patterns

Mention these naturally — don't just list them, explain *why* you used them:

### Strategy Pattern — Code Generation
```java
public interface ShortCodeStrategy {
    String generate(String longUrl);
}

public class Base62Strategy implements ShortCodeStrategy { ... }
public class MD5Strategy implements ShortCodeStrategy { ... }
public class RandomStrategy implements ShortCodeStrategy { ... }

// Swap strategy without changing service
public class UrlShortenerService {
    private final ShortCodeStrategy strategy;
    // constructor injection
}
```
**Why:** Makes code generation swappable — easy to change algorithm in production.

### Factory Pattern — UrlMapping Creation
```java
public class UrlMappingFactory {
    public static UrlMapping create(String shortCode, String longUrl, Duration ttl) {
        UrlMapping m = new UrlMapping(shortCode, longUrl);
        if (ttl != null) m.setExpiresAt(LocalDateTime.now().plus(ttl));
        return m;
    }
}
```

### Builder Pattern — UrlMapping
```java
UrlMapping mapping = new UrlMapping.Builder()
    .shortCode("abc1234")
    .longUrl("https://google.com")
    .expiresIn(Duration.ofDays(30))
    .build();
```

### Decorator Pattern — Analytics Tracking
```java
// Wrap the base service to add analytics without modifying it
public class AnalyticsUrlShortener implements UrlShortenerService {
    private final UrlShortenerService delegate;

    public String expandUrl(String code) {
        String url = delegate.expandUrl(code);
        analyticsService.track(code); // added behavior
        return url;
    }
}
```

---

## 6. Expiry & TTL Handling

```java
public class UrlMapping {
    private LocalDateTime expiresAt; // null = never expires

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
```

### Cleanup Strategy (mention proactively)

**Lazy deletion** — check expiry only on access (what we implement here):
- Pros: Simple, no background jobs
- Cons: Expired entries stay in memory

**Eager deletion** — background scheduled job:
```java
@Scheduled(fixedRate = 3600000) // every hour
public void cleanupExpired() {
    shortToLong.entrySet().removeIf(e -> e.getValue().isExpired());
}
```

---

## 7. Analytics & Tracking

### Basic: Click Count
```java
private final AtomicInteger clickCount = new AtomicInteger(0);
public void incrementClick() { clickCount.incrementAndGet(); }
```

### Advanced (mention for bonus points):
```java
public class UrlStats {
    private final String shortCode;
    private final int totalClicks;
    private final LocalDateTime lastAccessed;
    private final Map<String, Integer> clicksByCountry; // geo analytics
    private final Map<String, Integer> clicksByDevice;  // device analytics
}
```

**Scalability concern to mention:** In production, you wouldn't write analytics synchronously on every redirect — that adds latency. Instead, publish to a **message queue (Kafka/SQS)** and process asynchronously.

---

## 8. Scalability Ladder

Walk through this progression if asked "how would you scale this?":

```
Level 1 — Single Server (what we built)
  └── In-memory HashMap
  └── AtomicLong counter
  └── Works for ~thousands of URLs

Level 2 — Add Persistence
  └── MySQL: urls table (shortCode PK, longUrl, expiresAt, clickCount)
  └── Survive restarts, handle millions of rows

Level 3 — Add Caching
  └── Redis in front of DB
  └── Cache shortCode → longUrl (read-heavy workload)
  └── Cache TTL = URL expiry time

Level 4 — Distributed ID Generation
  └── Replace AtomicLong with Snowflake ID or ZooKeeper counter
  └── Multiple app servers can now generate unique IDs independently

Level 5 — Horizontal Scaling
  └── Multiple app server instances behind a Load Balancer
  └── Redis cluster for distributed caching
  └── DB read replicas for read scaling

Level 6 — Global Scale
  └── CDN for redirect (cache 301s at edge)
  └── Geo-distributed DB (CockroachDB, PlanetScale)
  └── Kafka for async analytics pipeline
```

---

## 9. Database Design

If asked about persistence, show this schema:

```sql
CREATE TABLE url_mappings (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    short_code   VARCHAR(10)  UNIQUE NOT NULL,
    long_url     TEXT         NOT NULL,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    expires_at   DATETIME     NULL,
    click_count  INT          DEFAULT 0,
    user_id      BIGINT       NULL  -- if auth is added
);

CREATE INDEX idx_short_code ON url_mappings(short_code); -- fast lookup
CREATE INDEX idx_expires_at ON url_mappings(expires_at); -- fast cleanup
```

**Repository interface to mention:**
```java
public interface UrlRepository {
    void save(UrlMapping mapping);
    Optional<UrlMapping> findByShortCode(String shortCode);
    Optional<String> findShortCodeByLongUrl(String longUrl); // dedup
    void deleteExpired();
}
```

---

## 10. Caching Strategy

```
GET /{shortCode}
    ↓
Check Redis Cache
    ↓ Miss                    ↓ Hit
Query MySQL           Return from Cache (fast)
    ↓
Store in Redis (with TTL)
    ↓
Return to user
```

**Cache eviction to mention:**
- **LRU** — evict least recently used URLs (good for popular URL patterns)
- **TTL-based** — cache expires when URL expires (simpler)
- Cache hit rate goal: **~95%+** for a typical URL shortener (reads are very repetitive)

---

## 11. API Design Discussion

```
POST   /api/v1/shorten          → Create short URL
GET    /{shortCode}             → Redirect (public, no auth)
GET    /api/v1/urls/{shortCode} → Get URL info (no redirect)
GET    /api/v1/urls/{shortCode}/stats → Analytics
DELETE /api/v1/urls/{shortCode} → Delete URL
PUT    /api/v1/urls/{shortCode} → Update expiry or long URL
```

**HTTP Status Codes to mention:**
| Scenario | Status Code |
|---|---|
| Redirect success | 301 / 302 |
| Short code not found | 404 Not Found |
| URL expired | 410 Gone |
| Custom alias taken | 409 Conflict |
| Invalid URL input | 400 Bad Request |
| Server error | 500 Internal Server Error |

---

## 12. Security Concerns

Proactively mentioning security is impressive in interviews:

| Threat | Mitigation |
|---|---|
| **Malicious URL (phishing)** | Blacklist check via Google Safe Browsing API |
| **URL enumeration** | Use random codes (not sequential) in production |
| **Rate limiting** | Limit shortening requests per IP (token bucket) |
| **SSRF attack** | Validate URL doesn't point to internal IPs (10.x.x.x, 192.168.x.x) |
| **SQL Injection** | Use parameterized queries / JPA (not raw SQL) |
| **Spam** | CAPTCHA or auth requirement for creating URLs |

---

## 13. SOLID Principles Checklist

Show you applied SOLID intentionally:

| Principle | How Applied |
|---|---|
| **S** — Single Responsibility | `Base62Encoder` only encodes; `UrlShortenerService` only manages mappings |
| **O** — Open/Closed | Add new code strategies via `ShortCodeStrategy` interface — no existing code changes |
| **L** — Liskov Substitution | Any `ShortCodeStrategy` impl can replace another without breaking service |
| **I** — Interface Segregation | `UrlShortenerService` interface is focused — no bloated methods |
| **D** — Dependency Inversion | Service depends on `ShortCodeStrategy` interface, not concrete class |

---

## 14. Testing Strategy

Mention this at the end to show engineering maturity:

```java
// Unit Tests
@Test void shouldGenerateUniqueShortCode()
@Test void shouldReturnSameCodeForDuplicateLongUrl()
@Test void shouldThrowWhenCustomAliasAlreadyExists()
@Test void shouldThrowWhenUrlExpired()
@Test void shouldThrowWhenShortCodeNotFound()
@Test void shouldIncrementClickCountOnAccess()

// Concurrency Tests
@Test void shouldHandleConcurrentShorteningRequests()  // 1000 threads simultaneously
@Test void shouldNotDuplicateShortCodesUnderConcurrency()

// Edge Case Tests
@Test void shouldRejectInvalidUrls()
@Test void shouldRejectNullOrEmptyInput()
@Test void shouldHandleVeryLongUrls()
```

**Tools to mention:** JUnit 5, Mockito (mock the repository), `ExecutorService` for concurrency tests.

---

## 15. Interview Flow & Time Tips

### Suggested 30-Minute Flow

```
0:00 – 0:03  │ Clarify requirements, ask about scale, auth, analytics
0:03 – 0:06  │ Define entities: UrlMapping, UrlStats
0:06 – 0:09  │ Define interfaces: UrlShortenerService, ShortCodeStrategy
0:09 – 0:20  │ Implement: Base62Encoder → UrlMapping → Service
0:20 – 0:24  │ Add edge cases: expiry check, dedup, custom alias conflict
0:24 – 0:27  │ Mention design patterns used (Strategy, Factory)
0:27 – 0:30  │ Scalability discussion: Redis, DB schema, Snowflake IDs
```

### Questions to Ask the Interviewer (shows seniority)
1. "Should the same long URL always return the same short code?"
2. "Do we need user authentication — should URLs be owned by users?"
3. "Is click analytics a hard requirement or nice-to-have?"
4. "Should expired URLs return 404 or 410?"
5. "What's our expected scale — URLs per day?"

### Phrases That Impress
- *"I'll use `ConcurrentHashMap` and `AtomicLong` for thread safety since this is an in-memory prototype — in production I'd use Redis with atomic operations."*
- *"I'm using the Strategy pattern here so we can swap the encoding algorithm without touching the service."*
- *"I chose 302 over 301 redirect so we get accurate analytics, but I'd confirm that tradeoff with the team."*
- *"For cleanup, I'll use lazy deletion now for simplicity, but mention a scheduled job would be better at scale."*

---

## Quick Reference Card

```
Short Code:  Base62 (a-z A-Z 0-9), 7 chars = 3.5 trillion combos
Redirect:    302 (analytics) vs 301 (performance)
Dedup:       longToShort reverse map — same URL → same code
Thread Safe: ConcurrentHashMap + AtomicLong + AtomicInteger
Expiry:      Lazy check on lookup + optional background cleanup
Scale:       HashMap → Redis → MySQL → Snowflake IDs → Kafka
Patterns:    Strategy, Factory, Builder, Decorator, Facade
```

---

*Good luck — you've got this! 🚀*