#!/usr/bin/env bash
# Regenerates the store icon and the feature graphic from the rendered launcher icon.
set -e
cd "$(dirname "$0")"
python make_graphics.py icon --emblem icon-512.png --out icon-512.png
python make_graphics.py feature --emblem icon-512.png --name "CONFUCIUS" \
  --tagline "The Analects, one chapter a day" \
  --line "All 499 chapters in James Legge's translation" \
  --line "Twelve key words, with the Chinese beside the English" \
  --bg "#121013,#1C191C,#2A1714" --accent "#E25A41" --text "#EDE3CF" --muted "#AEA596" \
  --motif none --no-stars --out feature-graphic-1024x500.png
