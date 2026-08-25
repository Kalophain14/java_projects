"""
main.py — CUPS queue listener loop.

Responsibility:
    Entry point for the daemon. Attaches to the CUPS print spooler (via
    pycups) and watches the DCP (Data Card Printer) queue specifically.
    For every incoming job:
      1. Pull job metadata (target printer, page count, media size)
      2. Hand metadata to inspector.py for rule evaluation
      3. Hand the verdict to action.py to allow / cancel / reroute
    Runs continuously as a background service (e.g. under systemd).

Not implemented here — this is a structural placeholder only.
"""
