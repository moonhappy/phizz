# **Software Requirements Specification (SRS)**

# **Project: LAN-Based ISO Master Streaming System**

## **1\. Introduction**

### **1.1 Project Overview**

This document outlines the requirements for a custom, server-client software solution designed to stream playback of master DVD and Blu-ray .iso files from a Network Attached Storage (NAS) device to various client devices on a local area network (LAN).

### **1.2 Problem Statement**

The client, a professional video authoring business, maintains an archive of authored master discs (DVDs and Blu-rays) as .iso files. These masters contain complex menu structures, including Blu-ray Disc Java (BD-J) menus, multiple audio tracks, and subtitle tracks.

Standard media server solutions (e.g., Plex, Emby, Jellyfin) are designed for file-based media and typically transcode content, stripping away all menu functionality. This project requires a "virtual player" solution that streams the *full, interactive disc experience*—including all menus—as if a physical disc were being played in a high-fidelity player.

### **1.3 Goals and Objectives**

* **Primary Goal:** To enable high-fidelity, real-time streaming of the complete, unmodified DVD/Blu-ray playback experience (including all menus and tracks) from .iso files over a LAN.  
* **Objective 1:** Achieve 100% preservation and interactivity of all menu systems, from simple DVD menus to dynamic BD-J menus.  
* **Objective 2:** Provide a simple, intuitive client interface for browsing the .iso archive and initiating playback.  
* **Objective 3:** Ensure smooth, low-latency playback that is functionally indistinguishable from local disc playback.

## **2\. Scope**

### **2.1 In-Scope**

* **Server Application ("Render Server"):** A backend application that:  
  * Scans and indexes .iso files from a specified NAS location.  
  * Contains a full-featured disc playback engine (capable of mounting/reading ISOs and executing BD-J).  
  * "Renders" the audio/video output of a playback session in real-time.  
  * Streams this rendered A/V output to a client.  
  * Receives and processes "remote control" commands from the client (e.g., menu navigation).  
* **Client Application ("Remote Viewer"):** A lightweight application (preferably web-based) that:  
  * Displays a browseable library of available titles from the server.  
  * Initiates playback sessions.  
  * Receives and displays the rendered A/V stream from the server.  
  * Captures user input (mouse clicks, keyboard arrows) and transmits them as navigation commands to the server.  
* **Playback Functionality:**  
  * Full menu navigation (Top Menu, Pop-up Menu).  
  * In-stream selection of all available audio and subtitle tracks.  
  * Standard transport controls (Play, Pause, Stop).

### **2.2 Out-of-Scope**

* **Media Transcoding:** The system must *not* transcode the core A/V streams in a lossy way or "flatten" the file structure. The goal is full-fidelity rendering, not conversion.  
* **Wide Area Network (WAN) Streaming:** This solution is intended strictly for LAN use.  
* **Metadata Scraping:** The system does not need to automatically download movie posters, cast info, or other metadata from the internet. The library will be identified by filename or a simple internal identifier.  
* **Disc Authoring/Editing:** This is a playback-only system.  
* **User/Rights Management:** A simple access control (e.g., a single password) may be implemented, but complex multi-user permission systems are not required.

## **3\. Functional Requirements**

### **3.1 Server Application ("Render Engine")**

| ID | Requirement |
| :---- | :---- |
| FR1.1 | The server application must be able to scan and index all .iso files from one or more specified network paths (e.g., \\\\NAS\\Archive\\, /mnt/nas/archive/) using SMB/CIFS or NFS protocols. |
| FR1.2 | The server must provide a library API or data feed that the client application can consume to display available titles. |
| FR1.3 | Upon a client request, the server must instantiate a "virtual player" instance. This instance will mount or read the requested .iso file. |
| FR1.4 | The virtual player engine **must** be capable of fully parsing and executing **BD-J (Blu-ray Disc Java) menus**. |
| FR1.5 | The virtual player engine must be able to parse and display standard DVD menus. |
| FR1.6 | The server shall decode the selected A/V streams (MPEG-2, H.264, VC-1, AC-3, DTS, LPCM, etc.) from the .iso in real-time. |
| FR1.7 | The server shall re-encode and stream the *rendered audio/video output* (i.e., the video *after* menu interaction and track selection) to the client using a modern, low-latency streaming protocol (e.g., WebRTC, HLS with low-latency tuning, or similar). |
| FR1.8 | The server must listen for and accept "remote control" commands from the client (e.g., "Arrow Up," "Enter," "Menu," "Audio Track 2"). |
| FR1.9 | The server must apply these commands to the correct virtual player instance to navigate menus, change tracks, or control playback. |
| FR1.10 | The server must support a minimum of **\[X\]** concurrent playback sessions (e.g., 3). This number is TBD based on server hardware. |

### **3.2 Client Application ("Remote Viewer")**

| ID | Requirement |
| :---- | :---- |
| FR2.1 | The client shall be a web-based application, compatible with modern browsers (Chrome, Firefox, Safari, Edge) on both desktop and tablet devices. |
| FR2.2 | The client shall display a grid or list of available .iso titles provided by the server, using the filename as the primary identifier. |
| FR2.3 | Clicking a title shall initiate a new playback session with the server. |
| FR2.4 | The client shall receive and display the A/V stream from the server with minimal buffering and latency. |
| FR2.5 | The client interface must capture user input to simulate a Blu-ray remote control: |

\* Mouse clicks on the video area shall be translated to "Enter" or "Select."  
\* Keyboard arrow keys shall be translated to "Arrow Up/Down/Left/Right."  
\* On-screen buttons must be provided for "Top Menu," "Pop-up Menu," "Play," "Pause," and "Stop."

| FR2.6 | The client shall provide a simple, on-screen-display (OSD) overlay (e.g., on mouse-over) to: \* List and select available audio tracks. \* List and select available subtitle tracks. | FR2.7 | These OSD selections must send the corresponding command to the server's virtual player instance. |

## **4\. Non-Functional Requirements**

### **4.1 Performance**

| ID | Requirement |
| :---- | :---- |
| NFR1.1 | Playback initiation (from client click to first frame displayed) shall be less than 8 seconds. |
| NFR1.2 | Latency for menu navigation (from client input to visible change on stream) shall be less than 750ms. |
| NFR1.3 | Playback of high-bitrate (40+ Mbps) Blu-ray content shall be smooth and free of stuttering, buffering, or A/V sync issues. |

### **4.2 Usability**

| ID | Requirement |
| :---- | :---- |
| NFR2.1 | The client interface must be simple, clean, and intuitive, requiring no technical knowledge to operate. "Point and click" to browse, "point and click" to play. |
| NFR2.2 | The server application should be configurable via a simple text file (e.g., config.yaml) or a basic web-based admin panel. |

### **4.3 Reliability**

| ID | Requirement |
| :---- | :---- |
| NFR3.1 | The server application must be stable and capable of running 24/7 as a background service or daemon. |
| NFR3.2 | The system must gracefully handle and report errors (e.g., "Corrupt ISO file," "Network path not found"). |

### **4.4 Compatibility**

| ID | Requirement |
| :---- | :---- |
| NFR4.1 | **Server OS:** Contractor to recommend the optimal host OS (e.g., Windows Server 2022, Ubuntu Server 22.04). |
| NFR4.2 | **Client OS/Browser:** Must function on Windows 10/11 (Chrome, Edge), macOS (Safari, Chrome), iPadOS (Safari), and Android (Chrome). |
| NFR4.3 | **Network:** Assumes a stable, wired 1Gbps Ethernet LAN environment. |

## **5\. Assumptions and Open Questions**

This section lists items to be confirmed before development begins, as they will significantly impact the project's complexity.

### **5.1 Assumptions**

* The LAN environment is stable and has sufficient bandwidth (1Gbps+) for high-bitrate streaming.  
* The client will provide adequate server hardware (CPU, RAM, and potentially a GPU for decoding) as recommended by the contractor to meet the concurrent stream requirements.

### **5.2 Open Questions for Contractor**

1. **Encryption (Critical):** Are the master .iso files encrypted (e.g., with AACS)?  
   * *Note:* If masters are 1:1 copies of protected discs, the playback engine will require access to a database of decryption keys (e.g., VUKs). If they are unencrypted masters, this greatly simplifies development. **This must be answered.**  
2. **Core Engine:** What core technology do you propose for the "virtual player" engine? (e.g., a solution based on libbluray / libdvdnav, or a licensed commercial SDK).  
3. **Streaming Protocol:** What streaming protocol do you recommend to balance low latency (for menu interactivity) with playback quality?  
4. **Hardware Recommendation:** Based on a requirement of **\[X\]** concurrent 1080p Blu-ray streams, what are the minimum and recommended server hardware specifications?  
5. **Concurrent Streams:** What is the maximum number of concurrent playback sessions the business requires? (Default assumption is 3).