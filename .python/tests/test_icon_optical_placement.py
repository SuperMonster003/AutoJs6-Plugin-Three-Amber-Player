import importlib.util
import math
from pathlib import Path
import unittest

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
spec = importlib.util.spec_from_file_location("icons", ROOT / ".python/generate_launcher_icons.py")
icons = importlib.util.module_from_spec(spec)
spec.loader.exec_module(icons)


class OpticalPlacementTest(unittest.TestCase):
    def test_visible_mass_is_balanced_in_ui_and_launcher_sizes(self):
        # The old bbox-centered triangle was ~42 px left in UI and ~25 px left
        # in adaptive artwork. Measure real alpha rather than echoing an offset.
        for resource in ("mipmap/ic_launcher.png", "mipmap/ic_launcher_system_foreground.png"):
            alpha = Image.open(icons.RES / resource).getchannel("A")
            pixels = [(x, y, alpha.getpixel((x, y))) for y in range(alpha.height) for x in range(alpha.width)]
            mass = sum(value for _, _, value in pixels)
            cx = sum(x * value for x, _, value in pixels) / mass
            cy = sum(y * value for _, y, value in pixels) / mass
            self.assertLess(abs(cx - 215.5), 432 * .03)
            self.assertLess(abs(cy - 215.5), 432 * .01)

    def test_every_nonzero_pixel_fits_the_offset_safe_circle(self):
        for ratio, radius in ((icons.ADAPTIVE_GLYPH, 132), (icons.UI_GLYPH, 216)):
            icons.validate_circle(icons.glyph_alpha(icons.source_alpha(), ratio), radius)
        with self.assertRaises(ValueError):
            icons.glyph_alpha(icons.source_alpha(), icons.UI_GLYPH, optical_x=1)
        with self.assertRaises(ValueError):
            icons.validate_circle(icons.glyph_alpha(icons.source_alpha(), icons.ADAPTIVE_GLYPH, optical_x=.5), 132)

    def test_color_variants_share_shape_and_match_generator_bytes(self):
        day = Image.open(icons.RES / "mipmap/ic_launcher.png").getchannel("A")
        night = Image.open(icons.RES / "mipmap-night/ic_launcher.png").getchannel("A")
        self.assertEqual(day.tobytes(), night.tobytes())
        expected = icons.generated_files()
        self.assertEqual(expected, icons.generated_files())
        for path, data in expected.items():
            self.assertEqual(path.read_bytes(), data, str(path))


if __name__ == "__main__":
    unittest.main()
