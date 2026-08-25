"""
action.py — Response handler for rule violations.

Responsibility:
    Given a "violation" verdict from inspector.py, carry out the configured
    response:
      1. Cancel the job on the DCP queue immediately (pycups)
      2. Optionally resubmit the same document to the branch's standard
         A4 office printer queue (auto-route)
      3. Optionally fire a desktop notification to the consultant
         (via dbus-python) explaining what happened
    Also supports a "log only" mode for the silent-monitoring rollout phase,
    where violations are recorded but no job is touched.

Not implemented here — this is a structural placeholder only.
"""
