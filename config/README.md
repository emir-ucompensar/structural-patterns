# Configuration

Document structure, dependencies, and shared formatting. Document values such
as title and authors live in `data/`.

| File | Purpose |
| --- | --- |
| `structure.tex` | Language and period defaults, period output, and localized labels |
| `packages.tex` | Central package loading and dependency setup |
| `apa7-config.tex` | Global typography, layout, headings, and captions |
| `styles.tex` | Reusable commands, environments, and APA table helpers |

`preamble.tex` loads these files in dependency order. Keep document content out
of this directory.

---

**TECHNOLOGY IS ELEGANT**  
Emir A.
