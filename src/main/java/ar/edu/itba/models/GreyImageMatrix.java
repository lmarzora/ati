package ar.edu.itba.models;

import ar.edu.itba.constants.NoiseType;
import ar.edu.itba.models.randomGenerators.RandomNumberGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;

public class GreyImageMatrix extends ImageMatrix implements Iterable<GreyPixel>{
    private double[][] grey;

    public GreyImageMatrix(BufferedImage image) {
        super(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        this.grey = new double[this.width][this.height];
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int pixelLength = 1;
        for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
            this.grey[row][col] = ((int) pixels[pixel] & 0xff); // blue
            row++;
            if (row == this.width) {
                row = 0;
                col++;
            }
        }
    }

    public GreyImageMatrix(int width, int height, double[][] grey) {
        super(width, height, BufferedImage.TYPE_BYTE_GRAY);
        this.grey = grey;
    }
    @Override
    protected BufferedImage toBufferedImage(boolean compress) {
        if (compress) {
            this.dynamicRange(getMaxValue(), getMinValue());
        }
        else {
            this.applyPunctualOperation(this::truncate);
        }
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                raster.setSample(i, j, 0, grey[i][j]);
            }
        }
        return image;
    }

    @Override
    public Pixel getPixelColor(int x, int y) {
        int value = (int) this.grey[x][y];
        return new GreyPixel(x, y, value);
    }

    @Override
    public void setPixel(Pixel pixel) {
        GreyPixel greyPixel = (GreyPixel) pixel;
        int x = greyPixel.getX(); int y = greyPixel.getY();
        int val = greyPixel.getGrey();
        setPixel(x, y, val);
    }

    private void setPixel(int x, int y, double val) {
        grey[x][y] = val;
    }

    @Override
    public ImageMatrix applyPunctualOperation(ToDoubleFunction<Double> operation) {
        double val;
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                val = operation.applyAsDouble(grey[i][j]);
                setPixel(i, j, val);
            }
        }
        //this.updateMinMaxValues(operation);
        return this;
    }

    @Override
    public ImageMatrix applyBinaryOperation(BinaryOperator<Double> operator, ImageMatrix matrix) {
        double val;
        System.out.println("BINARY");
        GreyImageMatrix greyMatrix = (GreyImageMatrix) matrix;
        for (int i = 0; i < this.width && i < matrix.getWidth(); i++) {
            for (int j = 0; j < this.height && i < matrix.getHeight(); j++) {
                val = operator.apply(grey[i][j], greyMatrix.grey[i][j]);
                this.setPixel(i, j, val);
            }
        }
        this.updateMinMaxValues(operator, matrix);
        return this;
    }

    @Override
    public void applyNoise(NoiseType noiseType, RandomNumberGenerator generator, double percentage) {
        long cant = Math.round(this.width * this.height * percentage);
        DoubleStream randoms =  generator.doubles(cant);
        Iterable<Point> toModify = getPixelsToModify(this.width, this.height, cant);
        double[][] matrix = getRandomMatrix(this.width, this.height, noiseType, toModify, randoms.iterator());
        ImageMatrix noise = new GreyImageMatrix(this.width, this.height, matrix);
        this.applyBinaryOperation((x1, x2) -> noiseType.getOperator().apply(x1,  x2), noise);
    }

    public int getValue(int x, int y) {
        return (int)this.grey[x][y];
    }

    @Override
    public Iterator<GreyPixel> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super GreyPixel> action) {

    }

    @Override
    public Spliterator<GreyPixel> spliterator() {
        return null;
    }

    public static GreyImageMatrix getNoiseImage(int width, int height, RandomNumberGenerator generator, NoiseType noiseType) {
        long cant = Math.round(width * height);
        DoubleStream randoms =  generator.doubles(cant);
        Iterable<Point> toModify = getPixelsToModify(width, height, cant);
        double[][] matrix = getRandomMatrix(width, height, noiseType, toModify, randoms.iterator());
        return new GreyImageMatrix(width, height, matrix);
    }
}
