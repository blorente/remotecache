#!/bin/bash
set -euo pipefail

repo_root=$(git rev-parse --show-toplevel)
bazel build //:RemoteCache
deployable=$(bazel build //:RemoteCache 2>&1 | grep bazel-bin | tr -d " ")
outfile="${repo_root}/RemoteCache.jar"
rm -rf "${outfile}"
cp "${deployable}" "${outfile}"
echo "Copied to ${outfile}"
echo "To run it, run:"
echo "  java -jar ${outfile} -h"