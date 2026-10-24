# Data

Document values such as title, authors, advisor, and output file name. Language,
period, and section labels live in `config/`.

| File | Purpose |
| --- | --- |
| `metadata.example.tex` | Versioned template; copy it to `metadata.tex` |
| `metadata.tex` | Local values for the paper, ignored by version control |

`metadata.tex` defines the cover page fields, PDF properties, authors, advisor,
and `\fileName` and `\fileAuthorName`, which set the exported PDF name. Leave
`\fileName` empty to build `build/main.pdf`.

---

**TECHNOLOGY IS ELEGANT**  
Emir A.
