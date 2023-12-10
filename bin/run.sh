#!/bin/bash
set -e -o pipefail

echo "APP_HOME: $APP_HOME"

if [ -n "$JAVA_HOME" ]; then
  JAVA="$JAVA_HOME/bin/java"
else
  if [ "$(uname -s)" = "Darwin" ]; then
    JAVA="$APP_HOME/jdk/Contents/Home/bin/java"
  else
    JAVA="$APP_HOME/jdk/bin/java"
  fi
fi

if [ ! -x "$JAVA" ]; then
  echo "could not find java in JAVA_HOME or bundled at $JAVA" >&2
  exit 1
fi

LIB_HOME="$APP_HOME/BOOT-INF/lib/*"
APP_CONF="$APP_HOME/config/application.properties"
JVM_OPTIONS="-Xms64m -Xmx64m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:10009"

cd "$APP_HOME"

# Print env variables
echo "Current environment:"
echo "JAVA_HOME: $JAVA"
echo "APP_HOME: $APP_HOME"
echo "LIB_HOME: $LIB_HOME"
echo "APP_CONF: $APP_CONF"

# Parse options

DAEMONIZE=false
for option in "$@"; do
  case "$option" in
  -d | --daemonize)
    DAEMONIZE=true
    ;;
  esac
done

MAIN_CLASS="org.springframework.boot.loader.JarLauncher"

# Run
if [ "$DAEMONIZE" = true ]; then
  set -x
  exec \
    "$JAVA" \
    $JVM_OPTIONS \
    -cp "$LIB_HOME" \
    "$MAIN_CLASS" \
    "$@" \
    <&- &
  set +x
else
  set -x
  exec \
    "$JAVA" \
    $JVM_OPTIONS \
    -cp "$APP_HOME:$LIB_HOME" \
    "$MAIN_CLASS" \
    "$@"
  set +x
  retval=$?
  pid=$!
  [ $retval -eq 0 ] || exit $retval
  if ! ps -p $pid >/dev/null; then
    exit 1
  fi
  exit 0
fi

exit $?
