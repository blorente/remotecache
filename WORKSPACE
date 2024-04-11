workspace(name = "remotecache")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

load("@contrib_rules_jvm//:repositories.bzl", "contrib_rules_jvm_deps", "contrib_rules_jvm_gazelle_deps")

contrib_rules_jvm_deps()
contrib_rules_jvm_gazelle_deps()

load("@contrib_rules_jvm//:setup.bzl", "contrib_rules_jvm_setup")

contrib_rules_jvm_setup()

load("@contrib_rules_jvm//:gazelle_setup.bzl", "contrib_rules_jvm_gazelle_setup")

contrib_rules_jvm_gazelle_setup()

load("@bazel_skylib_gazelle_plugin//:workspace.bzl", "bazel_skylib_gazelle_plugin_workspace")

bazel_skylib_gazelle_plugin_workspace()

load("@bazel_skylib_gazelle_plugin//:setup.bzl", "bazel_skylib_gazelle_plugin_setup")

bazel_skylib_gazelle_plugin_setup()

# Set up remote apis, not available in the BCR

http_archive(
    name = "remote_apis",
    sha256 = "3d4ac827a3bcc6727f19754746f2dd52eb4936d72fc165d712bdaacc54630a66",
    strip_prefix = "remote-apis-2.2.0",
    url = "https://github.com/bazelbuild/remote-apis/archive/v2.2.0.zip",
)

load("@remote_apis//:repository_rules.bzl", "switched_rules_by_language")
switched_rules_by_language(
    name = "bazel_remote_apis_imports",
    java = True,
)

http_archive(
    name = "googleapis",
    sha256 = "b28c13e99001664eac5f1fb81b44d912d19fbc041e30772263251da131f6573c",
    strip_prefix = "googleapis-bb964feba5980ed70c9fb8f84fe6e86694df65b0",
    urls = ["https://github.com/googleapis/googleapis/archive/bb964feba5980ed70c9fb8f84fe6e86694df65b0.zip"],
)

load("@googleapis//:repository_rules.bzl", "switched_rules_by_language")

switched_rules_by_language(
    name = "com_google_googleapis_imports",
    java = True,
)
