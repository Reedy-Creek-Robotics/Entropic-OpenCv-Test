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

		// Convert from RGB to HSV so the detection is more tolerant of changes in lighting
		Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2HSV)

		// This is the measured color of the pole, in HSV space
		def color = new Scalar(21, 201, 138)
		def lowerBound = new Scalar(10,0,0)
		def upperBound = new Scalar(30,255,255)

		// Threshold the image so that we have a mask of only the pixels that are close to the pole color
		def threshold = new Mat()
		Core.inRange(image, lowerBound, upperBound, threshold)
		Imgcodecs.imwrite('pole-detection-threshold.jpg', threshold)

		// Use morphology "close" and "open" to remove small batches of noisy pixels and leave only larger regions.
		def morphed = new Mat()
		def kernel = Mat.ones(7, 7, CvType.CV_8U)
		Imgproc.morphologyEx(threshold, morphed, Imgproc.MORPH_CLOSE, kernel)
		Imgproc.morphologyEx(morphed, morphed, Imgproc.MORPH_OPEN, kernel)

		Imgcodecs.imwrite('pole-detection-morphed.jpg', morphed)

		// Find the countours on the image, which are the connected regions of white pixels.
		List<MatOfPoint> contours = []
		Mat hierarchy = new Mat()
		Imgproc.findContours(morphed, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)

		def contoured = new Mat()
		image.copyTo(contoured)

		// Draw the contours to the screen.
		def red = new Scalar(0, 0, 255)
		def green = new Scalar(0, 255, 0)
		for (int i = 0; i < contours.size(); i++) {
			Imgproc.drawContours(contoured, contours, i, red, 2, Imgproc.LINE_8)
		}

		// todo: Here you would want to sort the contours by their area and select only the largest one
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
