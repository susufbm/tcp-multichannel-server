# Multi-Channel TCP Client–Server System

A client–server system built on raw TCP sockets with a custom application protocol,
a thread-per-client server, and three kinds of channels: commands, per-operation data
transfer, and connection liveness.

No framework, no RPC library. The protocol, the parser, the response format, the
data-channel negotiation and the concurrency model are implemented from scratch —
which is the point of the project.

## Architecture

| Channel | Port | Purpose |
|---|---|---|
| Command | 5000 | Line-based request/response for all operations |
| Keepalive | 4000 | Liveness detection, independent of command traffic |
| Data | ephemeral, negotiated per operation | Bulk object transfer |

The data channel works like FTP passive mode: when an operation needs to move an
entity, the server opens an ephemeral port, replies `PREOK <n> 200 <host> <port>`,
and the client connects there and sends the object via Java serialisation. Large
transfers therefore never block the command channel, and a stuck client is detected
by the keepalive channel rather than mistaken for a slow operation.

**Server:** a listening socket accepts connections and hands each one to its own
thread (`GestorServer extends Thread`). Lines are tokenised by `CommandParser`,
dispatched to the matching service, and answered through `ResponseBuilder` with
typed status codes (`OK/PREOK/FAILED <number> <code> <detail>`), so the client
branches on codes instead of parsing prose.

**Persistence:** a generic `BaseRepository<T>` with one concrete repository per
entity. Repositories are static and shared across all client threads.

**Authentication:** `USER`/`PASS` state machine; unauthenticated sessions are
rejected with a 403-style code on any management command.

## Concurrency: a partial-synchronisation bug and the fix

The shared repositories were protected with `synchronized` in the concrete
subclasses — but only partially: `add`, `delete` and `list` were synchronised,
while `update` was left as an auto-generated stub without it:

```java
@Override
public synchronized void add(Asignatura model) throws IOException { super.add(model); }

@Override
public void update(String id, Asignatura model) throws IOException {
    // TODO Auto-generated method stub
    super.update(id, model);   // <-- unsynchronised
}
```

That is the worst method to leave unprotected, because its critical section is
compound: when the entity's id changes, the base implementation performs a
check–remove–put sequence on the shared `HashMap`. Two concurrent updates can
interleave between those steps and silently lose a write — and concurrent
structural modification of a `HashMap` can corrupt the map itself.

The fix serialises all access at the root, so no subclass can forget it:

```java
public synchronized void add(T model)    { ... }
public synchronized void delete(String id) { ... }
public synchronized void update(String id, T model) { ... }
public synchronized List<T> list() { return new ArrayList<>(storage.values()); }
```

A coarse lock was chosen deliberately: it is obviously correct, the workload is
low-volume, and the contention window is tiny. If read throughput ever became the
bottleneck, the next step is a `ReentrantReadWriteLock` — at the cost of a design
that is harder to reason about. `list()` returns a copy so callers never iterate
a structure another thread may be mutating.

## Build and run

```bash
./build.sh
java -cp out/server edu.ucam.server.ServerApp     # terminal 1
java -cp out/client edu.ucam.cliente.AppUniversidad   # terminal 2 (user/pass: admin/admin)
```

Verified end to end: authentication, data-channel negotiation (`PREOK`), serialised
entity transfer, and repository counts over concurrent connections.

## Stack

Java · TCP sockets · Multithreading · Java serialisation · Custom application protocol

## Authors

Jesús Fernández Bautista, with a classmate
Computer Engineering, UCAM, 2025-26.
