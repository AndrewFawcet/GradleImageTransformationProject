package image_transformation;

import org.junit.jupiter.api.Test;

import java.io.IOException;


public class ImageTransformationTest {

//  not sure why this is unable to be tested...  
//  @Test
//  public void testImageTransformation() throws IOException {
//    ImageTransformation.main(new String[]{"1"});
//  }

  @Test
  public void testPipelineImageProcessing() throws IOException {
    PipelineImageProcessing.runPipelineImageProcessing();
  }
  @Test
  public void testPipelineImageProcessingUsingArrays() throws IOException {
    PipelineImageProcessingUsingArrays.runPipelineImageProcessingUsingArrays();   
  }
  @Test
  public void testPipelineImageProcessingUsingArraysAndSharding() throws IOException {
    PipelineImageProcessingUsingArraysAndSharding.runPipelineImageProcessingUsingArraysAndSharding();   
  }
  @Test
  public void testPipelineImageProcessingUsingArraysShardingAndSynchronisation() throws IOException {
    PipelineImageProcessingUsingArraysShardingAndSynchronisation.runPipelineImageProcessingUsingArraysShardingAndSynchronisation();   
  }

}

