# netease-music


## 依赖
基于[NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi)开发的网易云音乐工具，请先部署运行此服务

## 目前能力
1. 能够获取自己的歌单
2. 获取自己的歌单歌曲信息，数据返回支持APlayer
3. 歌曲播放链接会失效，定时获取歌曲播放链接

## APlayer动态刷新代码
因前端js代码不太精深，如果有可以优化的地方，欢迎指正

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
