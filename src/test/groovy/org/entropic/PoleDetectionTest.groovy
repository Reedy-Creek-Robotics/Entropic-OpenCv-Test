package org.entropic

import nu.pattern.OpenCV
import org.junit.Test
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

class PoleDetectionTest {

	@Test
	void 'Detect Pole'() {
		//Loading the OpenCV core library
		OpenCV.loadLocally()

		//def imageFile = new File(System.getProperty('user.dir'), '/src/test/resources/qr-code-on-phone.jpg')
		//def imageFile = new File(System.getProperty('user.dir'), '/src/test/resources/pole-at-1.125-in.bmp')
		def imageFile = new File(System.getProperty('user.dir'), '/src/test/resources/pole-at-4.25-in.bmp')
		def image = Imgcodecs.imread(imageFile.absolutePath)

		def color = new Scalar(0x00,0x50,0x7D)
		def lowerBound = new Scalar(0x60,0x30,0x00)
		def upperBound = new Scalar(0xFF,0x80,0x30)

		def threshold = new Mat()

		Core.inRange(image, lowerBound, upperBound, threshold)
		Imgcodecs.imwrite('pole-detection-threshold.jpg', threshold)

		def morphed = new Mat()
		def temp = new Mat()
		def kernel = Mat.ones(7, 7, CvType.CV_8U)
		Imgproc.morphologyEx(threshold, temp, Imgproc.MORPH_CLOSE, kernel)
		Imgproc.morphologyEx(temp, morphed, Imgproc.MORPH_OPEN, kernel)

		Imgcodecs.imwrite('pole-detection-morphed.jpg', morphed)

		List<MatOfPoint> contours = []
		Mat hierarchy = new Mat()
		Imgproc.findContours(morphed, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

		def contoured = new Mat()
		image.copyTo(contoured)

		def red = new Scalar(0, 0, 255)
		def green = new Scalar(0, 255, 0)
		for (int i = 0; i < contours.size(); i++) {
			Imgproc.drawContours(contoured, contours, i, red, 2, Imgproc.LINE_8)
		}

		println "Found ${contours.size()} contours"
		assert !contours.empty
		def contour = contours[0]

		Imgcodecs.imwrite('pole-detection-contoured.jpg', contoured)

		def bounded = new Mat()
		image.copyTo(bounded)

		def boundingRect = Imgproc.boundingRect(contour)
		Imgproc.rectangle(bounded, boundingRect, green, 2)

		def area = Imgproc.contourArea(contour)
		def width = boundingRect.width
		def height = boundingRect.height

		int textStart = 30
		int textHeight = 25
		def fontScale = 1.2
		def font = Imgproc.FONT_HERSHEY_PLAIN

		Imgproc.putText(bounded, "Area ${area.round(1)}", new Point(10, textStart), font, fontScale, green, 1)
		Imgproc.putText(bounded, "Width $width", new Point(10, textStart + textHeight), font, fontScale, green, 1)
		Imgproc.putText(bounded, "Height $height", new Point(10, textStart + 2 * textHeight), font, fontScale, green, 1)
		Imgproc.putText(bounded, "Fill ${(area / (width * height) * 100).round(1)}%", new Point(10, textStart + 3 * textHeight), font, fontScale, green, 1)

		Imgcodecs.imwrite('pole-detection-bounded.jpg', bounded)
	}

}
