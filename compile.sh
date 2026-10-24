#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
#
# compile.sh - Build the LaTeX paper template.
#
# Drives either a manual pdflatex pipeline (with optional BibTeX) or latexmk.
# Generated files go under build/, every step prints an explicit status line,
# and the exit code is non-zero whenever the final PDF cannot be trusted.
#
# The PDF is named YYYY-MM-DD-<fileAuthorName>-<fileName>.pdf from the metadata
# file, or <PROJECT_NAME>.pdf when \fileName is empty. After a successful build,
# build/ keeps only logs/ and that PDF.
#
# Usage:
#   ./compile.sh [pdflatex|latexmk] [--language english|spanish]
#                [--period year|semester]
#
# Setting resolution (highest priority first):
#   1. Command line flags            (--language, --period)
#   2. Values in config/structure.tex (\documentLanguageDefault,
#                                      \academicPeriodDefault)
#   3. Built-in defaults             (english, year)
#
# Environment:
#   NO_COLOR=1           Disable ANSI colors.
#   VERBOSE=1            Echo command output to the terminal while logging it.
#   PROJECT_NAME=name    Build name.tex instead of main.tex.
#   BUILD_DIR=dir        Write generated files to dir instead of build/.
#   STRUCTURE_FILE=path  Structure file with language and period defaults.
#                        Default: config/structure.tex when it exists.
#   METADATA_FILE=path   Metadata file with the exported PDF name.
#                        Default: data/metadata.tex when it exists.
#
# Exit codes:
#   0  Build completed and the PDF exists.
#   1  LaTeX, BibTeX, latexmk, or validation failed.
#   2  Invalid user input.
#   3  Required external command is missing.

set -uo pipefail

#
# Constants
#

readonly EXIT_OK=0
readonly EXIT_BUILD=1
readonly EXIT_USAGE=2
readonly EXIT_MISSING=3

readonly DEFAULT_METHOD="pdflatex"
readonly DEFAULT_LANGUAGE="english"
readonly DEFAULT_PERIOD="year"

readonly SUPPORTED_LANGUAGES="english spanish"
readonly SUPPORTED_PERIODS="year semester"

readonly STRUCTURE_LANGUAGE_KEY="documentLanguageDefault"
readonly STRUCTURE_PERIOD_KEY="academicPeriodDefault"
readonly METADATA_FILE_NAME_KEY="fileName"
readonly METADATA_FILE_AUTHOR_KEY="fileAuthorName"
readonly DEFAULT_STRUCTURE_FILE="config/structure.tex"
readonly DEFAULT_METADATA_FILE="data/metadata.tex"

readonly SEPARATOR="------------------------------------------------------------"

#
# Mutable state
#

PROJECT_NAME="${PROJECT_NAME:-main}"
BUILD_DIR="${BUILD_DIR:-build}"
STRUCTURE_FILE="${STRUCTURE_FILE:-}"
METADATA_FILE="${METADATA_FILE:-}"
LOG_DIR=""
RUN_LOG=""

METHOD="$DEFAULT_METHOD"
FLAG_LANGUAGE=""
FLAG_PERIOD=""

LANGUAGE=""
LANGUAGE_SOURCE=""
PERIOD=""
PERIOD_SOURCE=""

TEX_PRETEX=""
TEX_INPUT_COMMAND=""
OUTPUT_BASE=""
OUTPUT_PDF=""

COLOR_OK=""
COLOR_WARN=""
COLOR_ERROR=""
COLOR_INFO=""
COLOR_RESET=""
COLOR_ERROR_ENABLED=0

# init_colors - Enable colors only when the destination supports them.
#
# NO_COLOR is respected. stdout and stderr are checked independently so that
# redirecting one of them never leaks escape codes into a log file.
init_colors() {
    [ -n "${NO_COLOR:-}" ] && return 0

    if [ -t 1 ]; then
        COLOR_OK='\033[32m'
        COLOR_WARN='\033[33m'
        COLOR_INFO='\033[34m'
        COLOR_RESET='\033[0m'
    fi
    if [ -t 2 ]; then
        COLOR_ERROR='\033[31m'
        COLOR_ERROR_ENABLED=1
    fi
}

# log_msg - Print a status line with a stable tag.
# @tag: Status tag such as INFO or ERROR.
# @color: ANSI color prefix, or an empty string.
# @message: Text printed after the tag.
log_msg() {
    local tag="$1"
    local color="$2"
    local message="$3"

    printf '%b[%s]%b %s\n' "$color" "$tag" "$COLOR_RESET" "$message"
}

info() { log_msg "INFO" "$COLOR_INFO" "$1"; }
ok()   { log_msg "OK" "$COLOR_OK" "$1"; }
warn() { log_msg "WARN" "$COLOR_WARN" "$1"; }

# error - Print an actionable error to stderr.
#
# The reset code is only emitted when stderr actually received a color prefix.
error() {
    local reset=""

    [ "$COLOR_ERROR_ENABLED" -eq 1 ] && reset="$COLOR_RESET"
    printf '%b[ERROR]%b %s\n' "$COLOR_ERROR" "$reset" "$1" >&2
}

# usage - Show command line usage.
usage() {
    cat <<EOF
Usage: ./compile.sh [pdflatex|latexmk] [options]

Methods:
  pdflatex  Run pdflatex, optional BibTeX, pdflatex, pdflatex (default).
  latexmk   Run latexmk in PDF mode.

Options:
  --language VALUE  Document language: english or spanish.
  --period VALUE    Academic period format: year or semester.
  -h, --help        Show this help message.

Priority of settings:
  1. Command line flags
  2. Values in config/structure.tex (\documentLanguageDefault, \academicPeriodDefault)
  3. Built-in defaults (english, year)

Examples:
  ./compile.sh
  ./compile.sh pdflatex --language spanish --period semester
  ./compile.sh latexmk --language english

Environment:
  NO_COLOR=1           Disable ANSI colors.
  VERBOSE=1            Echo command output while logging it.
  PROJECT_NAME=name    Build name.tex instead of main.tex.
  BUILD_DIR=dir        Write generated files to dir instead of build/.
  STRUCTURE_FILE=path  Structure file with language and period defaults.
  METADATA_FILE=path   Metadata file with the exported PDF name.

Output name:
  The PDF is named YYYY-MM-DD-fileAuthorName-fileName.pdf using \fileAuthorName
  and \fileName from the metadata file, or PROJECT_NAME.pdf when \fileName is
  empty. After a successful build, build/ keeps only logs/ and the PDF.
EOF
}

# fail - Print an error and exit with the requested code.
# @code: Process exit code.
# @message: Error message to display.
fail() {
    error "$2"
    exit "$1"
}

# is_supported - Check whether a value belongs to a space separated list.
# @value: Candidate value.
# @list: Space separated allowed values.
is_supported() {
    local value="$1"
    local list="$2"
    local item

    for item in $list; do
        [ "$item" = "$value" ] && return 0
    done
    return 1
}

# to_lower - Print the argument in lower case (portable to bash 3.2).
to_lower() {
    printf '%s' "$1" | tr '[:upper:]' '[:lower:]'
}

# require_command - Verify that an external command exists in PATH.
# @command_name: Program name to look up.
require_command() {
    local command_name="$1"

    if command -v "$command_name" >/dev/null 2>&1; then
        ok "Found required command: ${command_name}"
        return 0
    fi

    fail "$EXIT_MISSING" "Required command not found: ${command_name}"
}

#
# Argument parsing
#

# require_option_value - Ensure an option received a non-empty value.
# @option: Option name, used in the error message.
# @value: Value that followed the option (may be empty or absent).
require_option_value() {
    local option="$1"
    local value="${2:-}"

    if [ -z "$value" ]; then
        fail "$EXIT_USAGE" "Missing value for ${option}"
    fi
}

# parse_args - Read command line arguments into FLAG_* and METHOD.
#
# Only explicitly passed flags are stored. Leaving FLAG_* empty is what allows
# the metadata file and the defaults to take over later.
parse_args() {
    while [ "$#" -gt 0 ]; do
        case "$1" in
            pdflatex|latexmk)
                METHOD="$1"
                shift
                ;;
            --language)
                require_option_value "--language" "${2:-}"
                FLAG_LANGUAGE="$2"
                shift 2
                ;;
            --language=*)
                FLAG_LANGUAGE="${1#--language=}"
                require_option_value "--language" "$FLAG_LANGUAGE"
                shift
                ;;
            --period)
                require_option_value "--period" "${2:-}"
                FLAG_PERIOD="$2"
                shift 2
                ;;
            --period=*)
                FLAG_PERIOD="${1#--period=}"
                require_option_value "--period" "$FLAG_PERIOD"
                shift
                ;;
            -h|--help|help)
                usage
                exit "$EXIT_OK"
                ;;
            *)
                usage >&2
                fail "$EXIT_USAGE" "Unsupported argument: $1"
                ;;
        esac
    done
}

#
# Setting resolution (flags > metadata > defaults)
#

# find_settings_files - Locate the structure and metadata files, if any.
#
# An explicit STRUCTURE_FILE or METADATA_FILE must exist. Otherwise the default
# paths are used when present; a missing default is not an error and its values
# fall back to flags and built-in defaults.
find_settings_files() {
    if [ -n "$STRUCTURE_FILE" ]; then
        [ -f "$STRUCTURE_FILE" ] || fail "$EXIT_USAGE" "Structure file not found: ${STRUCTURE_FILE}"
    elif [ -f "$DEFAULT_STRUCTURE_FILE" ]; then
        STRUCTURE_FILE="$DEFAULT_STRUCTURE_FILE"
    fi

    if [ -n "$METADATA_FILE" ]; then
        [ -f "$METADATA_FILE" ] || fail "$EXIT_USAGE" "Metadata file not found: ${METADATA_FILE}"
    elif [ -f "$DEFAULT_METADATA_FILE" ]; then
        METADATA_FILE="$DEFAULT_METADATA_FILE"
    fi
}

# read_metadata_raw - Extract the value of an uncommented \def from a file.
# @key: Macro name without the leading backslash.
# @file: Metadata file to scan.
#
# Prints the first match as written, or nothing when the macro is absent or
# empty. Commented lines are ignored because the pattern is anchored to the
# start of the line.
read_metadata_raw() {
    local key="$1"
    local file="$2"

    sed -nE 's/^[[:space:]]*\\def\\'"$key"'[[:space:]]*\{([^}]*)\}.*/\1/p' "$file" \
        | head -n 1
}

# read_metadata_value - Same as read_metadata_raw with all whitespace removed.
# @key: Macro name without the leading backslash.
# @file: Metadata file to scan.
read_metadata_value() {
    read_metadata_raw "$1" "$2" | tr -d '[:space:]'
}

# slugify - Convert free text to a lowercase ASCII slug with dashes.
# @text: Text to convert, for example "Análisis de Circuitos".
#
# Common Latin accents are mapped explicitly, so the result does not depend on
# the locale or on iconv. Anything else that is not a letter or digit becomes a
# single dash, and leading or trailing dashes are removed, so the result is
# always safe to use in a file name.
slugify() {
    local text="$1"

    printf '%s' "$text" \
        | sed -E \
            -e 's/á|à|â|ä|ã|Á|À|Â|Ä|Ã/a/g' \
            -e 's/é|è|ê|ë|É|È|Ê|Ë/e/g' \
            -e 's/í|ì|î|ï|Í|Ì|Î|Ï/i/g' \
            -e 's/ó|ò|ô|ö|õ|Ó|Ò|Ô|Ö|Õ/o/g' \
            -e 's/ú|ù|û|ü|Ú|Ù|Û|Ü/u/g' \
            -e 's/ñ|Ñ/n/g' \
            -e 's/ç|Ç/c/g' \
        | tr '[:upper:]' '[:lower:]' \
        | sed -E 's/[^a-z0-9]+/-/g; s/^-+//; s/-+$//'
}

# resolve_setting - Pick a value using flag > structure > default priority.
# @value_var: Name of the variable that receives the resolved value.
# @source_var: Name of the variable that receives where it came from.
# @label: Human readable setting name for messages.
# @flag_value: Value from the command line, or empty.
# @structure_key: Macro name to read from the structure file.
# @default_value: Fallback when nothing else is defined.
# @allowed: Space separated list of accepted values.
resolve_setting() {
    local value_var="$1"
    local source_var="$2"
    local label="$3"
    local flag_value="$4"
    local structure_key="$5"
    local default_value="$6"
    local allowed="$7"
    local value=""
    local source=""
    local structure_value=""

    if [ -n "$flag_value" ]; then
        value="$flag_value"
        source="command line flag"
    else
        if [ -n "$STRUCTURE_FILE" ]; then
            structure_value=$(read_metadata_value "$structure_key" "$STRUCTURE_FILE")
        fi

        if [ -n "$structure_value" ]; then
            value="$structure_value"
            source="${STRUCTURE_FILE}"
        else
            value="$default_value"
            source="built-in default"
        fi
    fi

    value=$(to_lower "$value")

    if ! is_supported "$value" "$allowed"; then
        fail "$EXIT_USAGE" "Unsupported ${label} '${value}' from ${source}. Allowed: ${allowed}"
    fi

    printf -v "$value_var" '%s' "$value"
    printf -v "$source_var" '%s' "$source"
    ok "${label} resolved to '${value}' from ${source}"
}

# resolve_build_options - Validate method and resolve language and period.
resolve_build_options() {
    ok "Compilation method selected: ${METHOD}"

    find_settings_files
    if [ -n "$STRUCTURE_FILE" ]; then
        info "Reading language and period settings from: ${STRUCTURE_FILE}"
    else
        info "No structure file found; using flags and built-in defaults"
    fi

    resolve_setting LANGUAGE LANGUAGE_SOURCE "Language" \
        "$FLAG_LANGUAGE" "$STRUCTURE_LANGUAGE_KEY" "$DEFAULT_LANGUAGE" "$SUPPORTED_LANGUAGES"
    resolve_setting PERIOD PERIOD_SOURCE "Academic period" \
        "$FLAG_PERIOD" "$STRUCTURE_PERIOD_KEY" "$DEFAULT_PERIOD" "$SUPPORTED_PERIODS"
}

# validate_names - Reject values that could break the TeX command line.
#
# PROJECT_NAME is embedded in an \input{} command, so it is restricted to a
# conservative character set instead of being escaped.
validate_names() {
    if [[ ! "$PROJECT_NAME" =~ ^[A-Za-z0-9._-]+$ ]]; then
        fail "$EXIT_USAGE" "Invalid PROJECT_NAME '${PROJECT_NAME}': use letters, digits, dot, dash, underscore"
    fi
    if [[ ! "$BUILD_DIR" =~ ^[A-Za-z0-9._/-]+$ ]]; then
        fail "$EXIT_USAGE" "Invalid BUILD_DIR '${BUILD_DIR}': use letters, digits, dot, dash, underscore, slash"
    fi
}

# resolve_output_name - Choose the base name shared by every build artifact.
#
# The PDF is named YYYY-MM-DD-<fileAuthorName>-<fileName>.pdf. Both parts come
# from the metadata file and are slugified, so they are safe in file names.
# \fileAuthorName is optional and skipped when empty. Without a \fileName the
# build keeps PROJECT_NAME as its base name.
resolve_output_name() {
    local name_slug=""
    local author_slug=""
    local stem=""

    if [ -n "$METADATA_FILE" ]; then
        name_slug=$(slugify "$(read_metadata_raw "$METADATA_FILE_NAME_KEY" "$METADATA_FILE")")
        author_slug=$(slugify "$(read_metadata_raw "$METADATA_FILE_AUTHOR_KEY" "$METADATA_FILE")")
    fi

    if [ -z "$name_slug" ]; then
        OUTPUT_BASE="$PROJECT_NAME"
        info "No exported name in metadata; using ${OUTPUT_BASE}.pdf"
    else
        stem="$(date +%Y-%m-%d)"
        [ -n "$author_slug" ] && stem="${stem}-${author_slug}"
        OUTPUT_BASE="${stem}-${name_slug}"
    fi

    OUTPUT_PDF="${BUILD_DIR}/${OUTPUT_BASE}.pdf"
    ok "Output name resolved to: ${OUTPUT_BASE}.pdf"
}

# configure_tex_overrides - Build the TeX code that carries the resolved values.
#
# The resolved values are always passed to TeX, so the final document does not
# depend on TeX re-implementing the priority rules. The same pretex string is
# used by both backends so pdflatex and latexmk always produce equal output.
configure_tex_overrides() {
    TEX_PRETEX="\\def\\documentLanguageOverride{${LANGUAGE}}\\def\\academicPeriodOverride{${PERIOD}}"
    TEX_INPUT_COMMAND="${TEX_PRETEX}\\input{${PROJECT_NAME}.tex}"
    ok "Runtime metadata overrides are ready"
}

#
# Workspace
#

# prepare_workspace - Validate the source file and create build directories.
#
# Previous output is removed so a stale file can never be mistaken for the
# result of this run.
prepare_workspace() {
    info "Preparing build workspace"

    LOG_DIR="${BUILD_DIR}/logs"
    RUN_LOG="${LOG_DIR}/compile.log"

    [ -f "${PROJECT_NAME}.tex" ] || fail "$EXIT_USAGE" "Source file not found: ${PROJECT_NAME}.tex"
    ok "Source file exists: ${PROJECT_NAME}.tex"

    mkdir -p "$BUILD_DIR" "$LOG_DIR" \
        || fail "$EXIT_BUILD" "Cannot create build directories under: ${BUILD_DIR}"
    ok "Build directories are ready: ${BUILD_DIR}"

    : > "$RUN_LOG" || fail "$EXIT_BUILD" "Cannot write log file: ${RUN_LOG}"
    ok "Build log initialized: ${RUN_LOG}"

    clean_build_dir 0

    # BibTeX runs from the project root but reads files from the build dir.
    export BIBINPUTS=".:${BIBINPUTS:-}"
    export BSTINPUTS=".:${BSTINPUTS:-}"
}

# clean_build_dir - Remove generated files, keeping logs/ and optionally the PDF.
# @keep_pdf: 1 keeps ${OUTPUT_BASE}.pdf, 0 removes it too.
clean_build_dir() {
    local keep_pdf="$1"

    [ -d "$BUILD_DIR" ] || return 0

    if [ "$keep_pdf" -eq 1 ]; then
        find "$BUILD_DIR" -mindepth 1 -maxdepth 1 \
            ! -name logs ! -name "${OUTPUT_BASE}.pdf" -exec rm -rf {} +
    else
        find "$BUILD_DIR" -mindepth 1 -maxdepth 1 ! -name logs -exec rm -rf {} +
    fi
}

#
# Build steps
#

# report_step_failure - Print the most useful error line from a step log.
# @step_log: Log file produced by run_step.
report_step_failure() {
    local step_log="$1"
    local first_error=""

    if [ ! -f "$step_log" ]; then
        warn "Step log is missing: ${step_log}"
        return 0
    fi

    first_error=$(grep -m 1 -E '^! |Fatal error|Emergency stop|No file .*\.aux|I couldn.t open' "$step_log" 2>/dev/null || true)

    if [ -n "$first_error" ]; then
        error "Likely cause: ${first_error}"
    else
        warn "No concise error pattern found; inspect the step log"
    fi
}

# run_step - Execute one command, log its output, and preserve its exit code.
# @label: Human readable step name.
# @argv: Command and arguments to run.
#
# Output is always appended to RUN_LOG. VERBOSE=1 mirrors it to the terminal.
run_step() {
    local label="$1"
    shift
    local step_log="${LOG_DIR}/${label// /_}.log"
    local status=0

    info "Starting: ${label}"
    {
        printf '\n===== %s =====\n' "$label"
        printf 'Command:'
        printf ' %s' "$@"
        printf '\n'
    } >> "$RUN_LOG"

    "$@" > "$step_log" 2>&1
    status=$?

    [ "${VERBOSE:-0}" = "1" ] && cat "$step_log"
    cat "$step_log" >> "$RUN_LOG"

    if [ "$status" -eq 0 ]; then
        ok "Completed: ${label}"
        return 0
    fi

    error "Failed: ${label} (exit ${status})"
    report_step_failure "$step_log"
    info "See step log: ${step_log}"
    return "$status"
}

# run_latexmk - Build the document with latexmk.
#
# latexmk owns dependency discovery and reruns. -halt-on-error makes fatal
# document errors fail immediately instead of leaving a stale PDF behind.
run_latexmk() {
    local -a args=(
        -pdf
        -halt-on-error
        -interaction=nonstopmode
        -outdir="$BUILD_DIR"
        -jobname="$OUTPUT_BASE"
        -pretex="$TEX_PRETEX"
        -usepretex
    )

    require_command latexmk

    run_step "Build PDF with latexmk" latexmk "${args[@]}" "${PROJECT_NAME}.tex"
}

# run_pdflatex_pass - Run a single pdflatex pass with the shared options.
# @label: Human readable step name.
run_pdflatex_pass() {
    run_step "$1" \
        pdflatex -halt-on-error -interaction=nonstopmode \
        -jobname="$OUTPUT_BASE" \
        -output-directory="$BUILD_DIR" \
        "$TEX_INPUT_COMMAND"
}

# run_bibtex_if_needed - Run BibTeX only when the AUX file contains citations.
run_bibtex_if_needed() {
    local aux_file="${BUILD_DIR}/${OUTPUT_BASE}.aux"

    if grep -Fq '\citation' "$aux_file"; then
        require_command bibtex
        run_step "Resolve bibliography citations" \
            bibtex -min-crossrefs=999 "${BUILD_DIR}/${OUTPUT_BASE}"
        return $?
    fi

    info "Skipping BibTeX: no citations found"
    rm -f "${BUILD_DIR}/${OUTPUT_BASE}.bbl" "${BUILD_DIR}/${OUTPUT_BASE}.blg"
}

# run_pdflatex_pipeline - Build with explicit TeX passes.
#
# Pass one creates the AUX file, BibTeX runs when needed, and two more passes
# settle citations, references, and the table of contents.
run_pdflatex_pipeline() {
    local aux_file="${BUILD_DIR}/${OUTPUT_BASE}.aux"

    require_command pdflatex

    run_pdflatex_pass "Create LaTeX auxiliary files" || return "$EXIT_BUILD"

    if [ ! -f "$aux_file" ]; then
        error "AUX file was not generated: ${aux_file}"
        return "$EXIT_BUILD"
    fi
    ok "AUX file generated: ${aux_file}"

    run_bibtex_if_needed || return "$EXIT_BUILD"
    run_pdflatex_pass "Apply bibliography and references" || return "$EXIT_BUILD"
    run_pdflatex_pass "Finalize cross references and PDF" || return "$EXIT_BUILD"
}

#
# Post-build checks and reporting
#

# report_latex_warnings - Surface common non-fatal TeX problems.
#
# LaTeX can succeed while leaving unresolved references or citations. They are
# reported as warnings because the PDF exists, but should be reviewed.
report_latex_warnings() {
    local tex_log="${BUILD_DIR}/${OUTPUT_BASE}.log"
    local pattern='LaTeX Warning:|Package .* Warning:|Citation .* undefined|Reference .* undefined|Rerun to get'

    if [ ! -f "$tex_log" ]; then
        warn "LaTeX log not found: ${tex_log}"
        return 0
    fi

    if grep -Eq "$pattern" "$tex_log"; then
        warn "LaTeX warnings were found in: ${tex_log}"
        grep -E "$pattern" "$tex_log" >> "$RUN_LOG" || true
    else
        ok "No common LaTeX warnings found"
    fi
}

# validate_pdf - Ensure the expected PDF exists and is not empty.
validate_pdf() {
    if [ -s "$OUTPUT_PDF" ]; then
        ok "PDF generated: ${OUTPUT_PDF}"
        return 0
    fi

    error "PDF was not generated or is empty: ${OUTPUT_PDF}"
    return "$EXIT_BUILD"
}


# print_header - Show a banner before compilation begins.
print_header() {
    printf '%s\n  Paper Template - Build System\n%s\n\n' "$SEPARATOR" "$SEPARATOR"
}

# print_summary - Show effective settings, their origin, and the result.
# @status: Final process status.
print_summary() {
    local status="$1"

    printf '\n%s\n  Build summary\n%s\n' "$SEPARATOR" "$SEPARATOR"
    printf '  Project:          %s.tex\n' "$PROJECT_NAME"
    printf '  Method:           %s\n' "$METHOD"
    printf '  Language:         %s (%s)\n' "$LANGUAGE" "$LANGUAGE_SOURCE"
    printf '  Academic period:  %s (%s)\n' "$PERIOD" "$PERIOD_SOURCE"
    printf '  Build directory:  %s\n' "$BUILD_DIR"
    printf '  Full log:         %s\n' "$RUN_LOG"
    [ "$status" -eq 0 ] && printf '  PDF:              %s\n' "$OUTPUT_PDF"
    printf '%s\n' "$SEPARATOR"

    if [ "$status" -eq 0 ]; then
        ok "Compilation completed successfully"
    else
        error "Compilation failed"
    fi
    printf '\n'
}

# run_build - Dispatch to the selected backend.
run_build() {
    case "$METHOD" in
        latexmk)  run_latexmk ;;
        pdflatex) run_pdflatex_pipeline ;;
    esac
}

# main - Orchestrate resolution, compilation, and final reporting.
main() {
    local status=0

    init_colors
    parse_args "$@"
    print_header
    validate_names
    resolve_build_options
    resolve_output_name
    configure_tex_overrides
    prepare_workspace

    run_build || status=$?

    if [ "$status" -eq 0 ]; then
        report_latex_warnings
        validate_pdf || status=$?
    fi

    if [ "$status" -eq 0 ]; then
        clean_build_dir 1
    fi

    print_summary "$status"
    exit "$status"
}

main "$@"
