package org.entropic

import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.QRCodeDetector

class WebCamQRCodeTest extends BaseWebCamTest {

	QRCodeDetector detector

	@Override
	protected void init() {
		super.init()

		detector = new QRCodeDetector()
	}

	@Override
	protected Mat processFrame(Mat image) {
		def result = new Mat()

		detector.detect(image, result)

		if (result.rows()) {

			def points = (0..<result.cols()).collect { new Point(result.get(0, it)) }

			for (int i = 0; i < points.size(); i++) {
				//Drawing lines on the image
				Point pt1 = points[i]
				Point pt2 = points[(i + 1) % points.size()]
				Imgproc.line(image, pt1, pt2, new Scalar(255, 0, 0), 3)
			}
		}

		image
	}

	static void main(String[] args) {
		new WebCamQRCodeTest().start()
	}

}
