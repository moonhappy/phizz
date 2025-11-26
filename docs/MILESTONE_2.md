# Milestone 2: Server Application Development

---

## 1. Epic: API & Server Foundation

- [ ] To Do
- [x] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `epic`, `server`, `api`

**Milestone:** `Milestone 2`

**Requirement:** FR2.1, FR2.2

**Scope:** Set up the Ktor server, configure it, and implement the basic library scanning functionality.

**Summary:** Establish the web server foundation using Ktor and implement the ability to scan the file system for ISO files to populate the library.

**Acceptance Criteria:**
- Ktor server runs and responds to health checks.
- API endpoint returns a list of ISO files from a configured directory.

### 1.1. Task: Ktor Setup & Configuration

- [ ] To Do
- [ ] In Progress
- [x] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `setup`

**Milestone:** `Milestone 2`

**Scope:** Initialize Ktor project, configure build dependencies, and set up basic server structure.

**Implementation Details:**
- Add Ktor dependencies to `build.gradle.kts`.
- Configure Netty engine.
- Set up content negotiation (JSON serialization).

### 1.2. Task: Library Scanning Implementation

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `backend`

**Milestone:** `Milestone 2`

**Scope:** Implement logic to scan a directory for `.iso` files.

**Implementation Details:**
- Create a `LibraryService`.
- Implement recursive file scanning.
- Extract basic metadata (filename, size).

### 1.3. Task: REST API Endpoints

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `api`

**Milestone:** `Milestone 2`

**Scope:** Expose library data via HTTP endpoints.

**Implementation Details:**
- Implement `GET /library` to return the list of ISOs.
- Implement `GET /health` for status checks.

---

## 2. Epic: Session Management & Control

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `epic`, `server`, `logic`

**Milestone:** `Milestone 2`

**Requirement:** FR2.3, FR2.4

**Scope:** Manage concurrent user sessions and handle playback control commands.

**Summary:** Implement the logic to manage active playback sessions, allowing users to start, stop, and control playback.

**Acceptance Criteria:**
- Multiple sessions can exist (though hardware may limit active streams).
- WebSocket commands successfully trigger `libbluray` actions in the correct session.

### 2.1. Task: Session Manager Implementation

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `logic`

**Milestone:** `Milestone 2`

**Scope:** Create a manager to track active `Player` instances.

**Implementation Details:**
- Create `SessionManager` class.
- Map Session IDs to `Player` instances.
- Handle session creation and cleanup.

### 2.2. Task: Playback Control Endpoints

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `api`

**Milestone:** `Milestone 2`

**Scope:** HTTP endpoints to start and stop sessions.

**Implementation Details:**
- `POST /play/{iso_filename}`: Starts a session, returns Session ID.
- `POST /stop/{session_id}`: Ends a session.

### 2.3. Task: WebSocket Command Interface

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `websocket`

**Milestone:** `Milestone 2`

**Scope:** Real-time control channel.

**Implementation Details:**
- Implement `/control/{session_id}` WebSocket endpoint.
- Handle JSON messages (e.g., `{"command": "arrow_up"}`).
- Route commands to the appropriate `Player` instance.

---

## 3. Epic: Media Streaming (WebRTC)

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `epic`, `server`, `streaming`

**Milestone:** `Milestone 2`

**Requirement:** FR2.5

**Scope:** Implement the pipeline to extract frames, encode them, and stream via WebRTC.

**Summary:** This is the most complex epic. It involves hooking into the raw frame data from `libbluray`, encoding it to H.264, and sending it over a WebRTC peer connection.

**Acceptance Criteria:**
- Video stream is visible in a web browser (or test client).
- Low latency (acceptable for menu navigation).

### 3.1. Task: WebRTC Integration & Signaling

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `webrtc`

**Milestone:** `Milestone 2`

**Scope:** Set up WebRTC libraries and signaling mechanism.

**Implementation Details:**
- Integrate a Kotlin/Java WebRTC library (e.g., `awslabs/aws-crt-java` or a native wrapper).
- Implement signaling logic over the WebSocket (SDP offer/answer exchange).

### 3.2. Task: Frame Extraction & Encoding Pipeline

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `encoding`

**Milestone:** `Milestone 2`

**Scope:** Convert raw frames to H.264.

**Implementation Details:**
- Implement a loop to read frames from `Player`.
- Feed frames into an encoder (x264 via JNA, or FFmpeg pipe).
- Ensure hardware acceleration if possible (NVENC/VAAPI).

### 3.3. Task: Stream Transmission

- [x] To Do
- [ ] In Progress
- [ ] Ready to Test
- [ ] Done

**Labels:** `task`, `server`, `webrtc`

**Milestone:** `Milestone 2`

**Scope:** Send encoded packets via WebRTC.

**Implementation Details:**
- Packetize H.264 NAL units.
- Send via WebRTC `VideoTrack`.
