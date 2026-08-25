"""
inspector.py — Print job metadata analyzer.

Responsibility:
    Evaluate a job's metadata against config/rules.json:
      - Target printer name matches the DCP queue?
      - Page count > 1 (card jobs are always exactly 1 page)?
      - Media size is A4/Letter/Legal instead of a card format (CR-80, ID-1)?
    Returns a pass/fail verdict (and which rule triggered) for main.py to act on.

Not implemented here — this is a structural placeholder only.
"""
