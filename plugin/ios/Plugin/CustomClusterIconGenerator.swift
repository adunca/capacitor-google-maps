import GoogleMaps
import GoogleMapsUtils

class CustomClusterIconGenerator: GMUDefaultClusterIconGenerator {
    override func icon(forSize size: UInt) -> UIImage {
        guard let baseImage = UIImage(named: "pin_cluster") else {
            return super.icon(forSize: size) // fallback
        }

        // Scale the image smaller (e.g., 70% of original size)
        let scaleFactor: CGFloat = 0.42
        let newSize = CGSize(width: baseImage.size.width * scaleFactor, height: baseImage.size.height * scaleFactor)

        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
        defer { UIGraphicsEndImageContext() }

        baseImage.draw(in: CGRect(origin: .zero, size: newSize))

        let textAttributes: [NSAttributedString.Key: Any] = [
            .foregroundColor: UIColor(hex: "#4A5468") ?? UIColor.black,
            .font: UIFont.boldSystemFont(ofSize: 18), // adjust text size
            .paragraphStyle: NSParagraphStyle.default
        ]

        let text = "\(size)"
        let textSize = text.size(withAttributes: textAttributes)
        let rect = CGRect(
            x: (newSize.width - textSize.width) / 2,
            y: (newSize.height - textSize.height) / 2,
            width: textSize.width,
            height: textSize.height
        )
        text.draw(in: rect, withAttributes: textAttributes)

        return UIGraphicsGetImageFromCurrentImageContext() ?? baseImage
    }
}
