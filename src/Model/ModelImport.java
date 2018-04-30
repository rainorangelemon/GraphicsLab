package Model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class ModelImport extends ModelShape{
	private File file;
	private int width, height;
	
	public ModelImport(int width, int height, File file) {
		super(Color.WHITE);
		this.width = width;
		this.height = height;
		setFile(file);
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		result.addAll(drawPics());
		return result;
	}
	

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	//algorithm to choose the file
	private List<ModelDot> drawPics(){
        try {
        	List<ModelDot> result = new ArrayList<ModelDot>();
            BufferedImage bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            // resize the image
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(false);
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
            image = imageView.snapshot(null, null);
            // convert image to Dots
            PixelReader pixelReader = image.getPixelReader();
            int imageWidth = (int)image.getWidth();
            int imageHeight = (int)image.getHeight();
            for (int y=0; y<height&&y<imageHeight; y++){
            	for (int x=0; x<width&&x<imageWidth; x++){
            		Color color = pixelReader.getColor(x, y);
            		result.add(new ModelDot(x, y, color));
                }
            }
            return result;
        } catch (IOException | IllegalArgumentException ex) {
           return new ArrayList<ModelDot>();
        }
	}
}
