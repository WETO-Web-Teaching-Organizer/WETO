// vue.config.js
module.exports = {
  // proxy all webpack dev-server requests starting with /api
  // to Weto backend (localhost:8080) using http-proxy-middleware
  // see https://cli.vuejs.org/config/#devserver-proxy
  devServer: {
    port: 4545,
    proxy: {
      'weto5': {
        target: 'http://localhost:8080/', // this configuration needs to correspond to the Weto backends' application.properties server.port
        ws: true,
        changeOrigin: true
      }
    }
  },
  // Change build paths to make them Maven compatible
  // see https://cli.vuejs.org/config/
  outputDir: 'target/dist',
  assetsDir: 'static'
};