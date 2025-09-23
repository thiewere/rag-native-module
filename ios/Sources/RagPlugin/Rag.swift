import Foundation

@objc public class Rag: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
