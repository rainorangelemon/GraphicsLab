package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;

import model.operation.OperationClip;
import model.operation.OperationRotation;
import model.operation.OperationScaling;
import model.operation.OperationTranslation;

public class ModelImport3D extends ModelShape{
	private File file;
	private int width, height;
	
	public ModelImport3D(int width, int height, File file) {
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

	@Override
	public ModelShape translation(int offsetX, int offsetY) {
		ModelDots result = new ModelDots(this.drawPics());
		OperationTranslation.dotsTranslation(offsetX, offsetY, result.getDots());
		return result;
	}

	@Override
	public ModelShape rotation(int rotationX, int rotationY, int rotationDegree) {
		ModelDots result = new ModelDots(this.drawPics());
		OperationRotation.dotsRotation(rotationX, rotationY, rotationDegree, result.getDots());
		return result;
	}

	@Override
	public ModelShape scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		ModelDots result = new ModelDots(this.drawPics());
		OperationScaling.dotsScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, result.getDots());
		return result;
	}

	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
		List<ModelDot> dots = this.drawPics();
		List<ModelDot> newDots = OperationClip.dotsClip(windowX0, windowY0, windowX1, windowY1, dots);
		ModelDots result = new ModelDots(newDots);
		return result;
	}
}
