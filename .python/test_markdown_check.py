"""The check path must detect drift without writing, and the punctuation rule must cover every language."""
import importlib.util
from pathlib import Path
import re
import tempfile
import unittest

spec = importlib.util.spec_from_file_location("generator_under_test", Path(__file__).with_name("generate_markdown.py"))
generator = importlib.util.module_from_spec(spec)
spec.loader.exec_module(generator)

class ReadOnlyCheckTest(unittest.TestCase):
    def test_existing_drift_is_reported_without_writing(self):
        with tempfile.TemporaryDirectory() as directory:
            generator.ROOT = Path(directory)
            generator.CHECK_MODE = True
            generator.CHECK_DIFFERENCES.clear()
            path = generator.ROOT / "README.md"
            path.write_bytes(b"old content")
            before = path.stat().st_mtime_ns
            generator.write_text(path, "new content")
            self.assertEqual(b"old content", path.read_bytes())
            self.assertEqual(before, path.stat().st_mtime_ns)
            self.assertEqual(["README.md"], generator.CHECK_DIFFERENCES)

    def test_missing_output_is_not_created(self):
        with tempfile.TemporaryDirectory() as directory:
            generator.ROOT = Path(directory)
            generator.CHECK_MODE = True
            generator.CHECK_DIFFERENCES.clear()
            path = generator.ROOT / "missing" / "README.md"
            generator.write_text(path, "new content")
            self.assertFalse(path.parent.exists())
            self.assertEqual(1, len(generator.CHECK_DIFFERENCES))

class PunctuationRuleTest(unittest.TestCase):
    def test_non_ascii_punctuation_is_rejected_in_every_language(self):
        # Arabic, CJK, fullwidth, dash, quote, Spanish, katakana middle dot and arrow samples.
        for code_point in (0x060C, 0x061B, 0x061F, 0x3001, 0xFF0C, 0x2013, 0x2019, 0x00AB, 0x00BF, 0x30FB, 0x2192):
            with self.subTest(code_point=f"U+{code_point:04X}"):
                with self.assertRaisesRegex(ValueError, re.escape(f"U+{code_point:04X}")):
                    generator.validate_symbols("text" + chr(code_point), "values-ar/strings.xml")

    def test_value_separator_and_bidi_isolates_are_allowed(self):
        generator.validate_symbols(
            "75% " + chr(0x00B7) + " " + chr(0x2068) + "3-Amber Player" + chr(0x2069) + " 0,25" + chr(0x00D7),
            "values-ar/strings.xml",
        )

if __name__ == "__main__":
    unittest.main()
