const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8080
  }
},
{
  module: {
    rules: [
      {
        test: /\.m?js$/,
        exclude: /node_modules/,
     
        pinia:"/node_modules/@pinia/nuxt/node_modules/pinia/dist/pinia.mjs",
     
        use: {
          loader: "babel-loader",
          options: {
            presets: ['@babel/preset-env']
          }
        }
      }
    ]
  }
})