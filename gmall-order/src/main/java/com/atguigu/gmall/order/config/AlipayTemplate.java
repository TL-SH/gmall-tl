package com.atguigu.gmall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gmall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2016101200668799";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCEYtmBDsEPHguzP87nrjtGSxuBQ2nuMbYMQBiBeOTKrDOp0DiYaBarOPlkCMI3lMVf++6ErQcEZII0+ioTtsl9PXfIOXNGpz5B9PszRmi+a3qe0lnSx5jGt175HAnfAbL72P3Pzh/bA6KkdUbgQNY0GxvTFDJikiarpcTbkxIIipGLOsnfvPbcCCJMwdyAmqJIhgCesn0ezuNbcDA0srpwMNPM4yJwFewAGgFzbf0FOs9svsCqs6qnDQLUe326FJcDVD549NW8jn+UbXuq8LdttduxEda4NG6JJKOrvbIGqXkKHlbtPM6FzsxU+Ip0VSo/WuH05eOt8m1W5R+jf5uPAgMBAAECggEAYqPAik+y+4+YiiuN/8QrODn4OX1AbZEuorwTRk0MtBJQwEN8F9reoR9neuinxYAc/GKbmtHNe7Fj7xNCzMOSJ7XoDRD5Wnh8g+qETPU2UEOG7tWuxcWgiNfSdOw2UqpScSKCIxbjV23C/qS/dZH5yIM2XTUcQaPCGnz+S9iuRBvbzLzh+qfHWelL937y4Z/DVd/DxYOyfJLXNDR/ow11lOVVMYB1S4RTCN+quj4slx96EbKRIDgkJfXxI8hWWjp1TBTauTuWpPo3lu43Rtr97QUjPSG4iK7pDKsj/rFC0XbpbDtU66OcPeiCX4M46PW8dvtmIlZWqyGVI8csmK1lUQKBgQDfbOYcKLMjKPxbWWgVXaT/7kqk+4aFiLOwgIRzsCrQnhoHDOzQUrFzWpR0obxAxLncmiUgjzP0MlSCtO+xaSuGpHtlfrm60Xk/CfhJEK7dBak1ryhhnzB1zwrbltNURJJ+rZ0WZigs5zPTOvUj9L184gL0zLGOfpAf0aMrB9j0CQKBgQCXsANUwZfLthqxYjyciaRyrFXTBmaS5r7qMrhBUyqWR0bWRsbYWIJ7aznRZyUP9AuyiY6orMY9NeClxt2Q12N7AGa+eHcSAMGsDKsnMxkGcFENW6zq/uvAl3pPPx3p7MwSnNvWcGtey4ZnrC2kjxtruU8yaFWWG0xjL6pwH69o1wKBgDBVvMpmu6plmYKhqvTEV4fOo3Nhvs5wrn8GrqDggcK0EWlpEGpZGW6dL/SzcKiKZrMk3ddXY6P2p3XHcvtshzAPsWpZqhCSdKqAm6gi5dGHAf5NMG0+JRu3eN/cl0dptk+Ve6B25UYhloKiBJqMvdmgg4c2KUtscf2QOr56aiMRAoGAG/KvOm+M81izJFmwRztJFGPSUFZOkeR36pi7wDV9lEC/uX1C59uu9uRzu7RgbMcPXK7hnQxwXhjOMl0ot6YnyyLwy0lQzv1ascOHyWotwzTwiiCJBKdBcrO2MmGztkUN+KP0EVeXQNkJA5MCud4wNDHpeKUml8ih02iV46DC5fMCgYB5tJGc5OqVXTY9QEQiyobM8MiohRXZe6q8utgi6iQBBX4xPI8Gvi9CrzzUZq9VcPaSydukLycCxNvzBdo3nVysBz/r8RSXWE84GgaanaBMr0OAEzchXriUFcA81Sis/mn3yLkUCnbrDRFfhZdDk+LYafC+bC/VJhHd+xrydGYqQA==";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAksKUn3PcQ9yss4X0xjTQ17HVjfcTlsKF1+cZuNUXUG0NXKC6u7ZS/GyykI6PZdctJAMEGu4dT3P8Na/+tQKefbEcnh3nZlDQgzgUCGSw/pwHRu2m62OjKj1aSma0Divm/bJNtEW27r7r1kYsTeL6cdTWZ151IKvxfEjYBifHb5dWgF+DpjdykxmPG8paEtibi6Ejy32ofv3JF+ItWIdgDAIw4Cv4+1Fl3YNNUNVxj5oJwB6MmaE/2V28lljClSEoqxs65GNDmek97lgYKZ+bKbBNLemXkL4xWmwyxBG7OPQAs62KsuEMSy8PmtOBqtK53Z3uIjREJ9v4Gs4x3BjqBwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "http://2im0cfyd6z.52http.net/api/order/pay/success";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = null;

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
