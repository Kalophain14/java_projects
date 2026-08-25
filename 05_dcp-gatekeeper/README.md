# DCP Gatekeeper — Automated Print Job Interceptor

A lightweight **Java 21** daemon that monitors a branch's **Windows Print Spooler** and prevents document-sized print jobs from reaching a **Data Card Printer (DCP)**.

Instead of allowing an A4 or multi-page document to lock up the printer's buffer and require manual intervention, the service inspects each queued job and automatically allows, cancels, or reroutes it based on configurable rules.

---

# Technology Stack

* **Java 21**
* **Spring Boot 3**
* **JNA (Java Native Access)** — binding to the Win32 `winspool.drv` print spooler API
* **Windows Print Spooler API** (`EnumJobs`, `FindFirstPrinterChangeNotification`)
* **Jackson** (configuration parsing)
* **SLF4J + Logback** (structured logging)
* **JUnit 5 + Mockito** (testing)
* **Windows 10 / 11 / Windows Server** (branch environment)
* **WinSW** (Windows Service wrapper for the packaged JAR)

---

# Project Structure

```text
dcp-gatekeeper/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/capitecbank/dcpgatekeeper/
│   │   │       ├── action/
│   │   │       │   ├── CancelAction.java
│   │   │       │   ├── RerouteAction.java
│   │   │       │   └── NotificationService.java
│   │   │       │
│   │   │       ├── config/
│   │   │       │   ├── RulesConfig.java
│   │   │       │   └── ConfigLoader.java
│   │   │       │
│   │   │       ├── inspector/
│   │   │       │   ├── Inspector.java
│   │   │       │   └── InspectionResult.java
│   │   │       │
│   │   │       ├── listener/
│   │   │       │   ├── PrintSpoolerListener.java
│   │   │       │   └── WinSpoolBinding.java
│   │   │       │
│   │   │       ├── model/
│   │   │       │   └── PrintJob.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   └── GatekeeperService.java
│   │   │       │
│   │   │       └── DcpGatekeeperApplication.java
│   │   │
│   │   └── resources/
│   │       └── rules.json
│   │
│   └── test/
│       └── java/
│
├── logs/
│
├── winsw/
│   ├── dcp-gatekeeper.xml
│   └── WinSW.exe
│
├── README.md
└── pom.xml
```

---

# Architecture

```text
                       IT Hero
                           │
                           ▼
                Windows Print Spooler
                   (spoolsv.exe)
                           │
                           ▼
             PrintSpoolerListener (Java)
                     via JNA → winspool.drv
                           │
                           ▼
                 GatekeeperService
                           │
                           ▼
                     Inspector ◄──────── ConfigLoader
                           │                   │
                           │                   ▼
                           │              RulesConfig
                           │              (rules.json)
                           ▼
                 ┌─────────┴─────────┐
                 ▼                   ▼
            InspectionResult     RulesConfig
                 │
        ┌────────┴────────┐
        ▼                 ▼
     Allow            Action Layer
                       │
             ┌─────────┼─────────┐
             ▼         ▼         ▼
         Cancel    Reroute    Notify
```

`ConfigLoader` reads `rules.json` at startup (and on reload) and populates `RulesConfig`, which the `Inspector` consults on every job evaluation. This keeps rule data fully decoupled from the decision logic — the `Inspector` never touches the file system directly.

---

# Operational Flow

1. An **IT Hero** submits a print job from a branch workstation.
2. The job is accepted by the **Windows Print Spooler** and queued against the DCP's printer object.
3. `PrintSpoolerListener` receives a change notification and pulls the job's `JOB_INFO_2` metadata.
4. The inspection engine evaluates the job against configured rules.
5. The action layer determines the outcome:

   * Allow the job.
   * Cancel the job.
   * Reroute it to the standard office printer.
   * Notify the consultant.

---

# How the Print Spooler Listener Works

`CupsListener` has been replaced with **`PrintSpoolerListener`**, since Windows branch machines don't run CUPS — print jobs are managed entirely by the native **Windows Print Spooler** service (`spoolsv.exe`).

## Native binding

`PrintSpoolerListener` uses **JNA** to call directly into `winspool.drv`, avoiding any need for a separate native (C/C++) build step:

* **`OpenPrinter`** — opens a handle to the protected DCP printer object by name.
* **`FindFirstPrinterChangeNotification`** / **`FindNextPrinterChangeNotification`** — registers for `PRINTER_CHANGE_ADD_JOB` events, so the listener reacts the instant a job is queued rather than polling.
* **`EnumJobs`** — once notified, retrieves the full job list as an array of **`JOB_INFO_2`** structures.

`WinSpoolBinding.java` isolates all of this native interop behind a small interface, so `PrintSpoolerListener` itself stays plain Java and testable with Mockito (the native calls are mocked out in unit tests).

## Metadata mapping

CUPS and Windows expose job metadata differently, so the `Inspector`'s field mapping changes accordingly:

| Signal        | CUPS (previous)      | Windows Print Spooler (current)                                  |
| ------------- | --------------------- | ------------------------------------------------------------------ |
| Target queue  | CUPS queue name        | `pPrinterName` on the `JOB_INFO_2` struct                          |
| Media size    | `media` job attribute  | `dmPaperSize` / `dmFormName` inside the job's embedded `DEVMODE`   |
| Page count    | `job-media-sheets`      | `TotalPages` field on `JOB_INFO_2`                                 |
| Document type | `document-format`       | `pDatatype` field on `JOB_INFO_2`                                  |

Card printer drivers typically register a **custom form name** (e.g. `CR-80`, `ID-1`) rather than using a standard Windows paper size like `A4` or `Letter`, so `allowedMedia` in `rules.json` is matched against `dmFormName` first, falling back to `dmPaperSize` if no custom form is set.

## Fallback polling mode

Some third-party card printer drivers don't reliably fire spooler change notifications. For those environments, `PrintSpoolerListener` can run in **polling mode** instead — calling `EnumJobs` on a fixed interval (default 2s, configurable in `rules.json`) rather than waiting on `FindFirstPrinterChangeNotification`. This trades a small amount of latency for reliability on flaky drivers.

---

# Detection Rules

The application distinguishes genuine card-print jobs from document print jobs using metadata exposed by the Windows Print Spooler.

| Signal        | Valid Card Job     | Misrouted Document           |
| ------------- | ------------------ | ----------------------------- |
| Target Queue  | DCP                 | DCP                           |
| Form / Media  | CR-80 / ID-1        | A4 / Letter / Legal            |
| Page Count    | 1                   | 2+                             |

Media/form name is treated as the primary indicator, while page count provides additional validation.

---

# Getting Started

## Prerequisites

* Windows 10 / 11 or Windows Server (branch workstation or print server)
* Java 21 (JDK)
* Maven 3.9+
* The DCP printer installed and registered as a Windows printer object, with a custom form (e.g. `CR-80`) configured in its driver
* Administrative rights on the machine (required to open printer handles and register for spooler change notifications)

## Build

```powershell
git clone https://github.com/<your-org>/dcp-gatekeeper.git
cd dcp-gatekeeper
mvn clean package
```

This produces an executable JAR at `target\dcp-gatekeeper-<version>.jar`.

## Configure

Edit `src/main/resources/rules.json` (or supply an external copy — see below) to match your branch's printer setup:

```json
{
  "mode": "active",
  "listenerMode": "notify",
  "pollIntervalMs": 2000,
  "queues": {
    "protected": [
      "DCP"
    ],
    "fallback": "OfficePrinter"
  },
  "limits": {
    "maxPages": 1
  },
  "allowedMedia": [
    "CR-80",
    "ID-1"
  ]
}
```

* `queues.protected` — the Windows printer name(s) to monitor (must match the name shown in `Get-Printer`).
* `queues.fallback` — where misrouted jobs get rerouted to.
* `listenerMode` — `notify` (event-driven, default) or `poll` (fallback for drivers that don't fire change notifications).
* `pollIntervalMs` — polling interval in milliseconds, only used when `listenerMode` is `poll`.
* `limits.maxPages` — page count above which a job is treated as a document.
* `allowedMedia` — form/paper names considered valid card jobs.

## Run

Run directly with Java for local testing (from an elevated PowerShell prompt):

```powershell
java -jar target\dcp-gatekeeper-<version>.jar --spring.config.location=file:./rules.json
```

Or install as a Windows Service using **WinSW** for production:

```powershell
copy target\dcp-gatekeeper-<version>.jar winsw\dcp-gatekeeper.jar
cd winsw
.\WinSW.exe install dcp-gatekeeper.xml
.\WinSW.exe start dcp-gatekeeper.xml
sc query DcpGatekeeper
```

## Verify

Submit a test document to the protected DCP printer and confirm it's cancelled or rerouted:

```powershell
Get-Printer -Name "DCP"
Start-Process -FilePath "some-test-document.pdf" -Verb Print
Get-EventLog -LogName Application -Source DcpGatekeeper -Newest 5
```

You should see a structured log entry with `"decision":"cancelled"` (or `"rerouted"`) for the test job.

---

# Core Components

## PrintSpoolerListener

* Registers for change notifications on the configured Windows printer(s) via `winspool.drv`
* Retrieves job metadata (`JOB_INFO_2`) through JNA
* Falls back to polling mode for drivers with unreliable notifications
* Passes jobs to the inspection service

---

## Inspector

Evaluates each print job against configurable rules and returns an `InspectionResult`.

Checks include:

* Target printer
* Media / form name
* Page count
* Queue configuration

---

## Action Layer

Responsible for executing the selected action.

Supported actions include:

* Allow
* Cancel
* Reroute
* Notify

Each action is implemented independently to keep the decision engine isolated from execution logic.

---

# Logging

Every inspected job is logged with structured metadata.

Example:

```json
{
  "timestamp":"2026-07-23T10:15:20Z",
  "jobId":421,
  "user":"jsmith",
  "printer":"DCP",
  "pages":3,
  "media":"A4",
  "decision":"cancelled",
  "reason":"Document media detected"
}
```

---

# Reliability

The application follows a **fail-open** strategy.

If configuration loading or rule evaluation fails:

* the error is logged;
* the print job proceeds normally;
* legitimate card printing is never blocked by a software failure.

---

# Deployment

The application is packaged as a Spring Boot executable JAR and deployed as a **Windows Service** using **WinSW**.

Example:

```powershell
.\WinSW.exe install dcp-gatekeeper.xml
.\WinSW.exe start dcp-gatekeeper.xml
sc query DcpGatekeeper
```

The service is configured to restart automatically after unexpected failures (via the `<onfailure>` action in `dcp-gatekeeper.xml`).

---

# Future Enhancements

* Hot reloading of configuration
* Web-based administration dashboard
* Multiple protected printer support

---

# Learning Objectives

This project demonstrates practical experience with:

* Java 21
* Spring Boot
* Object-oriented design
* Configuration-driven architecture
* Native interop (JNA) with the Windows Print Spooler API
* Windows service deployment (WinSW)
* Process management
* Structured logging
* Unit testing
* Long-running service development
* Defensive programming
* Enterprise application architecture