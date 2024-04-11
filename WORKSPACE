workspace(name = "buildcache")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "rules_jvm_external",
    sha256 = "d31e369b854322ca5098ea12c69d7175ded971435e55c18dd9dd5f29cc5249ac",
    strip_prefix = "rules_jvm_external-5.3",
    url = "https://github.com/bazelbuild/rules_jvm_external/releases/download/5.3/rules_jvm_external-5.3.tar.gz",
)

#=== Gazelle setup
# We need this because there is a bug in bazel_contrib/rules_jvm that doesn't initialize gazelle correclty.
# If we try to initialize java-gazelle before bazel-gazelle, it will complain about the repository
# `@@bazel_gazelle_is_bazel_module` being missing.

http_archive(
    name = "io_bazel_rules_go",
    sha256 = "80a98277ad1311dacd837f9b16db62887702e9f1d1c4c9f796d0121a46c8e184",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/rules_go/releases/download/v0.46.0/rules_go-v0.46.0.zip",
        "https://github.com/bazelbuild/rules_go/releases/download/v0.46.0/rules_go-v0.46.0.zip",
    ],
)

http_archive(
    name = "bazel_gazelle",
    integrity = "sha256-MpOL2hbmcABjA1R5Bj2dJMYO2o15/Uc5Vj9Q0zHLMgk=",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/bazel-gazelle/releases/download/v0.35.0/bazel-gazelle-v0.35.0.tar.gz",
        "https://github.com/bazelbuild/bazel-gazelle/releases/download/v0.35.0/bazel-gazelle-v0.35.0.tar.gz",
    ],
)


load("@io_bazel_rules_go//go:deps.bzl", "go_register_toolchains", "go_rules_dependencies")
load("@bazel_gazelle//:deps.bzl", "gazelle_dependencies", "go_repository")

go_rules_dependencies()

go_register_toolchains(version = "1.20.5")

gazelle_dependencies()

#=== Contrib rules jvm (for linting and gazelle for Java)

http_archive(
    name = "contrib_rules_jvm",
    sha256 = "2412e22bc1eb9d3a5eae15180f304140f1aad3f8184dbd99c845fafde0964559",
    strip_prefix = "rules_jvm-0.24.0",
    url = "https://github.com/bazel-contrib/rules_jvm/releases/download/v0.24.0/rules_jvm-v0.24.0.tar.gz",
)

load("@contrib_rules_jvm//:repositories.bzl", "contrib_rules_jvm_deps", "contrib_rules_jvm_gazelle_deps")

contrib_rules_jvm_deps()

contrib_rules_jvm_gazelle_deps()

#=== Linting

load("@apple_rules_lint//lint:repositories.bzl", "lint_deps")

lint_deps()

load("@apple_rules_lint//lint:setup.bzl", "lint_setup")

lint_setup({
    "java-checkstyle": "@contrib_rules_jvm//java:checkstyle-default-config",
    "java-pmd": "@contrib_rules_jvm//java:pmd-config",
    "java-spotbugs": "@contrib_rules_jvm//java:spotbugs-default-config",
})

#=== Setup bazel-contrib/rules_jvm

load("@contrib_rules_jvm//:setup.bzl", "contrib_rules_jvm_setup")

contrib_rules_jvm_setup()

load("@contrib_rules_jvm//:gazelle_setup.bzl", "contrib_rules_jvm_gazelle_setup")

contrib_rules_jvm_gazelle_setup()

# #=== GRPC setup
#
# http_archive(
#     name = "io_grpc_grpc_java",
#     sha256 = "cc4853d9ff7fbd866a2175f48ff02e9615d02907eb946892954ea25ef62aeca3",
#     strip_prefix = "grpc-java-1.63.0",
#     url = "https://github.com/grpc/grpc-java/archive/v1.63.0.zip",
# )
#
# load("@rules_jvm_external//:defs.bzl", "maven_install")
# load("@io_grpc_grpc_java//:repositories.bzl", "IO_GRPC_GRPC_JAVA_ARTIFACTS")
# load("@io_grpc_grpc_java//:repositories.bzl", "IO_GRPC_GRPC_JAVA_OVERRIDE_TARGETS")
# load("@io_grpc_grpc_java//:repositories.bzl", "grpc_java_repositories")
#
# grpc_java_repositories()
#
# # Protobuf now requires C++14 or higher, which requires Bazel configuration
# # outside the WORKSPACE. See .bazelrc in this directory.
# load("@com_google_protobuf//:protobuf_deps.bzl", "PROTOBUF_MAVEN_ARTIFACTS")
# load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")
#
# protobuf_deps()
#
# maven_install(
#     artifacts = [
#         "com.google.api.grpc:grpc-google-cloud-pubsub-v1:0.1.24",
#         "com.google.api.grpc:proto-google-cloud-pubsub-v1:0.1.24",
#     ] + IO_GRPC_GRPC_JAVA_ARTIFACTS + PROTOBUF_MAVEN_ARTIFACTS,
#     generate_compat_repositories = True,
#     override_targets = IO_GRPC_GRPC_JAVA_OVERRIDE_TARGETS,
#     repositories = [
#         "https://repo.maven.apache.org/maven2/",
#     ],
# )
#
# load("@maven//:compat.bzl", "compat_repositories")
#
# compat_repositories()

#=== Remote Build API protos

http_archive(
    name = "remote_apis",
    sha256 = "3d4ac827a3bcc6727f19754746f2dd52eb4936d72fc165d712bdaacc54630a66",
    strip_prefix = "remote-apis-2.2.0",
    url = "https://github.com/bazelbuild/remote-apis/archive/v2.2.0.zip",
)

