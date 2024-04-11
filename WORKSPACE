#load("@contrib_rules_jvm//:gazelle_setup.bzl", "contrib_rules_jvm_gazelle_setup")
#
#contrib_rules_jvm_gazelle_setup()
#workspace(name = "remotecache")
#
#load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
#
## Due to a bug in rules_jvm, however,
## we need to explicitly initialize gazelle to instantiate the
## @bazel_gazelle_is_bazel_module repository.
#http_archive(
#    name = "io_bazel_rules_go",
#    sha256 = "80a98277ad1311dacd837f9b16db62887702e9f1d1c4c9f796d0121a46c8e184",
#    urls = [
#        "https://mirror.bazel.build/github.com/bazelbuild/rules_go/releases/download/v0.46.0/rules_go-v0.46.0.zip",
#        "https://github.com/bazelbuild/rules_go/releases/download/v0.46.0/rules_go-v0.46.0.zip",
#    ],
#)
#
#http_archive(
#    name = "bazel_gazelle",
#    integrity = "sha256-MpOL2hbmcABjA1R5Bj2dJMYO2o15/Uc5Vj9Q0zHLMgk=",
#    urls = [
#        "https://mirror.bazel.build/github.com/bazelbuild/bazel-gazelle/releases/download/v0.35.0/bazel-gazelle-v0.35.0.tar.gz",
#        "https://github.com/bazelbuild/bazel-gazelle/releases/download/v0.35.0/bazel-gazelle-v0.35.0.tar.gz",
#    ],
#)
#
#load("@io_bazel_rules_go//go:deps.bzl", "go_register_toolchains", "go_rules_dependencies")
#load("@bazel_gazelle//:deps.bzl", "gazelle_dependencies", "go_repository")
#
#go_rules_dependencies()
#
#go_register_toolchains(version = "1.20.5")
#
#gazelle_dependencies()
#
## We need the remote apis for java
#http_archive(
#    name = "remote_apis",
#    sha256 = "a8eb03b399e6caf4e59647d1c0c5569673bbdea34a4420145ffcde865b3d9db5",
#    strip_prefix = "remote-apis-e95641649b5b4d3c582c89daabfaabeb8189dd77",
#    url = "https://github.com/bazelbuild/remote-apis/archive/e95641649b5b4d3c582c89daabfaabeb8189dd77.zip",
#)
#
#load("@remote_apis//:repository_rules.bzl", "switched_rules_by_language")
#
#switched_rules_by_language(
#    name = "bazel_remote_apis_imports",
#    java = True,
#)
#
## Needed for protobuf.
#http_archive(
#    name = "com_google_protobuf",
#    sha256 = "535fbf566d372ccf3a097c374b26896fa044bf4232aef9cab37bd1cc1ba4e850",
#    strip_prefix = "protobuf-3.15.0",
#    urls = ["https://github.com/protocolbuffers/protobuf/archive/v3.15.0.zip"],
#)
#
#load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")
#
#protobuf_deps()
#
#http_archive(
#    name = "com_github_grpc_grpc",
#    sha256 = "b391a327429279f6f29b9ae7e5317cd80d5e9d49cc100e6d682221af73d984a6",
#    strip_prefix = "grpc-93e8830070e9afcbaa992c75817009ee3f4b63a0",  # v1.24.3 with fixes
#    urls = ["https://github.com/grpc/grpc/archive/93e8830070e9afcbaa992c75817009ee3f4b63a0.zip"],
#)
#
#http_archive(
#    name = "googleapis",
#    sha256 = "b28c13e99001664eac5f1fb81b44d912d19fbc041e30772263251da131f6573c",
#    strip_prefix = "googleapis-bb964feba5980ed70c9fb8f84fe6e86694df65b0",
#    urls = ["https://github.com/googleapis/googleapis/archive/bb964feba5980ed70c9fb8f84fe6e86694df65b0.zip"],
#)
#
#load("@googleapis//:repository_rules.bzl", "switched_rules_by_language")
#
#switched_rules_by_language(
#    name = "com_google_googleapis_imports",
#)
#
# bazel-contrib/rules_jvm conveniently gives us the following:
# - Gazelle plugin for Java.
# - Compatible versions of protobuf and grpc_java.
# - java_junit5_test targets for easy junit5 testing.
#http_archive(
#    name = "contrib_rules_jvm",
#    sha256 = "2412e22bc1eb9d3a5eae15180f304140f1aad3f8184dbd99c845fafde0964559",
#    strip_prefix = "rules_jvm-0.24.0",
#    url = "https://github.com/bazel-contrib/rules_jvm/releases/download/v0.24.0/rules_jvm-v0.24.0.tar.gz",
#)
#
#load("@contrib_rules_jvm//:repositories.bzl", "contrib_rules_jvm_deps", "contrib_rules_jvm_gazelle_deps")
#
#contrib_rules_jvm_deps()
#
#contrib_rules_jvm_gazelle_deps()
#
#load("@contrib_rules_jvm//:setup.bzl", "contrib_rules_jvm_setup")
#
#contrib_rules_jvm_setup()
#
#load("@contrib_rules_jvm//:gazelle_setup.bzl", "contrib_rules_jvm_gazelle_setup")
#
#contrib_rules_jvm_gazelle_setup()

