help:
    @echo "Usage: just <command>"
    @echo ""
    @echo "Commands:"
    @echo "  help:         Show this help message"
    @echo "  format:       Format code"
    @echo "  check:        Check code"

format:
    @echo "Formatting code"
    @./gradlew ktfmtFormat

test: format
    @echo "Running tests"
    @./gradlew clean test

check: format
    @echo "Checking for justfile"
    @./gradlew clean check
