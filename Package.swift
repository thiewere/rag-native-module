// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "RagNativeModule",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "RagNativeModule",
            targets: ["RagPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "RagPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/RagPlugin"),
        .testTarget(
            name: "RagPluginTests",
            dependencies: ["RagPlugin"],
            path: "ios/Tests/RagPluginTests")
    ]
)