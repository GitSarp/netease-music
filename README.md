# netease-music


## 依赖
基于[NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi)开发的网易云音乐小工具，请先部署运行此服务

## 功能
1. 能够获取自己的歌单
2. 获取自己喜欢的歌曲信息，支持vip歌曲分享
3. 数据返回格式支持APlayer
4. 网易云歌曲链接默认有效期为1200s，，客户端需要定时请求最新链接
5. 歌曲数据缓存在redis中,过期时间expire设置为有效期的一半。客户端应以不大于expire的时间间隔请求数据。

## 效果
<img width="403" alt="image" src="https://user-images.githubusercontent.com/14067824/161174028-aece52dd-9fb9-439d-b94e-b9edd5b263e8.png">

## APlayer动态刷新代码
因对APlayer api不太精深，如果有可以优化的地方，欢迎指正

``` javascript
        //最新播放器数据
        var audioUrl = [];
        //是否响应switch事件，app.list.add会触发此事件，所以需要屏蔽掉
        var canSwitch = true;
        
        //music player
        const ap = new APlayer({
            container: document.getElementById('aplayer'),
            fixed: true,
            audio: audioUrl
        });

        //获取最新播放器数据
        var gm = function getMusic(refresh){
                console.log('request for music...');
                $.ajax({
                    type: "GET",
                    url: "/netease/songs",
                    headers: {
                        appId: '',
                        appSec: ''
                    },
                    success: function(res) {
                        audioUrl = JSON.parse(res);
                        if(refresh){
                                console.log('init music');
                                canSwitch=false;
                                ap.list.add(audioUrl);

                                audioUrl=[];
                                canSwitch=true;
                        }
                    }
                });

        };
        //初始化播放器数据
        gm(true);
        
        //定时580s获取最新播放链接
        self.setInterval(gm, 580000,false);

        //当音乐切换时刷新播放器数据，实现无感
        ap.on('listswitch', (index) => {
                //console.log(canSwitch);
                //console.log(audioUrl);
                if(canSwitch && audioUrl != undefined && audioUrl.length != 0){
                        console.log('refresh music...');
                        ap.list.clear();
                        canSwitch=false;
                        ap.list.add(audioUrl);
                        ap.list.switch(index);
                        ap.play();
                        //清空播放链接，防止死循环
                        audioUrl=[];
                        canSwitch=true;
                }
        });
```
