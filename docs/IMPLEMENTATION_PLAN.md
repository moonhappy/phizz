# **Implementation Plan**

## **Project: LAN-Based ISO Master Streaming System**

This document outlines the proposed technical approach, technology stack, and development milestones for the LAN-Based ISO Master Streaming System, as specified in the Software Requirements Specification (SRS).

## **1. Addressing Open Questions**

Before defining the implementation, we must address the critical open questions from the SRS:

1.  **Encryption (Critical):**
    *   **Assumption:** For this implementation plan, we will assume the .iso files are **unencrypted**.
    *   **Contingency:** If the files are encrypted with AACS, the scope of the project increases significantly. It would require integrating a decryption library and managing a key database (e.g., `KEYDB.cfg`). This would add an estimated 2-3 weeks to the timeline for research, implementation, and testing. **This question must be answered before development begins.**

2.  **Core Engine:**
    *   **Recommendation:** We will use **libbluray** and **libdvdnav**. These open-source libraries are the de-facto standard for Blu-ray and DVD navigation and are capable of handling complex menus, including BD-J. They are mature, well-documented, and provide the necessary foundation for the "virtual player" engine.

3.  **Streaming Protocol:**
    *   **Recommendation:** We will use **WebRTC** (Web Real-Time Communication).
    *   **Justification:** WebRTC is designed for low-latency, peer-to-peer communication, which directly addresses the requirement for responsive menu navigation (NFR1.2). It streams H.264 video directly between the server and the browser without intermediate transcoding, preserving quality.

4.  **Hardware Recommendation (for 3 concurrent streams):**
    *   **Minimum:**
        *   **CPU:** Modern 6-core/12-thread CPU (e.g., Intel Core i5-12600K, AMD Ryzen 5 7600X).
        *   **GPU:** NVIDIA GeForce RTX 3050 or equivalent (for hardware-accelerated decoding/encoding).
        *   **RAM:** 16 GB DDR4.
        *   **OS:** Ubuntu Server 22.04 LTS.
    *   **Recommended:**
        *   **CPU:** Modern 8-core/16-thread CPU (e.g., Intel Core i7-13700K, AMD Ryzen 7 7700X).
        *   **GPU:** NVIDIA GeForce RTX 4060 or equivalent.
        *   **RAM:** 32 GB DDR5.
        *   **OS:** Ubuntu Server 22.04 LTS.

5.  **Concurrent Streams:**
    *   **Assumption:** The plan will proceed with the assumption of **3 concurrent streams**. The recommended hardware can likely support more, but this is a safe baseline.

## **2. Technology Stack Evaluation**

### **2.1. Server Application ("Render Engine")**

The core of this project is the server application. It needs to be performant, stable, and capable of interoperating with native C libraries (`libbluray`).

*   **C# with .NET:**
    *   **Pros:** High performance, excellent tooling with Visual Studio, mature ecosystem. P/Invoke is a well-established mechanism for calling native libraries. Cross-platform with .NET 6/7/8.
    *   **Cons:** Integrating with `libbluray` and its dependencies on Linux can be complex, requiring careful marshalling of data types.
*   **Kotlin (JVM):**
    *   **Pros:** Modern, safe language. Runs on the mature and performant JVM. Excellent interoperability with Java libraries, which is a major advantage for the BD-J requirement (which runs in a Java virtual machine). JNA (Java Native Access) provides a straightforward way to call `libbluray`.
    *   **Cons:** Slightly higher memory footprint than a native C# application.
*   **Dart:**
    *   **Pros:** Fast, AOT-compiled to native code.
    *   **Cons:** While Dart has an FFI (Foreign Function Interface), it is less mature than C#'s P/Invoke or Java's JNA. The ecosystem for server-side development is smaller than .NET or the JVM's. This is the highest-risk option.

**Decision:** **Kotlin (JVM)** is the recommended choice. The native support for a Java environment is a critical advantage for fulfilling the BD-J requirement (FR1.4). The stability of the JVM and the ease of use of JNA for integrating with `libbluray` make it the most robust and lowest-risk option.

### **2.2. Client Application ("Remote Viewer")**

The client must be a modern, responsive web application.

*   **Framework:** **React with TypeScript**.
    *   **Justification:** React is the industry standard for building dynamic user interfaces. TypeScript adds static typing, which improves code quality and maintainability.
*   **Styling:** **Tailwind CSS**.
    *   **Justification:** A utility-first CSS framework that will allow for rapid development of a clean, simple, and responsive UI as required by NFR2.1.
*   **Communication:**
    *   **WebRTC:** For the low-latency A/V stream.
    *   **WebSockets:** For sending remote control commands and receiving server events (e.g., track information).

**WASM/Blazor Evaluation:**

*   **WASM (WebAssembly):** While it would be possible to compile a C/C++ library to WASM to run a "player" in the browser, this approach is not suitable here. The core requirement is to run the playback engine *on the server*.
*   **Blazor:** Blazor is a C#-based web framework. Since we have selected Kotlin for the server, using Blazor for the client would introduce an unnecessary second technology stack. A standard TypeScript/React frontend is a more direct and widely supported approach for interacting with a Kotlin backend.

## **3. Project Milestones**

### **Milestone 1: Core Engine Proof of Concept (PoC)**

*   **Goal:** Validate the core playback functionality.
*   **Tasks:**
    1.  Set up a Kotlin development environment on Ubuntu Server.
    2.  Install `libbluray`, `libdvdnav`, and their dependencies.
    3.  Create a simple Kotlin application that uses JNA to:
        *   Open a specified .iso file.
        *   List all available titles, audio tracks, and subtitle tracks.
        *   "Play" a title and read the raw video/audio frames.
    4.  Create a simple "main loop" that can simulate navigating the menu (e.g., send "Enter" or "Arrow Up" commands).
*   **Deliverable:** A command-line application that proves we can control a Blu-ray .iso with `libbluray` from a Kotlin application.

### **Milestone 2: Server Application Development**

*   **Goal:** Build the full server application around the PoC.
*   **Tasks:**
    1.  **API Development:**
        *   Create a web server using a Kotlin framework (e.g., Ktor).
        *   Implement an API endpoint (`/library`) that scans the NAS path and returns a JSON list of available .iso files.
    2.  **Session Management:**
        *   Implement logic to manage concurrent playback sessions.
        *   Create endpoints to start (`/play/{iso_filename}`) and stop (`/stop/{session_id}`) a session.
    3.  **Streaming Implementation:**
        *   Integrate a WebRTC library for Kotlin.
        *   For each active session, take the decoded video/audio frames from `libbluray`, encode them into an H.264 stream, and send them over a WebRTC `MediaStream`.
    4.  **Remote Control:**
        *   Set up a WebSocket endpoint (`/control/{session_id}`).
        *   Listen for JSON commands (e.g., `{"command": "arrow_up"}`) and translate them into the appropriate `libbluray` function calls for the correct session.
*   **Deliverable:** A runnable server application that can serve a library, stream a disc over WebRTC, and be controlled via WebSocket commands.

### **Milestone 3: Web Client Development**

*   **Goal:** Create the user-facing remote viewer.
*   **Tasks:**
    1.  **Project Setup:**
        *   Initialize a new React/TypeScript project using Vite.
        *   Set up Tailwind CSS.
    2.  **Library View:**
        *   Create a component that fetches data from the server's `/library` endpoint.
        *   Display the titles in a simple grid or list.
        *   Clicking a title should trigger a request to the `/play` endpoint and navigate the user to the player view.
    3.  **Player View:**
        *   Create a main video component that establishes a WebRTC connection with the server and displays the incoming stream.
        *   Implement the WebSocket connection to the `/control` endpoint.
        *   Capture keyboard arrow events and mouse clicks and send the corresponding commands over the WebSocket.
        *   Create on-screen buttons for "Top Menu," "Pop-up Menu," etc., that also send commands.
    4.  **OSD (On-Screen Display):**
        *   Create an overlay that appears on mouse-over.
        *   This overlay will fetch and display the available audio/subtitle tracks for the current stream.
        *   Clicking a track will send the appropriate "set track" command to the server.
*   **Deliverable:** A complete, functional web application that can be served as a static site.

### **Milestone 4: Integration, Testing, and Deployment**

*   **Goal:** Finalize the system and prepare for delivery.
*   **Tasks:**
    1.  **Configuration:**
        *   Implement server configuration from a simple `config.yaml` file (for NAS paths, etc.).
    2.  **Testing:**
        *   Perform end-to-end testing with a variety of DVD, Blu-ray, and BD-J .iso files.
        *   Test on all target browsers (Chrome, Firefox, Safari, Edge) and devices (desktop, tablet).
        *   Verify that all functional and non-functional requirements are met (e.g., latency, playback quality).
    3.  **Deployment:**
        *   Package the server application as a systemd service for 24/7 operation on the Ubuntu server.
        *   Provide documentation on how to install and configure the server.
        *   Provide instructions on how to access the web client.
*   **Deliverable:** A fully deployed and documented system, ready for client use.
