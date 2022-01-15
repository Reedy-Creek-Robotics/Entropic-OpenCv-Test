package org.entropic

import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.RotatedRect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.GeneralizedHoughBallard
import org.opencv.imgproc.Imgproc

class WebCamGeneralizedHoughTest extends BaseWebCamTest {

	GeneralizedHoughBallard generalizedHough
	Mat template

	@Override
	protected void init() {
		super.init()

		def templateFile = new File(System.getProperty('user.dir'), '/src/main/resources/plus_mask.png')
		def colorTemplate = Imgcodecs.imread(templateFile.absolutePath)

		def grayTemplate = new Mat()
		Imgproc.cvtColor(colorTemplate, grayTemplate, Imgproc.COLOR_RGBA2GRAY)

		template = new Mat()
		Imgproc.threshold(grayTemplate, template, 127, 255, Imgproc.THRESH_BINARY)

		Imgcodecs.imwrite('template.png', template)

		generalizedHough = Imgproc.createGeneralizedHoughBallard()
		generalizedHough.template = template
	}

	@Override
	protected Mat processFrame(Mat image) {
		def gray = new Mat()
		Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGBA2GRAY)

		def edges = new Mat()
		Imgproc.Canny(gray, edges, 60, 60 * 3, 3, false)

		def cannyColor = new Mat()
		Imgproc.cvtColor(edges, cannyColor, Imgproc.COLOR_GRAY2BGR)

		def positions = new Mat()
		generalizedHough.detect(edges, positions)

		println positions.rows()

		for (int i = 0; i < positions.rows(); i++) {
			def data = positions.get(i, 0)
			def center = new Point(data[0], data[1])
			def scale = data[2]
			def angle = data[3]

			println "$center, $scale, $angle"

			def rrect = new RotatedRect(center, new Size(scale * template.width(), scale * template.height()), angle)

			def points = new Point[4]
			rrect.points(points)

			Imgproc.polylines(image, [new MatOfPoint(points)], true, new Scalar(255, 0, 0), 3)
		}

		image
	}

	static void main(String[] args) {
		new WebCamGeneralizedHoughTest().start()
	}

}
