# -*- coding: utf-8 -*-
import argparse
import json
import re
import xml.etree.ElementTree as ElementTree
from pathlib import Path


LANGUAGE_CODES = [
    "zh-Hans",
    "zh-Hant-HK",
    "zh-Hant-TW",
    "en",
    "fr",
    "es",
    "ja",
    "ko",
    "ru",
    "ar",
]
LANGUAGE_CODE_DEFAULT = "zh-Hans"
README_COMPAT_ROOT_LANGUAGES = set()
ANDROID_CHANGELOG_ALIASES = {
    "zh-Hans": ["zh", "zh-Hans"],
    "zh-Hant-HK": ["zh-rHK", "zh-Hant-HK"],
    "zh-Hant-TW": ["zh-rTW", "zh-Hant-TW"],
}
ANDROID_RESOURCE_QUALIFIERS = {
    "zh-Hans": "zh",
    "zh-Hant-HK": "zh-rHK",
    "zh-Hant-TW": "zh-rTW",
    "en": "en",
    "fr": "fr",
    "es": "es",
    "ja": "ja",
    "ko": "ko",
    "ru": "ru",
    "ar": "ar",
}
# Markdown sources and outputs stick to halfwidth punctuation; Android UI resources
# (strings.xml, plugin_instruction.md) keep native punctuation and are exempt.
PROHIBITED_SYMBOL_PATTERN = re.compile(
    r"[‐-‧　-〿・︐-﹯＀-￯]",
)


def project_root() -> Path:
    return Path(__file__).resolve().parents[1]


ROOT = project_root()
README_DIR = ROOT / ".readme"
CHANGELOG_DIR = ROOT / ".changelog"
ANDROID_CHANGELOG_DIR = ROOT / "app" / "src" / "main" / "assets" / "doc"
ANDROID_RES_DIR = ROOT / "app" / "src" / "main" / "res"
VERSION_PROPERTIES = ROOT / "version.properties"


def validate_source_layout():
    expected_changelog_files = {
        "template_changelog.md",
        *(f"lang_{code}.json" for code in LANGUAGE_CODES),
    }
    changelog_entries = list(CHANGELOG_DIR.iterdir())
    actual_changelog_files = {path.name for path in changelog_entries}
    invalid_entry_types = sorted(path.name for path in changelog_entries if not path.is_file())
    if actual_changelog_files != expected_changelog_files or invalid_entry_types:
        missing = sorted(expected_changelog_files - actual_changelog_files)
        unexpected = sorted(actual_changelog_files - expected_changelog_files)
        raise ValueError(
            "Invalid .changelog entries; "
            f"missing={missing}, unexpected={unexpected}, non_files={invalid_entry_types}",
        )

    root_changelogs = sorted(path.name for path in ROOT.glob("CHANGELOG*.md"))
    if root_changelogs:
        raise ValueError(f"Root changelog files are not allowed: {root_changelogs}")

    root_compat_readmes = sorted(path.name for path in ROOT.glob("README-*.md"))
    if root_compat_readmes:
        raise ValueError(f"Root compatibility README files are not allowed: {root_compat_readmes}")


def validate_symbols(text: str, source):
    match = PROHIBITED_SYMBOL_PATTERN.search(text)
    if match:
        code_point = f"U+{ord(match.group()):04X}"
        raise ValueError(f"Prohibited full-width symbol {match.group()!r} ({code_point}) in {source}")


def validate_json_symbols(value, source):
    if isinstance(value, dict):
        for key, item in value.items():
            validate_symbols(str(key), source)
            validate_json_symbols(item, source)
    elif isinstance(value, list):
        for item in value:
            validate_json_symbols(item, source)
    elif isinstance(value, str):
        validate_symbols(value, source)


def load_json(path: Path):
    text = path.read_text(encoding="utf-8")
    validate_symbols(text, path.relative_to(ROOT))
    value = json.loads(text)
    validate_json_symbols(value, path.relative_to(ROOT))
    return value


def load_template(path: Path) -> str:
    text = path.read_text(encoding="utf-8")
    validate_symbols(text, path.relative_to(ROOT))
    return text


def render_template(text: str, values: dict) -> str:
    def repl(match):
        key = match.group(1).strip()
        if key not in values:
            raise KeyError(f"Missing template value: {key}")
        return str(values[key])

    return re.sub(r"\{\{\s*([A-Za-z0-9_$.-]+)\s*\}\}", repl, text)


def render_dynamic(value, values: dict):
    if isinstance(value, dict):
        return {k: render_dynamic(v, values) for k, v in value.items()}
    if isinstance(value, list):
        return [render_dynamic(v, values) for v in value]
    if isinstance(value, str):
        return render_template(value, values)
    return value


def markdown_link(label, url):
    return f"[{label}]({url})"


def bullet_list(items):
    return "\n".join(f"- {item}" for item in items)


def numbered_list(items):
    return "\n".join(f"{index}. {item}" for index, item in enumerate(items, start=1))


def faq_block(items):
    blocks = [f"**{item['question']}**\n\n{item['answer']}" for item in items]
    return "\n\n".join(blocks)


def changelog_version_name() -> str:
    properties = {}
    for line in VERSION_PROPERTIES.read_text(encoding="utf-8").splitlines():
        if not line or line.lstrip().startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        properties[key.strip()] = value.strip()
    version_name = properties.get("VERSION_NAME")
    if not version_name:
        raise ValueError("VERSION_NAME is missing from version.properties")
    return version_name if version_name.startswith("v") else f"v{version_name}"


def load_languages():
    common = load_json(README_DIR / "common.json")
    languages = {}
    changelogs = {}
    expected_version = changelog_version_name()
    expected_version_names = None
    for code in LANGUAGE_CODES:
        raw_lang = load_json(README_DIR / f"lang_{code}.json")
        merged_lang = {**common, **raw_lang}
        languages[code] = render_dynamic(merged_lang, merged_lang)

        synopsis = languages[code]["text_plugin_synopsis"]
        if synopsis != synopsis.rstrip():
            raise ValueError(f"Plugin synopsis must not end with whitespace: {code}")
        if synopsis.rstrip().endswith((".", "!", "?", ":", ";")):
            raise ValueError(f"Plugin synopsis must not end with punctuation: {code}")

        raw_changelog = load_json(CHANGELOG_DIR / f"lang_{code}.json")
        changelog_values = {k: v for k, v in raw_changelog.items() if k != "$data"}
        changelog_values = render_dynamic(changelog_values, changelog_values)
        changelog_data = render_dynamic(raw_changelog["$data"], changelog_values)
        version_names = list(changelog_data)
        if not version_names or version_names[0] != expected_version:
            raise ValueError(
                f"Latest changelog version for {code} must be {expected_version!r}: {version_names}",
            )
        if expected_version_names is None:
            expected_version_names = version_names
        elif version_names != expected_version_names:
            raise ValueError(f"Changelog versions are inconsistent for {code}: {version_names}")
        changelogs[code] = {
            "values": changelog_values,
            "data": changelog_data,
        }
    return languages, changelogs


def android_string_value(value: str) -> str:
    return value.replace("\\'", "'").replace('\\"', '"').replace("\\n", "\n")


def validate_android_resource_pair(code: str, languages, values_dir: str):
    strings_path = ANDROID_RES_DIR / values_dir / "strings.xml"
    root = ElementTree.fromstring(strings_path.read_text(encoding="utf-8"))
    element = next(
        (item for item in root.findall("string") if item.get("name") == "plugin_description"),
        None,
    )
    if element is None:
        raise ValueError(f"plugin_description is missing from {strings_path.relative_to(ROOT)}")
    description = android_string_value("".join(element.itertext()))
    synopsis = languages[code]["text_plugin_synopsis"]
    if description != synopsis:
        raise ValueError(
            f"plugin_description differs from text_plugin_synopsis for {code}: "
            f"{description!r} != {synopsis!r}",
        )


def validate_android_resources(languages):
    for code, qualifier in ANDROID_RESOURCE_QUALIFIERS.items():
        validate_android_resource_pair(code, languages, f"values-{qualifier}")
    validate_android_resource_pair("en", languages, "values")


def format_changelog_items(changelog, limit=None, heading_level=1):
    values = changelog["values"]
    data = changelog["data"]
    heading = "#" * heading_level
    chunks = []
    for index, (version_name, item) in enumerate(data.items()):
        if limit is not None and index >= limit:
            break
        lines = [
            f"{heading} {version_name}",
            "",
            f"###### {item['released_date']}",
            "",
        ]
        for category in ["hint", "feature", "fix", "improvement", "dependency"]:
            for text in item.get(category, []):
                label = values[f"changelog_label_{category}"]
                lines.append(f"* `{label}` {text}")
        chunks.append("\n".join(lines).rstrip())
    return "\n\n".join(chunks).rstrip() + "\n"


def build_language_list(target_code, languages):
    lines = []
    for code in LANGUAGE_CODES:
        content = languages[code]
        label = f"{content['$name']} [{code}]"
        if code == target_code:
            lines.append(f"- {label} # {content['text_current_lowercase']}")
        else:
            url = f"{content['repo_url']}/blob/master/.readme/README-{code}.md"
            lines.append(f"- {markdown_link(label, url)}")
    return "\n".join(lines)


def build_readme_values(code, languages, changelogs):
    content = dict(languages[code])
    content["placeholder_ul_languages_all_supported"] = build_language_list(code, languages)
    content["placeholder_highlights"] = bullet_list(content["highlights"])
    content["placeholder_usage_steps"] = numbered_list(content["usage_steps"])
    content["placeholder_gestures"] = bullet_list(content["gestures"])
    content["placeholder_faq"] = faq_block(content["faq"])
    content["placeholder_security_points"] = bullet_list(content["security_points"])
    content["placeholder_latest_release_history"] = format_changelog_items(
        changelogs[code],
        limit=3,
        heading_level=4,
    ).rstrip()
    content["placeholder_read_more_in_changelog_md"] = markdown_link(
        f"CHANGELOG-{code}.md",
        f"{content['repo_url']}/blob/master/app/src/main/assets/doc/CHANGELOG-{code}.md",
    )
    return content


CHECK_MODE = False
CHECK_DIFFERENCES = []


def write_text(path: Path, text: str):
    if CHECK_MODE:
        if not path.is_file() or path.read_bytes().replace(b"\r\n", b"\n") != text.encode("utf-8"):
            CHECK_DIFFERENCES.append(str(path.relative_to(ROOT)))
        return
    validate_symbols(text, path.relative_to(ROOT))
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="\n") as file:
        file.write(text)
    print(f"Generated {path.relative_to(ROOT)}")


def generate_readmes(languages, changelogs):
    template = load_template(README_DIR / "template_readme.md")
    for code in LANGUAGE_CODES:
        output = render_template(template, build_readme_values(code, languages, changelogs))
        path = README_DIR / f"README-{code}.md"
        write_text(path, output)
        if code == LANGUAGE_CODE_DEFAULT:
            write_text(ROOT / "README.md", output)
        if code in README_COMPAT_ROOT_LANGUAGES:
            write_text(ROOT / f"README-{code}.md", output)


def generate_changelogs(languages, changelogs):
    # CHANGELOG version headings must stay at level 1 ("# vX.Y.Z"); the in-app
    # release history screen (ReleaseHistoryActivity.formatMarkdown) only styles
    # "# ", "### ", "###### " and "* " prefixed lines.
    template = load_template(CHANGELOG_DIR / "template_changelog.md")
    for code in LANGUAGE_CODES:
        values = dict(languages[code])
        values["placeholder_release_history"] = format_changelog_items(
            changelogs[code],
            heading_level=1,
        ).rstrip()
        output = render_template(template, values)
        names = ANDROID_CHANGELOG_ALIASES.get(code, [code])
        for name in names:
            write_text(ANDROID_CHANGELOG_DIR / f"CHANGELOG-{name}.md", output)
        if code == LANGUAGE_CODE_DEFAULT:
            write_text(ANDROID_CHANGELOG_DIR / "CHANGELOG.md", output)


def main(argv=None):
    global CHECK_MODE
    parser = argparse.ArgumentParser(description="Generate or verify localized documentation")
    parser.add_argument("--check", action="store_true", help="check generated files without writing")
    CHECK_MODE = parser.parse_args(argv).check
    CHECK_DIFFERENCES.clear()
    if LANGUAGE_CODE_DEFAULT not in LANGUAGE_CODES:
        raise ValueError(f"Default language code {LANGUAGE_CODE_DEFAULT!r} is not in LANGUAGE_CODES")
    validate_source_layout()
    languages, changelogs = load_languages()
    validate_android_resources(languages)
    generate_changelogs(languages, changelogs)
    generate_readmes(languages, changelogs)

    if CHECK_DIFFERENCES:
        raise SystemExit("Generated documentation drift: " + ", ".join(CHECK_DIFFERENCES))
    if CHECK_MODE:
        print("Markdown check passed; no files written")


if __name__ == "__main__":
    main()
