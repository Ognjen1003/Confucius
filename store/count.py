"""Measures the listing fields and writes the full-description count into LISTING.md."""
import re
from pathlib import Path

p = Path(__file__).with_name("LISTING.md")
s = p.read_text(encoding="utf-8")
blocks = re.findall(r"```\n(.*?)\n```", s, re.S)
name, short, full = blocks[0], blocks[1], blocks[2]
print(f"name {len(name)}/30, short {len(short)}/80, full {len(full)}/4000")
assert len(name) <= 30 and len(short) <= 80 and len(full) <= 4000
assert "\u2014" not in s
s = re.sub(r"Character count: \*\*(COUNT_FULL|\d+)\*\*", f"Character count: **{len(full)}**", s)
p.write_text(s, encoding="utf-8")
