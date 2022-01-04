package org.entropic

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.WritableRaster
import org.opencv.core.Mat

class ImageUtil {

	static BufferedImage toBufferedImage(Mat mat) {

		int type;
		if (mat.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else if (mat.channels() == 3) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		} else {
			return null;
		}

		BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
		WritableRaster raster = image.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		mat.get(0, 0, data);

		return image;
	}

}
