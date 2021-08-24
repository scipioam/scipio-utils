import com.github.ScipioAM.scipio_utils_net.http.ApacheHttpRequester;
import com.github.ScipioAM.scipio_utils_net.http.bean.ResponseResult;
import com.github.ScipioAM.scipio_utils_net.http.common.ResponseDataMode;
import com.github.ScipioAM.scipio_utils_net.http.listener.DownloadListener;
import com.github.ScipioAM.scipio_utils_net.http.listener.FileUploadListener;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

/**
 * @since 2021/6/10
 */
public class RequesterTest {

    @Test
    public void testHttpClient() {
        String url0 = "https://example.com/";

        ApacheHttpRequester requester = new ApacheHttpRequester()
                .setDefaultUserAgent();
        ResponseResult result = requester.get(url0);
        System.out.println("Response code: " + result.getResponseCode());
        System.out.println("Error message: " + result.getErrorMsg());
        System.out.println("Response data: " + result.getData());
    }

    @Test
    public void testFile() {
        String originalFilePath="D:\\图库\\car001.jpg";
        String newFilePath="D:\\图库\\removebg_test0.png";
        String url="https://api.remove.bg/v1.0/removebg";
        String removebg_apiKey="821jUTZLnFSf6eeqWdzdoKV9";

        HashMap<String,String> headParams=new HashMap<>();
        headParams.put("X-Api-Key",removebg_apiKey);

        HashMap<String,String> params=new HashMap<>();
        params.put("size","auto");
        HashMap<String,File> fileParams=new HashMap<>();
        fileParams.put("image_file",new File(originalFilePath));

        ApacheHttpRequester requester = new ApacheHttpRequester();
        //响应后的回调
        requester.setResponseSuccessHandler((responseCode, result) -> {
            System.out.println("对方响应结果：成功！开始写入响应返回的文件到本地");
            System.out.println("本地路径："+newFilePath);
        });
        requester.setResponseFailureHandler((responseCode, result) ->
                System.out.println("对方响应结果：失败")
        );

        //发起请求的方法
        System.out.println("源文件："+originalFilePath);
        System.out.println("开始发起请求");
        ResponseResult response = requester.setRequestForm(params)
                .setFiddlerProxy()
                .setRequestHeader(headParams)
                .setUploadFile(fileParams)
                .setDownloadFilePath(newFilePath)
                .setDownloadListener(DownloadListener.EMPTY_IMPL)
                .setUploadListener(FileUploadListener.EMPTY_IMPL)
                .postFile(url, ResponseDataMode.DOWNLOAD_FILE);
        System.out.println("响应码："+response.getResponseCode());
        System.out.println("错误信息："+response.getErrorMsg());
    }

}
