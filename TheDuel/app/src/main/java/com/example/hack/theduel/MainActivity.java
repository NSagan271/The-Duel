    package com.example.hack.theduel;

    import android.support.v4.content.res.ResourcesCompat;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.Button;
    import android.widget.ImageView;
    import android.widget.TextView;

    public class MainActivity extends AppCompatActivity {
        Button shoot;
        Button block;
        Button load;
        Button next;
        TextView bullets;
        TextView round;
        ImageView youImg;
        ImageView them;
        TextView over;
        TextView yText;
        TextView pText;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //buttons
            shoot = (Button) findViewById(R.id.shoot);
            block = (Button) findViewById(R.id.block);
            load = (Button) findViewById(R.id.load);
            next = (Button) findViewById(R.id.next);
            setButtons(false, false, false);

            //text
            bullets = (TextView) findViewById(R.id.bullets);
            round = (TextView) findViewById(R.id.round);
            over = (TextView) findViewById(R.id.over);
            yText = (TextView) findViewById(R.id.youText);
            pText = (TextView) findViewById(R.id.oText);
            //images
            youImg = (ImageView) findViewById(R.id.you);
            them = (ImageView) findViewById(R.id.them);
            instr();
        }
        void nextVisible(){
            next.setVisibility(View.VISIBLE);
            shoot.setVisibility(View.INVISIBLE);
            block.setVisibility(View.INVISIBLE);
            load.setVisibility(View.INVISIBLE);
        }
        void nextInvis(){
            next.setVisibility(View.INVISIBLE);
            shoot.setVisibility(View.VISIBLE);
            block.setVisibility(View.VISIBLE);
            load.setVisibility(View.VISIBLE);
        }
        void startGame(){

            nextVisible();
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextInvis();
                    youImg.setVisibility(View.VISIBLE);
                    them.setVisibility(View.VISIBLE);
                    yText.setVisibility(View.VISIBLE);
                    pText.setVisibility(View.VISIBLE);
                    game();
                }
            });
        }
        void nextTurn(final Player you, final Player computer, final int r){
            if(you.dead||computer.dead){
                them.setScaleX(1);
                if (you.dead)setImg(youImg,R.drawable.x);

                else setImg(youImg,R.drawable.check);
                if (computer.dead)setImg(them,R.drawable.x);
                else setImg(them,R.drawable.check);
                over.setVisibility(View.VISIBLE);
                round.setVisibility(View.INVISIBLE);

                retry();
            }
            else{
                nextVisible();
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextInvis();
                        turn(you, computer, r);
                    }
                });
            }

        }
        void retry(){
            nextVisible();
            next.setText(R.string.retry);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextInvis();
                    them.setScaleX(-1);
                    over.setVisibility(View.INVISIBLE);
                    round.setVisibility(View.VISIBLE);
                    game();
                }
            });
        }

        void instr() {
            yText.setVisibility(View.INVISIBLE);
            pText.setVisibility(View.INVISIBLE);
            setText(round, getString(R.string.instr));
            next.setText(R.string.go);
            youImg.setVisibility(View.INVISIBLE);
            them.setVisibility(View.INVISIBLE);
            next.setVisibility(View.VISIBLE);
            startGame();
        }

        void game() {
            Player you = new Player();
            Player computer = new Player();
            int r = 0;
            next.setText(R.string.next);

            turn(you, computer, r);


        }
        void turn(Player you, Player computer, int r){

            you.newTurn();
            computer.newTurn();
            r++;
            setImg(youImg, R.drawable.nothing);
            setImg(them, R.drawable.nothing);
            setText(round, "Round: " + r);
            setText(bullets, "Bullets: " + you.bullets);
            setButtons((you.bullets != 0), true, (you.bullets < 3));
            setAction(you, computer, r);
        }

        void setText(TextView t, String str) {
            t.setText(str);
        }

        void setImg(ImageView i, int d) {
            i.setImageDrawable(ResourcesCompat.getDrawable(getResources(), d, null));
        }

        void setButtons(boolean s, boolean b, boolean l) {
            shoot.setEnabled(s);
            block.setEnabled(b);
            load.setEnabled(l);
        }

        void setAction(Player you, Player p, int r) {
            int pAct = getOpponentResp(p, you);
            getResp(you, p, pAct,r);

        }
        void handleResp(Player you, Player p, int pAct,int yAct, int r){
            setImg(youImg, yAct);
            setImg(them,pAct);
            if (yAct == R.drawable.gun) {
                if (you.shoot()) {
                    boolean x = p.killed();
                    //if (x)System.out.println("Your opponent died!");
                    //else System.out.println("You opponent blocked your shot.");
                }
                //else System.out.println("You tried to shoot when your gun had no bullets.");
            }
            if (pAct == R.drawable.gun && p.shoot()) {
                boolean x = you.killed();
                //if (x) System.out.println("You were shot dead!");
                //else System.out.println ("You blocked your opponent's shot.");
            }
            setText(bullets, "Bullets: " + you.bullets);
            setButtons(false, false, false);
            next.setVisibility(View.VISIBLE);
            nextTurn(you, p, r);
        }
        void getResp(final Player you, final Player p, final int pAct, final int r){

            shoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleResp(you, p, pAct,R.drawable.gun,r);

                }
            });
            load.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    you.bullets++;
                    handleResp(you, p, pAct,R.drawable.load,r);
                }
            });
            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    you.shield = true;
                    handleResp(you, p, pAct,R.drawable.shield,r);
                }
            });
        }
        int getOpponentResp(Player p, Player y){
            if (p.bullets==0&&y.bullets==0){
                p.bullets++;
                return R.drawable.load;
            }
            int s;
            if (p.bullets==3)s = (int)(Math.random()*2);
            else if (p.bullets==0) s = (int)(Math.random()*2)+1;
            else s = (int)(Math.random()*3);
            if (s==0)return R.drawable.gun;
            if (s==1){
                p.shield = true;
                return R.drawable.shield;
            }
            if (s==2){
                p.bullets++;
                return R.drawable.load;
            }
            return R.drawable.shield;
        }

    }
    class Player{
        int bullets = (int)(Math.random()*3);
        boolean shield = false;
        boolean dead = false;
        boolean killed(){
            if (!shield){
                dead = true;
                return true;
            }
            return false;
        }
        boolean shoot(){
            boolean b = (bullets!=0);
            if (b)bullets--;
            return b;
        }
        void newTurn(){
            shield = false;
        }
    }