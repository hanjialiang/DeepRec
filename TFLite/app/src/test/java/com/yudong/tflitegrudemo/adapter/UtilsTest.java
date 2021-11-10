//package com.yudong.tflitegrudemo.adapter;
//
//import static com.yudong.tflitegrudemo.adapter.utils.Utils.read2DArray;
//
//import junit.framework.TestCase;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
//public class UtilsTest extends TestCase {
//
//    public void testRead2DArray() throws IOException {
//        File file = new File("/Users/maghsk/AndroidStudioProjects/TFLiteGRUDemo/app/src/main/assets/feats_bs512.txt");
//        InputStream is = new FileInputStream(file);
//        int[][] feats = read2DArray(is, DataLoader.batchNum, DataLoader.batchSize);
//        int[] answer = new int[]{148,6,150,586,1254,1244,1416};
//        for (int i = 0; i < answer.length; i++) {
//            assertEquals(answer[i], feats[3][i]);
//        }
//    }
//}