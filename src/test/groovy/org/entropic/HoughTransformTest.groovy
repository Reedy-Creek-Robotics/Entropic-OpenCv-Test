package org.entropic

import nu.pattern.OpenCV
import org.junit.Test
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

class HoughTransformTest {

	@Test
	void 'Hough transform detects lines'() {
		//Loading the OpenCV core library
		OpenCV.loadShared()

		def imageFile = new File(System.getProperty('user.dir'), '/src/test/resources/mat-3.jpg')
		def image = Imgcodecs.imread(imageFile.absolutePath)

		def gray = new Mat()
		Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGBA2GRAY)

		Imgcodecs.imwrite('lines-gray.jpg', gray)

		def edges = new Mat()
		Imgproc.Canny(gray, edges, 60, 60 * 3, 3, false)

		def cannyColor = new Mat()
		Imgproc.cvtColor(edges, cannyColor, Imgproc.COLOR_GRAY2BGR)

		Imgcodecs.imwrite('lines-canny.jpg', cannyColor)

		def lines = new Mat()
		Imgproc.HoughLines(edges, lines, 1, Math.PI / 180, 125)

		for (int i = 0; i < lines.rows(); i++) {
			double[] data = lines.get(i, 0)
			double rho = data[0]
			double theta = data[1]
			double a = Math.cos(theta)
			double b = Math.sin(theta)
			double x0 = a * rho
			double y0 = b * rho

			//Drawing lines on the image
			Point pt1 = new Point()
			Point pt2 = new Point()
			pt1.x = Math.round(x0 + 1000 * (-b))
			pt1.y = Math.round(y0 + 1000 * (a))
			pt2.x = Math.round(x0 - 1000 * (-b))
			pt2.y = Math.round(y0 - 1000 * (a))
			Imgproc.line(cannyColor, pt1, pt2, new Scalar(0, 0, 255), 3)
		}

		Imgcodecs.imwrite('lines.jpg', cannyColor)
	}

}
