package image_transformation;

import org.junit.jupiter.api.Test;

import image_transformation.ImageTransformation;

import image_transformation.PipelineImageProcessing;

import java.io.IOException;


public class ImageTransformationTest {
  
//  @Test
//  public void testImageTransformation() throws IOException {
//    ImageTransformation.main(new String[]{"1"});
//  }

  @Test
  public void testPipelineImageProcessing() throws IOException {
    PipelineImageProcessing.runPipelineProcessing();
  }


}
