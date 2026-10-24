# Sections

Document content assembled by `main.tex`. The existing sections are defaults,
not a required academic structure.

Use descriptive lowercase kebab-case filenames, such as
`literature-review.tex`. Keep top-level headings in `main.tex`, not in the
section file.

To add a localized section:

1. Create `sections/<section-name>.tex`.
2. Define the matching `\sectionTitle<UpperCamelCase>` in both language branches
   of `config/structure.tex`.
3. Add the heading and input to `main.tex`:

```latex
\section{\sectionTitleLiteratureReview}
\input{sections/literature-review}
```

Keep the filename, metadata command, and `main.tex` reference in exact parity,
then review the compiler options with `./compile.sh --help`.

---

**TECHNOLOGY IS ELEGANT**  
Emir A.
