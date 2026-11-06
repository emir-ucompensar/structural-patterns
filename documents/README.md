# Documents

One directory per delivered document. Each one is self-contained: its
`main.tex` lists only the sections that document needs, and its `sections/`
holds their content. What both documents share stays outside: `config/`
(including the cover page, references, and final signature), `data/`,
`figures/`, `main.bib`, and `preamble.tex`.

`shared/` holds content rendered in both documents, such as `source-code.tex`
(repository, releases and how to run the program), which each `main.tex`
inputs right after the introduction.

| Directory | Document | Metadata |
| --- | --- | --- |
| `design/` | Design document (first deliverable) | `data/metadata-design.tex` |
| `modifications/` | Modifications document (second deliverable) | `data/metadata-modifications.tex` |

Build both with `./compile.sh`, or one with `--document design` or
`--document modifications`. TeX runs from the project root, so every path in a
`main.tex` is relative to it.

## Add a section

1. Create `documents/<document>/sections/<section-name>.tex`, with a
   descriptive lowercase kebab-case name and the header
   `% SPDX-License-Identifier: CC-BY-4.0`, since it holds research content.
2. Define the matching `\sectionTitle<UpperCamelCase>` in both language
   branches of `config/structure.tex`, unless it already exists.
3. Add the heading and input to that document's `main.tex`:

```latex
\section{\sectionTitleLiteratureReview}
\input{\documentDirectory/sections/literature-review}
```

Keep top-level headings in `main.tex`, not in the section file.

## Add a document

Create `documents/<document>/` with its `main.tex` (defining
`\documentVariant` and `\documentDirectory`) and `sections/`, add its metadata
file to `data/` and `.gitignore`, and add the name to `SUPPORTED_DOCUMENTS` in
`compile.sh`.

---

**TECHNOLOGY IS ELEGANT**  
Emir A.
