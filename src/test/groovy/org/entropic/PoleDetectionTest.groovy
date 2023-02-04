package org.entropic

import nu.pattern.OpenCV
import org.junit.Test
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.QRCodeDetector

class PoleDetectionTest {

	@Test
	void 'Detect QR code'() {
		//Loading the OpenCV core library
		OpenCV.loadLocally()

		//def imageFile = new File(System.getProperty('user.dir'), '/src/test/resources/qr-code-on-phone.jpg')
		def imageFile = new File(System.getProperty('user.dir'), '/src/test/resources/qr-code-at-angle.jpg')
		def image = Imgcodecs.imread(imageFile.absolutePath)

		def result = new Mat()

		def detector = new QRCodeDetector()
		detector.detect(image, result)

		assert result.rows() == 1

		def points = (0..<result.cols()).collect { new Point(result.get(0, it)) }

		for (int i = 0; i < points.size(); i++) {
			//Drawing lines on the image
			Point pt1 = points[i]
			Point pt2 = points[(i + 1) % points.size()]
			Imgproc.line(image, pt1, pt2, new Scalar(0, 0, 255), 3)

		}

		Imgcodecs.imwrite('qr-code.jpg', image)
	}

}
