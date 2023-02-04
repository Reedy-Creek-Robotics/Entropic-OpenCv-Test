package org.entropic

import java.awt.*
import javax.swing.*
import nu.pattern.OpenCV
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.videoio.VideoCapture

abstract class BaseWebCamTest {

	boolean mirror = true

	private JFrame frame
	private JLabel panel

	private VideoCapture videoCapture

	private int frameCount
	private long frameCountSecond

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

			if (mirror) {
				def mirror = new Mat()
				Core.flip(image, mirror, 1)
				image = mirror
			}

			def processed = processFrame(image)
			showImage(processed)

			updateFps()
		}

	}

	abstract protected Mat processFrame(Mat image)

	protected void init() {
		// Loading the OpenCV core library
		OpenCV.loadLocally()

		videoCapture = new VideoCapture(0)

		// Get the first frame to find the resolution
		def image = new Mat()
		videoCapture.read(image)

		def size = new Dimension(image.width(), image.height())
		frame.setSize(size)
		panel.setSize(size)

		frame.setVisible(true)
	}

	private void updateFps() {
		frameCount++

		def second = System.currentTimeMillis() / 1000 as long
		if (frameCountSecond != second) {
			frameCountSecond = second
			frame.setTitle("$this  ($frameCount fps)")
			frameCount = 0
		}
	}

	private void showImage(Mat image) {
		panel.setIcon(new ImageIcon(ImageUtil.toBufferedImage(image)))
	}

	@Override
	String toString() {
		getClass().simpleName
	}

}
