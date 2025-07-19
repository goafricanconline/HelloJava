import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class ImageProcessing {
  public static void main(String[] args) {
    int[][] imageData = imgToTwoD("./apple.jpg");

    if (imageData == null) {
      System.out.println("Image loading failed. Exiting...");
      return;
    }

    // Trim borders
    int[][] trimmed = trimBorders(imageData, 60);
    twoDToImage(trimmed, "./trimmed_apple.jpg");

    // Apply multiple filters
    int[][] filtered = negativeColor(invertImage(trimBorders(imageData, 50)));

    // Save filtered image
    twoDToImage(filtered, "./filtered_apple.jpg");

    System.out.println("Image processing completed.");
  }

  // ------------------------
  // IMAGE PROCESSING METHODS
  // ------------------------

  public static int[][] trimBorders(int[][] imageTwoD, int pixelCount) {
    if (imageTwoD.length > pixelCount * 2 && imageTwoD[0].length > pixelCount * 2) {
      int newRows = imageTwoD.length - 2 * pixelCount;
      int newCols = imageTwoD[0].length - 2 * pixelCount;
      int[][] trimmed = new int[newRows][newCols];

      for (int i = 0; i < newRows; i++) {
        for (int j = 0; j < newCols; j++) {
          trimmed[i][j] = imageTwoD[i + pixelCount][j + pixelCount];
        }
      }
      return trimmed;
    } else {
      System.out.println("Cannot trim that many pixels from the given image.");
      return imageTwoD;
    }
  }

  public static int[][] negativeColor(int[][] imageTwoD) {
    int rows = imageTwoD.length;
    int cols = imageTwoD[0].length;
    int[][] output = new int[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        int[] rgba = getRGBAFromPixel(imageTwoD[i][j]);
        int red = 255 - rgba[0];
        int green = 255 - rgba[1];
        int blue = 255 - rgba[2];
        int alpha = rgba[3];
        output[i][j] = getColorIntValFromRGBA(new int[]{red, green, blue, alpha});
      }
    }
    return output;
  }

  public static int[][] invertImage(int[][] imageTwoD) {
    int rows = imageTwoD.length;
    int cols = imageTwoD[0].length;
    int[][] inverted = new int[rows][cols];

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        inverted[i][j] = imageTwoD[rows - 1 - i][cols - 1 - j];
      }
    }
    return inverted;
  }

  // --------------------
  // IMAGE IO UTILITIES
  // --------------------

  public static int[][] imgToTwoD(String inputFileOrLink) {
    try {
      BufferedImage image;
      if (inputFileOrLink.toLowerCase().startsWith("http")) {
        URL imageUrl = new URL(inputFileOrLink);
        image = ImageIO.read(imageUrl);
      } else {
        image = ImageIO.read(new File(inputFileOrLink));
      }

      int imgRows = image.getHeight();
      int imgCols = image.getWidth();
      int[][] pixelData = new int[imgRows][imgCols];

      for (int i = 0; i < imgRows; i++) {
        for (int j = 0; j < imgCols; j++) {
          pixelData[i][j] = image.getRGB(j, i);
        }
      }

      return pixelData;
    } catch (Exception e) {
      System.out.println("Failed to load image: " + e.getMessage());
      return null;
    }
  }

  public static void twoDToImage(int[][] imgData, String fileName) {
    try {
      int imgRows = imgData.length;
      int imgCols = imgData[0].length;
      BufferedImage result = new BufferedImage(imgCols, imgRows, BufferedImage.TYPE_INT_RGB);

      for (int i = 0; i < imgRows; i++) {
        for (int j = 0; j < imgCols; j++) {
          result.setRGB(j, i, imgData[i][j]);
        }
      }

      ImageIO.write(result, "jpg", new File(fileName));
    } catch (Exception e) {
      System.out.println("Failed to save image: " + e.getMessage());
    }
  }

  public static int[] getRGBAFromPixel(int pixelColorValue) {
    Color pixelColor = new Color(pixelColorValue, true);
    return new int[]{pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue(), pixelColor.getAlpha()};
  }

  public static int getColorIntValFromRGBA(int[] colorData) {
    if (colorData.length == 4) {
      Color color = new Color(colorData[0], colorData[1], colorData[2], colorData[3]);
      return color.getRGB();
    } else {
      System.out.println("Incorrect number of elements in RGBA array.");
      return -1;
    }
  }

  // --------------------
  // DEBUGGING TOOL
  // --------------------

  public static void viewImageData(int[][] imageTwoD) {
    if (imageTwoD.length > 3 && imageTwoD[0].length > 3) {
      int[][] rawPixels = new int[3][3];
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          rawPixels[i][j] = imageTwoD[i][j];
        }
      }

      System.out.println("Raw pixel data from the top left corner:");
      System.out.println(Arrays.deepToString(rawPixels).replace("],", "],\n"));

      int[][][] rgbaPixels = new int[3][3][4];
      for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
          rgbaPixels[i][j] = getRGBAFromPixel(imageTwoD[i][j]);
        }
      }

      System.out.println("\nExtracted RGBA pixel data from top left corner:");
      for (int[][] row : rgbaPixels) {
        System.out.println(Arrays.deepToString(row));
      }
    } else {
      System.out.println("Image too small to view a 3x3 sample.");
    }
  }
}
