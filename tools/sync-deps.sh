#!/bin/bash
set -euo pipefail

REPIN=1 bazel run @unpinned_maven//:pin
