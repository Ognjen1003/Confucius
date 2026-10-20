"""
Play Store icon and feature graphic, generated rather than drawn by hand.

Two decisions shape this file.

**The emblem is the app's real icon, not a redrawing of it.** Both subcommands take the rendered
512 launcher icon as input; the feature graphic composites it and derives its gradient and accent
from that icon's own pixels. An app cannot end up with a store mark that has quietly drifted from
the one on the home screen, and adding a new app needs no new drawing code.

**Every line of type is measured before it is drawn.** A promotional graphic does not truncate an
overflowing line with an ellipsis — the canvas edge cuts it, silently. `fitted()` picks the largest
size that actually fits, so editing the copy cannot break the image.

Usage:
    python make_graphics.py icon    --emblem in.png [--out out.png] [--size 512]
    python make_graphics.py feature --emblem icon-512.png --name "APP" \
        --tagline "One line that must survive a crop" \
        --line "Secondary line" [--line "Another"] \
        [--bg "#0A0E1F,#161B33,#241536"] [--accent "#E0C36B"] \
        [--text "#CED6E0"] [--muted "#9AA2B8"] [--motif rings|none] [--out out.png]
"""

import argparse
import colorsys
import math
import os

from PIL import Image, ImageDraw, ImageFilter, ImageFont

# Serif for the wordmark, sans for everything else. Overridable: a suite that ever gets a face of
# its own changes these two lines and nothing else.
SERIF_BOLD = os.environ.get("PLAY_SERIF_BOLD", "C:/Windows/Fonts/cambriab.ttf")
SANS = os.environ.get("PLAY_SANS", "C:/Windows/Fonts/segoeui.ttf")
SANS_SEMI = os.environ.get("PLAY_SANS_SEMI", "C:/Windows/Fonts/seguisb.ttf")


# ---------------------------------------------------------------------------------------------
# Colour
# ---------------------------------------------------------------------------------------------

def hex_to_rgb(s):
    s = s.strip().lstrip("#")
    return tuple(int(s[i:i + 2], 16) for i in (0, 2, 4))


def lerp(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def derive_palette(emblem):
    """
    A three-stop background and an accent, read off the icon itself.

    The background stops come from the **darkest** pixels (an icon of this kind is artwork on a
    dark ground, and that ground is the app's colour), the accent from the most saturated bright
    one. Sampled on a thumbnail: exact pixels do not matter and a 512x512 scan per run does.
    """
    small = emblem.convert("RGB").resize((64, 64), Image.LANCZOS)
    pixels = list(small.getdata())

    by_luma = sorted(pixels, key=lambda p: 0.2126 * p[0] + 0.7152 * p[1] + 0.0722 * p[2])
    dark = by_luma[: len(by_luma) // 4]
    base = tuple(sum(c[i] for c in dark) // len(dark) for i in range(3))

    def saturation(p):
        return colorsys.rgb_to_hsv(*[c / 255 for c in p])[1]

    bright = [p for p in pixels if max(p) > 120]
    accent = max(bright, key=saturation) if bright else (224, 195, 107)

    # A floor on the accent's value. The wordmark is drawn in it against a near-black ground, and
    # an app whose icon uses a deep gold (Jyotish) yields an accent that reads as brown at title
    # size. Lifting the value in HSV keeps the app's hue - which is the whole point of deriving it
    # - while making the one word that has to be read from a thumbnail actually readable.
    h_, s_, v_ = colorsys.rgb_to_hsv(*[c / 255 for c in accent])
    if v_ < 0.82:
        accent = tuple(round(c * 255) for c in colorsys.hsv_to_rgb(h_, s_, 0.82))

    # Two companions to the base: a step towards the accent's hue, and a step towards violet, which
    # is what gives a flat dark ground some depth across 1024 px.
    mid = lerp(base, accent, 0.10)
    far = lerp(base, (60, 30, 80), 0.35)
    return [base, mid, far], accent


def diagonal_gradient(size, stops):
    """Corner-to-corner linear gradient through three colours."""
    w, h = size
    img = Image.new("RGB", size)
    px = img.load()
    for y in range(h):
        ty = y / max(1, h - 1)
        for x in range(w):
            t = (x / max(1, w - 1)) * 0.45 + ty * 0.55
            px[x, y] = lerp(stops[0], stops[1], t / 0.55) if t <= 0.55 \
                else lerp(stops[1], stops[2], (t - 0.55) / 0.45)
    return img


# ---------------------------------------------------------------------------------------------
# Decoration
# ---------------------------------------------------------------------------------------------

def starfield(draw, rect, seed, count, max_r):
    """Scattered points. Deterministic from `seed`, so a re-run produces the same image."""
    x0, y0, x1, y1 = rect
    state = seed
    for _ in range(count):
        vals = []
        for _ in range(3):
            state = (1103515245 * state + 12345) % (2 ** 31)
            vals.append(state / (2 ** 31))
        fx, fy, fr = vals
        x, y = x0 + fx * (x1 - x0), y0 + fy * (y1 - y0)
        r = max_r * (0.25 + 0.75 * fr)
        draw.ellipse([x - r, y - r, x + r, y + r], fill=(255, 255, 255, int(70 + 150 * fr)))


def ring_motif(size, cx, cy, r, accent, ss):
    """
    Concentric rings with divisions and a few chords: a wheel, read as texture rather than as an
    illustration. Faint on purpose — it is the ground the type sits on. It suits any app whose
    subject is a circle (a chart, a compass, a calendar); pass `--motif none` when it does not.
    """
    layer = Image.new("RGBA", size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    for rr, alpha in ((r, 90), (r * 0.87, 70), (r * 0.74, 55), (r * 0.50, 45)):
        d.ellipse([cx - rr, cy - rr, cx + rr, cy + rr], outline=accent + (alpha,), width=3 * ss)
    for i in range(12):
        a = math.radians(i * 30 + 8)
        d.line([cx + r * 0.87 * math.cos(a), cy + r * 0.87 * math.sin(a),
                cx + r * math.cos(a), cy + r * math.sin(a)], fill=accent + (70,), width=2 * ss)
        d.line([cx + r * 0.50 * math.cos(a), cy + r * 0.50 * math.sin(a),
                cx + r * 0.74 * math.cos(a), cy + r * 0.74 * math.sin(a)],
               fill=accent + (34,), width=2 * ss)
    for a1, a2, col, alpha in ((18, 138, (122, 190, 140), 60), (18, 200, (181, 80, 46), 55),
                               (138, 258, (122, 190, 140), 55), (200, 320, (122, 190, 140), 40)):
        p1 = (cx + r * 0.50 * math.cos(math.radians(a1)), cy + r * 0.50 * math.sin(math.radians(a1)))
        p2 = (cx + r * 0.50 * math.cos(math.radians(a2)), cy + r * 0.50 * math.sin(math.radians(a2)))
        d.line([p1, p2], fill=col + (alpha,), width=2 * ss)
    return layer


# ---------------------------------------------------------------------------------------------
# Type
# ---------------------------------------------------------------------------------------------

def fitted(path, text, max_width, start_size, min_size=8):
    """
    The largest size of `path` at which `text` still fits `max_width`.

    Chosen by measurement rather than by eye, because an overflowing line is not shortened with an
    ellipsis here — it is cut by the edge of the canvas, and the result reads as a typo.
    """
    size = start_size
    while size > min_size:
        f = ImageFont.truetype(path, size)
        if f.getbbox(text)[2] <= max_width:
            return f
        size -= 1
    return ImageFont.truetype(path, min_size)


# ---------------------------------------------------------------------------------------------
# Subcommands
# ---------------------------------------------------------------------------------------------

def build_icon(args):
    """
    Normalise a rendered launcher icon into the store icon.

    Almost all of the work is done before this runs — the point of the step is the **mode**: Play's
    field is a 32-bit PNG with an alpha channel, and a rendering pipeline that produced RGB gives a
    24-bit file. The channel is fully opaque; Play applies its own rounded mask over a full-bleed
    square.
    """
    img = Image.open(args.emblem).convert("RGBA")
    if img.size != (args.size, args.size):
        img = img.resize((args.size, args.size), Image.LANCZOS)
    out = args.out or args.emblem
    img.save(out, "PNG")
    return out


def build_feature(args):
    """
    1024x500, no alpha.

    Composition rule: **the readable half is the left half.** Play crops and overlays this image
    differently across its surfaces — a play button lands centre on some of them — so the name and
    the one sentence that has to survive sit left of centre, and the right is carried by the motif,
    which loses nothing to a crop.
    """
    w, h, ss = args.width, args.height, 3
    W, H = w * ss, h * ss

    emblem = Image.open(args.emblem).convert("RGBA")
    stops, accent = derive_palette(emblem)
    if args.bg:
        stops = [hex_to_rgb(c) for c in args.bg.split(",")]
    if args.accent:
        accent = hex_to_rgb(args.accent)
    text_col = hex_to_rgb(args.text) if args.text else (206, 214, 224)
    muted_col = hex_to_rgb(args.muted) if args.muted else (154, 162, 184)

    img = diagonal_gradient((W, H), stops).convert("RGBA")

    # A starfield suits a sky app and nothing else: a text app (Kybalion) must look like a book.
    if args.stars:
        stars = Image.new("RGBA", (W, H), (0, 0, 0, 0))
        starfield(ImageDraw.Draw(stars), (0, 0, W, H), seed=20260909, count=170, max_r=W * 0.0024)
        img.alpha_composite(stars)

    if args.motif == "rings":
        img.alpha_composite(ring_motif((W, H), W * 0.815, H * 0.50, H * 0.60, accent, ss))

    # The emblem, left, as the anchor — **masked to a circle**.
    # The store icon is a full-bleed square carrying its own background, so compositing it flat onto
    # a different gradient shows its edges as a paler box floating in the artwork. It is masked.
    #
    # **A rounded square, not a circle.** Play and the launchers mask to a squircle, and more to the
    # point the input here is already the adaptive icon's *visible* 72dp window — artwork authored
    # to fill it. A circle cuts the corners off a mark that was drawn to reach them, which is
    # exactly what happens to a square yantra or a Lo Shu grid. The mask is drawn at 4x and
    # downsampled, which is what feathers the rim instead of leaving a staircase.
    side = int(H * 0.56)
    shape = emblem.resize((side, side), Image.LANCZOS)
    mask = Image.new("L", (side * 4, side * 4), 0)
    ImageDraw.Draw(mask).rounded_rectangle(
        [0, 0, side * 4 - 1, side * 4 - 1], radius=int(side * 4 * 0.24), fill=255,
    )
    shape.putalpha(mask.resize((side, side), Image.LANCZOS))
    img.alpha_composite(shape, (int(W * 0.052), int(H * 0.5 - side / 2)))

    # A soft plate under the type: the motif passes behind it, and thin grey text over a bright arc
    # is the one thing that would make this unreadable at thumbnail size.
    plate = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    ImageDraw.Draw(plate).rectangle([W * 0.235, 0, W * 0.80, H], fill=stops[0] + (120,))
    img.alpha_composite(plate.filter(ImageFilter.GaussianBlur(28 * ss)))

    d = ImageDraw.Draw(img)
    x = W * 0.335
    room = W * 0.965 - x
    lines = (args.line or [])[:2]
    d.text((x, H * 0.400), args.name,
           font=fitted(SERIF_BOLD, args.name, room, int(H * 0.20)), fill=accent, anchor="ls")
    if args.tagline:
        d.text((x, H * 0.548), args.tagline,
               font=fitted(SANS_SEMI, args.tagline, room, int(H * 0.093)), fill=text_col, anchor="ls")
    for i, line in enumerate(lines):
        d.text((x, H * (0.668 + 0.090 * i)), line,
               font=fitted(SANS, line, room, int(H * 0.062)), fill=muted_col, anchor="ls")

    # RGB, not RGBA: Play rejects a feature graphic that carries an alpha channel.
    img.convert("RGB").resize((w, h), Image.LANCZOS).save(args.out, "PNG")
    return args.out


def main():
    p = argparse.ArgumentParser(description=__doc__)
    sub = p.add_subparsers(dest="cmd", required=True)

    pi = sub.add_parser("icon", help="normalise a rendered launcher icon into the 512 store icon")
    pi.add_argument("--emblem", required=True)
    pi.add_argument("--out")
    pi.add_argument("--size", type=int, default=512)
    pi.set_defaults(func=build_icon)

    pf = sub.add_parser("feature", help="1024x500 feature graphic")
    pf.add_argument("--emblem", required=True, help="the 512 store icon, composited as the mark")
    pf.add_argument("--name", required=True)
    pf.add_argument("--tagline")
    pf.add_argument("--line", action="append", help="repeatable; at most two are drawn")
    pf.add_argument("--bg", help="three hex stops, comma separated")
    pf.add_argument("--accent")
    pf.add_argument("--text")
    pf.add_argument("--muted")
    pf.add_argument("--motif", choices=["rings", "none"], default="rings")
    pf.add_argument("--no-stars", dest="stars", action="store_false", help="plain gradient, no starfield")
    pf.add_argument("--width", type=int, default=1024)
    pf.add_argument("--height", type=int, default=500)
    pf.add_argument("--out", default="feature-graphic-1024x500.png")
    pf.set_defaults(func=build_feature)

    args = p.parse_args()
    out = args.func(args)
    im = Image.open(out)
    print(f"{out}  {im.size[0]}x{im.size[1]}  {im.mode}")


if __name__ == "__main__":
    main()
