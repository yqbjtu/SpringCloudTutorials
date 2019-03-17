# restTemplate

http://127.0.0.1:9901/swagger-ui.html

https://127.0.0.1:9901/swagger-ui.html


    private static HttpClient httpClient = null;

    public static HttpClient getHttpsClient() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException {
        if (httpClient != null) {
            return httpClient;
        }
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{xtm}, null);
        SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", scsf).build();
        PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager(sfr);
        httpClient = HttpClientBuilder.create().setConnectionManager(pcm).build();
        return httpClient;
    }

    public static HttpClient createHttpsClient() throws KeyManagementException, NoSuchAlgorithmException {
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{xtm}, null);
        SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", scsf).build();
        PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager(sfr);
        return HttpClientBuilder.create().setConnectionManager(pcm).build();
}