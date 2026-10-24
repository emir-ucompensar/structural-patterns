# APA7 Paper Template

A minimal, modular LaTeX template for APA 7 research papers.

`main.tex` assembles metadata, content, references, and APA formatting into a
single PDF.

## Layout

| Directory | Contents |
| --- | --- |
| `config/` | Structure settings, packages, APA rules, and shared styles |
| `data/` | Document metadata: tracked example and private active copy |
| `sections/` | Paper sections and structural document fragments |
| `figures/` | Images, diagrams, graphs, and plots |
| `scripts/` | Research and data-processing utilities |
| `.agents/skills/` | Project workflows for Agents |
| `build/` | Generated PDF, logs, and LaTeX artifacts |

## Build

```sh
cp data/metadata.example.tex data/metadata.tex
./compile.sh --help
```

Review the available build methods and options before compiling. The generated
PDF is written to `build/` as `YYYY-MM-DD-<fileAuthorName>-<fileName>.pdf`, or
`build/main.pdf` when `\fileName` is empty. After a successful build, `build/`
keeps only `logs/` and the PDF.

Requires Bash and a TeX distribution.  
Licensed under MIT.

---

**TECHNOLOGY IS ELEGANT**  
Emir A.
