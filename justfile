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

clean:
    @echo "Cleaning project"
    @./gradlew clean

clean-build: clean
    @echo "Cleaning build"
    @./gradlew build --refresh-dependencies

build:
    @echo "Building project"
    @./gradlew build

run:
    @echo "Running project"
    @./gradlew run

worker:
    @echo "Running worker"
    @./gradlew worker

