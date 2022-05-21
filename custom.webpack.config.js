var path = require("path");
var merge = require('webpack-merge');
var generated = require('./scalajs.webpack.config');

var local = {
    devServer: {
        // the historyAPIFallback allows react-router to work
        historyApiFallback: true,
        proxy: {
            // when a request to /api is done, we want to apply a proxy
            '/api': {
                changeOrigin: true,
                cookieDomainRewrite: 'localhost',
                target: 'http://localhost:9000',
                pathRewrite: { '^/api': '/'},
                onProxyReq: (req) => {
                    if (req.getHeader('origin')) {
                        req.setHeader('origin', 'http://localhost:9000')
                    }
                }
            }
        }
    },
    resolve: {
        alias: {
            "js": path.resolve(__dirname, "../../../../src/main/js"),
        }
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: /\.(ttf|eot|woff|png|jpg|glb|svg)$/,
                use: 'file-loader'
            },
            {
                test: /\.(eot)$/,
                use: 'url-loader'
            }
        ]
    }
}

module.exports = merge(generated, local);
