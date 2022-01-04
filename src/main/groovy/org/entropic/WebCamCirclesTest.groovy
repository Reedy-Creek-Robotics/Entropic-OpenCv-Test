package org.entropic

import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class WebCamCirclesTest extends BaseWebCamTest {

	@Override
	protected Mat processFrame(Mat image) {

		def gray = new Mat()
		Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGBA2GRAY)

		def edges = new Mat()
		Imgproc.Canny(gray, edges, 60, 60 * 3, 3, false)

		def cannyColor = new Mat()
		Imgproc.cvtColor(edges, cannyColor, Imgproc.COLOR_GRAY2BGR)

		def circles = new Mat()
		Imgproc.HoughCircles(edges, circles, Imgproc.HOUGH_GRADIENT, 1.5, 1)

		for (int i = 0; i < circles.rows(); i++) {

			double[] data = circles.get(i, 0)
			def center = new Point(data[0], data[1])
			def radius = data[2]

			// Drawing circles
			Imgproc.circle(image, center, (int) radius, new Scalar(0, 0, 255), 3)
		}

		image
	}

	static void main(String[] args) {
		new WebCamCirclesTest().start()
	}

}
