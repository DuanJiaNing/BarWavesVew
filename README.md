
### 为媒体资源播放打造的“条形与波浪”可视化效果

## 截图
<table>
  <tr>
    <td><img width="300px" height="380px" src="https://raw.githubusercontent.com/DuanJiaNing/BarWavesVew/master/screenshort.gif"></td>
    <td><img width="700px" height="380px" src="https://raw.githubusercontent.com/DuanJiaNing/BarWavesVew/master/screenshort02.gif"></td>
  </tr>
</table>

## 说明

1. 提供的 xml 属性

- barColor  横条颜色
- barHeight  横条高度
- waveRange  波浪条极差（最高与最低的差值）
- waveMinHeight  
- waveColor  波浪条最小高度
- waveWidth  波浪条颜色
- waveNumber  波浪条宽度
- waveInterval  波浪条数量

## 如何使用
复制 library 下的 BarWavesView.java 文件到你的项目中，注意修改包名，同时复制 library 目录下的 attrs 中的属性到你自己的 attrs 中（如果没有 attrs
文件，则直接复制文件）。

在 xml 中使用：
```xml
<!--记得修改包名 --!>
<com.duan.library.BarWavesView
    android:id="@+id/BarWavesView_3"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="-230dp"
    app:waveNumber="35"
    app:waveWidth="30dp"
    app:waveRange="600dp"
    app:waveMinHeight="0dp"
    app:waveInterval="5dp"
    app:waveColor="#7eaeaeae"
    app:barHeight="0dp"
/>

```

在 java 中使用：
```java
//省略代码
public class MainActivity extends AppCompatActivity {
    private BarWavesView barWavesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        barWavesView = (BarWavesView) findViewById(R.id.BarWavesView);
        barWavesView.setBarColor(ColorUtils.getRandomColor()); // ColorUtils.getRandomColor() 获得一个随机颜色 
        
        int[][] cs = new int[barWavesView.getWaveNumber()][2];
        for (int i = 0; i < cs.length; i++) {
            // 控件允许给每一条波浪条单独设置颜色，这两个颜色将以纵向渐变的形式被绘制
            cs[i][0] = ColorUtils.getRandomColor(); 
            cs[i][1] = ColorUtils.getRandomColor();
        }
        barWavesView.setWaveColor(cs);
        
        // barWavesView.setWaveHeight(float[] hs); 修改控件波浪条高度
        
    }
}
//省略代码
```
详见 app 模块示例。

## 原理解析

参看博文：