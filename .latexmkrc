#!/usr/bin/env perl
# SPDX-License-Identifier: MIT
#
# .latexmkrc - Configure latexmk for the APA7 LaTeX paper template.
#
# This file is loaded by latexmk before a build starts. It keeps all generated
# artifacts under build/ and selects PDF output through pdflatex.
#
# Usage:
#   latexmk -pdf main.tex
#   ./compile.sh latexmk
#
# Notes:
#   This is Perl code evaluated by latexmk. Its configuration variables are
#   package globals, so strict and warnings pragmas are intentionally not used.

# configure_output_modes - Select the document formats latexmk should produce.
#
# PDF is the only expected artifact for this template. Disabling DVI and
# PostScript avoids extra files and keeps the build pipeline deterministic.
$pdf_mode = 1;
$postscript_mode = 0;
$dvi_mode = 0;

# configure_directories - Keep generated artifacts out of the project root.
#
# latexmk writes final output and auxiliary files to the same build directory so
# cleanup, inspection, and scripted validation have a single stable location.
$out_dir = 'build';
$aux_dir = 'build';

# configure_cleanup - Register generated extensions removed by latexmk -c.
#
# The list includes common LaTeX, BibTeX, and latexmk state files. Source files,
# bibliography databases, images, and user-authored content are intentionally not
# listed here.
@generated_exts = qw(
    aux
    bbl
    bcf
    blg
    brf
    fdb_latexmk
    fls
    log
    out
    run.xml
    synctex.gz
    toc
    lof
    lot
);

# configure_reruns - Bound automatic rebuild attempts.
#
# Cross references, citations, and tables of contents may require several LaTeX
# passes. A finite cap prevents broken documents from causing endless rebuilds.
$max_repeat = 5;

# configure_output_verbosity - Keep latexmk output visible.
#
# The wrapper script already owns high-level status messages. latexmk output is
# still left available so direct latexmk users can diagnose build failures.
$silent = 0;
$show_error_detail = 1;

# configure_pdflatex - Forward the pretex prelude to TeX.
#
# %P expands to the pretex string plus \input{main.tex} when latexmk runs with
# -usepretex, as compile.sh does, so --language and --period reach the document.
# Without -usepretex, %P expands to the plain source file and the defaults in
# config/structure.tex apply.
$pdflatex = 'pdflatex %O -jobname=%B %P';
