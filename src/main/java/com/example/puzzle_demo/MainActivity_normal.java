package com.example.puzzle_demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MainActivity_normal extends AppCompatActivity implements View.OnClickListener {

    GridLayout gameView;
    ImageView card;
    ImageView reStart;
    ImageView help;
    Chronometer chronometer;
    TextView tvStep;

    int step;

    ImageView[][] cards=new ImageView[6][6];
    /**
     * null null null null null null
     * null Img  Img  Img  Img  null
     * null Img  Img  Img  Img  null
     * null Img  Img  Img  Img  null
     * null Img  Img  Img  Img  null
     * null null null null null null
     */
    int[] imageId=new int[16];//用于记录16张图片的资源ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_normal);
        initView();
    }

    void initView()
    {
        chronometer=findViewById(R.id.time);
        tvStep=findViewById(R.id.step);
        help=findViewById(R.id.help);
        reStart=findViewById(R.id.restart);
        gameView=findViewById(R.id.gameview);
        chronometer.setFormat("%s");  //设置时间显示格式

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity_normal.this, "游戏规则，移动拼图，使拼图按从小到大的顺序摆放，如123 456 78即为成功", Toast.LENGTH_SHORT).show();

            }
        });

        reStart.setOnClickListener(this);

        gameView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x=motionEvent.getX();
                float y=motionEvent.getY();



                int indexX=(int) x/card.getWidth()+1;
                int indexY=(int) y/card.getWidth()+1;

                //检查这个Card四周有没有可以交换的卡片
                exchangeCard(cards[indexY][indexX],cards[indexY-1][indexX],cards[indexY][indexX-1],
                        cards[indexY+1][indexX],cards[indexY][indexX+1]);

                return false;
            }
        });
        startGame();
    }

    /**
     * 开始或者重新开始的方法
     */
    void startGame()
    {
        gameView.removeAllViews();//移除全部图片
        chronometer.setBase(SystemClock.elapsedRealtime());//计时器清零
        step=0;                      //步数清零
        tvStep.setText(String.valueOf(step));//步数显示清零

        chronometer.start();//开始计时

        int k=0;
        GridLayout.LayoutParams params;  //添加图片所用的参数
        getRandomNum();                  //打乱16个资源ID，用于随机显示图片的位置
        for(int i=1;i<=4;i++)
        {
            for(int j=1;j<=4;j++)
            {
                card=new ImageView(this);  //初始化一个卡片
                card.setImageResource(imageId[k]);//将打乱后的资源ID挨个赋予他们
                card.setTag(imageId[k]);//因为ImageView没有getImageResourceId这样的方法，我们可以把它的资源ID放在这个ImageView的标记中方便提取判断
                k++;                    //下一位资源
                GridLayout.Spec rowSpec=GridLayout.spec(i-1);//设置该卡片显示的行数
                GridLayout.Spec columnSpec=GridLayout.spec(j-1);//设置列数
                params=new GridLayout.LayoutParams(rowSpec,columnSpec);//初始化参数
                params.width=200;    //参数长宽设置为包含组件
                params.height= 200;
                gameView.addView(card,params);                       //将卡片加入布局
                cards[i][j]=card;                                   //把这个卡片记录在二维数组中方便操作
            }
        }
    }

    /**
     * 打乱资源ID排布的方法
     */
    private void getRandomNum()
    {
        int tempNum[]={               //15个资源ID
                R.drawable.chr0,
                R.drawable.chr1,
                R.drawable.chr2,
                R.drawable.chr3,
                R.drawable.chr4,
                R.drawable.chr5,
                R.drawable.chr6,
                R.drawable.chr7,
                R.drawable.chr8,
                R.drawable.chr9,
                R.drawable.chr10,
                R.drawable.chr11,
                R.drawable.chr12,
                R.drawable.chr13,
                R.drawable.chr14,
                R.drawable.chr15,

        };
        int last=15;
        Random r=new Random();
        int temp;
        for(int i=0;i<15;i++)
        {
            temp=r.nextInt(16)%last;
            imageId[i]=tempNum[temp];
            tempNum[temp]=tempNum[last];
            tempNum[last]=imageId[i];
            last--;
        }
        imageId[15]=tempNum[0];
        Collections.shuffle(Collections.singletonList(tempNum));
    }

    /**
     * 交换卡片  上左下右遍历
     *
     * @param card  点击的卡片
     * @param top   上面的卡片
     * @param left  左边的卡片
     * @param bottom 下面的卡片
     * @param rigth  右边的卡片
     */
    public  void exchangeCard(ImageView card, ImageView top, ImageView left, ImageView bottom, ImageView rigth)
    {
        if(top!=null)
        {
            if((int)top.getTag()==R.drawable.chr0) //Tag表示的就是这张卡片的资源ID，取出来用于判断
                Change(card,top);
        }
        if(left!=null)
        {
            if((int)left.getTag()==R.drawable.chr0)
                Change(card,left);
        }
        if(bottom!=null)
        {
            if((int)bottom.getTag()==R.drawable.chr0)
                Change(card,bottom);
        }
        if(rigth!=null)
        {
            if((int)rigth.getTag()==R.drawable.chr0)
//            if(rigth.getTag().equals(R.drawable.chr0))
                Change(card,rigth);
        }
    }

    /**
     * 交换两张卡片
     * @param card1 卡片1
     * @param card2  卡片2
     *
     * 提取出两张图片的资源ID，互相交换他们的ID，实现交换的效果
     */
    void Change(ImageView card1,ImageView card2)
    {
        int temp;
        int temp1;
        int temp2;
        temp1=(int)card1.getTag();//取出要交换两张卡片的资源的ID
        temp2=(int)card2.getTag();

        //交换操作，实则交换他们的资源ID，让它们显示的图片不同
        temp=temp2;
        card2.setImageResource(temp1);
        card2.setTag(temp1);
        card1.setImageResource(temp);
        card1.setTag(temp);

        gameView.invalidate();//必须调用该重绘方法，显示才会有改变的效果

        step++;//步数++
        tvStep.setText(String.valueOf(step));//显示的步数++

        if(IsOver()){              //如果游戏结束就显示游戏结束对话框
            GameOverDialog();
        }
    }

    public boolean IsOver()
    {
        int result[]={    //result存放的就是标准结局的资源ID排布，123456780
                R.drawable.chr1,
                R.drawable.chr2,
                R.drawable.chr3,
                R.drawable.chr4,
                R.drawable.chr5,
                R.drawable.chr6,
                R.drawable.chr7,
                R.drawable.chr8,
                R.drawable.chr9,
                R.drawable.chr10,
                R.drawable.chr11,
                R.drawable.chr12,
                R.drawable.chr13,
                R.drawable.chr14,
                R.drawable.chr15,
                R.drawable.chr0
        };
        int num[] = new int[16];
        //挨个取出资源ID
        int k=0;
        for(int i=1;i<=4;i++)
        {
            for(int j=1;j<=4;j++)
            {
                num[k]=(int)cards[i][j].getTag();
                k++;
            }
        }
//        Log.i("Result", Arrays.equals(num,result)+"");
        return Arrays.equals(num,result);//返回判断结果，true为结束，false为未结束
    }

    /**
     * 游戏结束对话框
     */
    public void GameOverDialog()
    {
        chronometer.stop();
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("游戏结束");
        builder.setMessage("共耗时"+chronometer.getText().toString()+" ! 走了"+step+"步 !");
        builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startGame();
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 是否重新开始对话框
     */
    public void ReStartDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("重新开始");
        builder.setMessage("确认重新开始本局游戏吗");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startGame();
            }
        });
        builder.setNegativeButton("取消",null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    /**
     * 是否退出游戏对话框
     */
    public void ExictDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("确认退出");
        builder.setMessage("确认退出游戏吗");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("取消",null);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.restart:
                ReStartDialog();  //重开对话框
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)   //点击反回提示是否退出
        {
            ExictDialog();
        }
        return super.onKeyDown(keyCode, event);
    }
}