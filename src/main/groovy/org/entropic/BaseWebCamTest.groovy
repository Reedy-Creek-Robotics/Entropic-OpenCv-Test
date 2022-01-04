package org.entropic

import java.awt.*
import javax.swing.*
import nu.pattern.OpenCV
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture

abstract class BaseWebCamTest {

	private JFrame frame
	private JLabel panel

	private VideoCapture videoCapture

	BaseWebCamTest() {
		frame = new JFrame()
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

		panel = new JLabel()
		frame.add(panel)
	}

	void start() {
		init()

		def image = new Mat()
		while (videoCapture.read(image)) {
			def processed = processFrame(image)
			showImage(processed)
		}
	}

	abstract protected Mat processFrame(Mat image)

	protected void init() {
		// Loading the OpenCV core library
		OpenCV.loadShared()

		videoCapture = new VideoCapture(0)

		// Get the first frame to find the resolution
		def image = new Mat()
		videoCapture.read(image)

		def size = new Dimension(image.width(), image.height())

		frame.setSize(size)
		panel.setSize(size)
		frame.setVisible(true)
	}

	private void showImage(Mat image) {
		panel.setIcon(new ImageIcon(ImageUtil.toBufferedImage(image)))
	}

}
