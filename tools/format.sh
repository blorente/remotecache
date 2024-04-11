#!/bin/bash
set -euo pipefail

bazel run @gazelle
bazel run //:buildifier.fix
