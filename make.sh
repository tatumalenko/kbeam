#!/usr/bin/env bash

workspace=$(cd "$(dirname "$0")" && pwd)

build() {
  if ! ./gradlew build; then
    echo "ERROR during build"
    exit 1
  fi
}

run() {
  local mainClass=$2
  local pipelineName=$3
  local timestamp
  timestamp=$(date +%Y%m%d%H%M%S)
  local jobName="${mainClass//./-}-${timestamp}"

  echo
  echo "Running ${jobName}"

  local array=( "$@" )
  local len=${#array[@]}
  local args=${array[*]:3:$len}

  "${workspace}"/gradlew runJob -PjobClass="${mainClass}" -Poptions="
    --runner=DirectRunner
    --jobName=${jobName}
    --pipeline=${pipelineName}
    ${args}
    "
}

command="help"
if [ "$#" -ge 1 ]; then
    command=$1
fi

if [ "$#" -lt 3 ]; then
    command="help"
fi

cat << EOF
-----------------------------------------------------
 * workspace: ${workspace}
 * command: ${command}
-----------------------------------------------------
EOF

build

case ${command} in
    build|run)
        ${command} "$@"
        ;;
    *)
cat << EOF
 * Usage: ./make.sh <command> <mainClass> [<pipelineName>]
  * command:
   - build: builds the Gradle project
   - run: runs a pipeline job on local
 * example:
  - /> ./make.sh run Main WordCount
  - /> ./make.sh run Main WordCount --inputFile=./src/main/resources/input.txt --output=./output.txt
EOF
        ;;
esac
